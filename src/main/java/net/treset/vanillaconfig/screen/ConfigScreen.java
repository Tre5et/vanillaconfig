package net.treset.vanillaconfig.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.treset.vanillaconfig.config.*;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.screen.widgets.*;
import net.treset.vanillaconfig.screen.widgets.base.GuiBaseWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    Screen parent;

    PageConfig config;
    List<GuiBaseWidget> widgets;

    boolean active = false;

    double scrollOffset = 0;

    private boolean scrolling = false;

    private int top = 32;
    private int bottom = this.height - 32;
    private int left = 0;
    private int right = this.width;

    private String[] renderTooltip = new String[]{};

    public ConfigScreen(PageConfig config, Screen parent) {
        super(new LiteralText(config.getName()));

        this.shouldCloseOnEsc();
        this.config = config;

        this.parent = parent;

        bottom = this.height - 32;
        right = this.width;
    }

    protected void init() {
        if(MinecraftClient.getInstance() == null) return;

        bottom = this.height - 32;
        right = this.width;

        this.widgets = new ArrayList<>();
        addOptions(config.getOptions());

        this.active = true;
        this.onOpen.run();
    }

    public boolean isActive() { return this.active; }

    public boolean isMouseOverOptions(int mouseX, int mouseY) { return mouseX >= this.left && mouseX <= this.right && mouseY >= this.top && mouseY <= this.bottom; }

    public void addOptions(BaseConfig[] options) {
        for (BaseConfig e : options) {

            if(e.getType() == ConfigType.BOOLEAN) this.widgets.add(new GuiBooleanWidget(0, (BooleanConfig)e, this));
            else if(e.getType() == ConfigType.LIST) this.widgets.add(new GuiListWidget(0, (ListConfig)e, this));
            else if(e.getType() == ConfigType.PAGE) this.widgets.add(new GuiPageWidget(0, (PageConfig)e, this));
            else if(e.getType() == ConfigType.INTEGER) this.widgets.add(new GuiIntegerWidget(0, (IntegerConfig)e, this));
            else if(e.getType() == ConfigType.STRING) this.widgets.add(new GuiStringWidget(0, (StringConfig)e, this));
            else if(e.getType() == ConfigType.DOUBLE) this.widgets.add(new GuiDoubleWidget(0, (DoubleConfig)e, this));
            else if(e.getType() == ConfigType.KEYBIND) this.widgets.add(new GuiKeybindWidget(0, (KeybindConfig)e, this));
        }
    }

    public PageConfig getConfig() { return this.config; }
    public GuiBaseWidget[] getWidgets() { return this.widgets.toArray(new GuiBaseWidget[]{}); }

    private int getOptionsHeight() {
        int height = 3;
        for (GuiBaseWidget e : widgets) {
            if(e.isRendered()) height += e.getHeight() + 3;
        }
        return height - 3;
    }
    private int getHeightOfOption(int index) {
        if(index >= this.getWidgets().length || index < 0) return -1;
        int height = 3;
        for(int i = 0; i < index; i++) {
            GuiBaseWidget e = this.getWidgets()[i];
            if(e.isRendered()) height += e.getHeight() + 3;
        }
        return height;
    }
    private int getHeightOfOption(GuiBaseWidget widget) {
        if(this.widgets.contains(widget)) return this.getHeightOfOption(this.widgets.indexOf(widget));
        return -1;
    }
    public double getScrollOffset() { return this.scrollOffset; }
    public double getScrollHeight() { return Math.max(0, this.getOptionsHeight() - (this.getDisplayAreaHeight() - 4)); }

    public double getAverageWidgetHeight() { return ((double)this.getOptionsHeight() - 3) / (double)this.getWidgets().length - 3; }

    public int getDisplayAreaHeight() { return this.bottom - this.top; }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        this.renderBackground(t, b);
        this.renderOptions(matrices, mouseX, mouseY);
        this.renderOverlay(matrices, t, b);
        this.renderScrollbar(t, b);
        this.renderDoneButton(matrices, mouseX, mouseY);
        this.renderTooltip(matrices, mouseX, mouseY);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        super.render(matrices, mouseX, mouseY, delta);
    }
    
    private void renderBackground(Tessellator t, BufferBuilder b) {
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        b.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        b.vertex(this.left, this.bottom, 0.0D).texture((float)this.left / 32.0F, (float)(this.bottom + (int)this.getScrollOffset()) / 32.0F).color(32, 32, 32, 255).next();
        b.vertex(this.right, this.bottom, 0.0D).texture((float)this.right / 32.0F, (float)(this.bottom + (int)this.getScrollOffset()) / 32.0F).color(32, 32, 32, 255).next();
        b.vertex(this.right, this.top, 0.0D).texture((float)this.right / 32.0F, (float)(this.top + (int)this.getScrollOffset()) / 32.0F).color(32, 32, 32, 255).next();
        b.vertex(this.left, this.top, 0.0D).texture((float)this.left / 32.0F, (float)(this.top + (int)this.getScrollOffset()) / 32.0F).color(32, 32, 32, 255).next();
        t.draw();
    }

    private void renderScrollbar(Tessellator t, BufferBuilder b) {
        if(this.getScrollHeight() == 0) return;
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int scrollBarHeight = (int)((float)(this.getDisplayAreaHeight() * this.getDisplayAreaHeight()) / (float)this.getOptionsHeight());
        scrollBarHeight = MathHelper.clamp(scrollBarHeight, 32, this.getDisplayAreaHeight() - 8);

        int scrollBarY = (int)this.getScrollOffset() * (this.getDisplayAreaHeight() - scrollBarHeight) / (int)this.getScrollHeight() + this.top;
        if (scrollBarY < this.top) {
            scrollBarY = this.top;
        }

        int scrollAreaL = this.width / 2 + 150;
        int scrollAreaR = scrollAreaL + 6;

        b.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        //draw scroll background
        b.vertex(scrollAreaL, this.bottom, 0.0D).color(0, 0, 0, 255).next();
        b.vertex(scrollAreaR, this.bottom, 0.0D).color(0, 0, 0, 255).next();
        b.vertex(scrollAreaR, this.top, 0.0D).color(0, 0, 0, 255).next();
        b.vertex(scrollAreaL, this.top, 0.0D).color(0, 0, 0, 255).next();
        //draw scroll bar
        b.vertex(scrollAreaL, scrollBarY + scrollBarHeight, 0.0D).color(128, 128, 128, 255).next();
        b.vertex(scrollAreaR, scrollBarY + scrollBarHeight, 0.0D).color(128, 128, 128, 255).next();
        b.vertex(scrollAreaR, scrollBarY, 0.0D).color(128, 128, 128, 255).next();
        b.vertex(scrollAreaL, scrollBarY, 0.0D).color(128, 128, 128, 255).next();
        //draw white bit
        b.vertex(scrollAreaL, scrollBarY + scrollBarHeight - 1, 0.0D).color(192, 192, 192, 255).next();
        b.vertex((scrollAreaR - 1), scrollBarY + scrollBarHeight - 1, 0.0D).color(192, 192, 192, 255).next();
        b.vertex((scrollAreaR - 1), scrollBarY, 0.0D).color(192, 192, 192, 255).next();
        b.vertex(scrollAreaL, scrollBarY, 0.0D).color(192, 192, 192, 255).next();

        t.draw();
    }

    private void renderOptions(MatrixStack matrices, int mouseX, int mouseY) {
        if(!this.isMouseOverOptions(mouseX, mouseY)) mouseX = mouseY = -1;
        for (GuiBaseWidget e : widgets) {
            e.render(matrices, this.getHeightOfOption(e) + this.top, mouseX, mouseY, (int)this.getScrollOffset());
        }
    }

    private void renderOverlay(MatrixStack matrices, Tessellator t, BufferBuilder b) {
        this.renderBars(t, b);

        drawCenteredText(matrices, this.textRenderer, this.getConfig().getName(), this.width / 2, 5, 16777215);
    }
    
    private void renderBars(Tessellator t, BufferBuilder b) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);

        b.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        //draw top bar
        b.vertex(this.left, this.top, -100.0D).texture(0.0F, (float)this.top / 32.0F).color(64, 64, 64, 255).next();
        b.vertex(this.left + this.width, this.top, -100.0D).texture((float)this.width / 32.0F, (float)this.top / 32.0F).color(64, 64, 64, 255).next();
        b.vertex(this.left + this.width, 0.0D, -100.0D).texture((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
        b.vertex(this.left, 0.0D, -100.0D).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
        //draw bottom bar
        b.vertex(this.left, this.height, -100.0D).texture(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).next();
        b.vertex(this.left + this.width, this.height, -100.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).next();
        b.vertex(this.left + this.width, this.bottom, -100.0D).texture((float)this.width / 32.0F, (float)this.bottom / 32.0F).color(64, 64, 64, 255).next();
        b.vertex(this.left, this.bottom, -100.0D).texture(0.0F, (float)this.bottom / 32.0F).color(64, 64, 64, 255).next();

        t.draw();

        this.renderShadows(t, b);
    }

    private void renderShadows(Tessellator t, BufferBuilder b) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        b.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        //draw top shadow
        b.vertex(this.left, this.top + 4, 0.0D).color(0, 0, 0, 0).next();
        b.vertex(this.right, this.top + 4, 0.0D).color(0, 0, 0, 0).next();
        b.vertex(this.right, this.top, 0.0D).color(0, 0, 0, 255).next();
        b.vertex(this.left, this.top, 0.0D).color(0, 0, 0, 255).next();
        //draw bottom shadow
        b.vertex(this.left, this.bottom, 0.0D).color(0, 0, 0, 255).next();
        b.vertex(this.right, this.bottom, 0.0D).color(0, 0, 0, 255).next();
        b.vertex(this.right, this.bottom - 4, 0.0D).color(0, 0, 0, 0).next();
        b.vertex(this.left, this.bottom - 4, 0.0D).color(0, 0, 0, 0).next();
        t.draw();
    }

    private void renderDoneButton(MatrixStack matrices, int mouseX, int mouseY) {
        final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        int texY = (this.isHoveredOverDone(mouseX, mouseY)) ? 86 : 66;
        DrawableHelper drawHelper = new DrawableHelper() {};
        drawHelper.drawTexture(matrices, this.width / 2 - 100, this.bottom + 5, 0, texY, 200, 20);
        DrawableHelper.drawCenteredText(matrices, textRenderer, new TranslatableText("gui.done"), this.width / 2, this.bottom + 11, Formatting.WHITE.getColorValue());
    }

    private boolean isHoveredOverDone(int mouseX, int mouseY) {
        return this.width / 2 - 100 <= mouseX && mouseX <= this.width / 2 + 100 && this.bottom + 5 <= mouseY && mouseY <= this.bottom + 25;
    }

    private void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if(this.renderTooltip != null && this.renderTooltip.length > 0 && !this.renderTooltip[0].isEmpty()) {
            List<Text> texts = new ArrayList<>();
            for (String s : this.renderTooltip) {
                texts.add(new TranslatableText(s));
            }
            this.renderTooltip(matrices, texts, mouseX, mouseY);
            this.renderTooltip = null;
        }
    }

    public boolean requestTooltip(String[] tooltip) {
        if(!(this.renderTooltip == null || this.renderTooltip.length < 1 || this.renderTooltip[0].isEmpty())) return false;
        this.renderTooltip = tooltip;
        return true;
    }

    private void scrollKey(int key) {
        if(key == GLFW.GLFW_KEY_DOWN) scroll(this.getAverageWidgetHeight() * 1.5);
        else if(key == GLFW.GLFW_KEY_UP) scroll(-this.getAverageWidgetHeight() * 1.5);
        else if(key == GLFW.GLFW_KEY_PAGE_DOWN) scroll(this.bottom - this.top - this.getAverageWidgetHeight());
        else if(key == GLFW.GLFW_KEY_PAGE_UP) scroll(-this.bottom + this.top + this.getAverageWidgetHeight());
    }
    private void scroll(double amount) {
        if(this.getOptionsHeight() + this.top <= MinecraftClient.getInstance().getWindow().getScaledHeight() + 35) return;

        this.setScroll(this.getScrollOffset() + amount);
    }
    private void setScroll(double scroll) {
        this.scrollOffset = scroll;

        if(this.scrollOffset < 0) this.scrollOffset = 0;
        else if(this.scrollOffset > this.getOptionsHeight() + this.top - MinecraftClient.getInstance().getWindow().getScaledHeight() + 35) this.scrollOffset = this.getOptionsHeight() + this.top - MinecraftClient.getInstance().getWindow().getScaledHeight() + 35;
    }

    boolean ioInterruptRequest = false;
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.ioInterruptRequest = false;
        if(button == 0 && this.getScrollHeight() != 0 && mouseX >= this.width / 2 + 150 && mouseX <= this.width / 2 + 156) this.scrolling = true;
        else if(button == 0 && this.isHoveredOverDone((int)mouseX, (int)mouseY)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.close();
        }
        else {
            for (GuiBaseWidget e : this.getWidgets()) {
                e.onMouseDown(button);
            }
        }
        if(ioInterruptRequest) {
            ioInterruptRequest = false;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.ioInterruptRequest = false;
        if(this.scrolling) this.scrolling = false;
        else {
            for (GuiBaseWidget e : this.getWidgets()) {
                e.onMouseUp(button);
            }
        }
        if(ioInterruptRequest) {
            ioInterruptRequest = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        else if(this.scrolling && button == 0 && this.getScrollHeight() != 0) {
            if(mouseY < this.top) this.setScroll(0);
            else if(mouseY > this.bottom) this.setScroll(this.getScrollHeight());
            else {
                int scrollBarHeight = MathHelper.clamp((int) ((float)(this.getDisplayAreaHeight()^2) / (float)this.getOptionsHeight()), 32, this.getDisplayAreaHeight() - 8);
                double scrollPerScrollbarPixel = this.getScrollHeight() / (double)(this.getDisplayAreaHeight() - scrollBarHeight);
                this.setScroll(this.getScrollOffset() + deltaY * scrollPerScrollbarPixel);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.scroll(-amount * this.getAverageWidgetHeight() / 2D);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        this.ioInterruptRequest = false;
        for (GuiBaseWidget e : this.getWidgets()) {
            e.onKeyDown(key, scancode);
        }
        if(ioInterruptRequest) {
            ioInterruptRequest = false;
            return true;
        }
        this.scrollKey(key);
        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        this.ioInterruptRequest = false;
        for (GuiBaseWidget e : this.getWidgets()) {
            e.onKeyUp(key, scancode);
        }
        if(ioInterruptRequest) {
            ioInterruptRequest = false;
            return true;
        }
        return super.keyReleased(key, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.ioInterruptRequest = false;
        if(this.isActive()) {
            for (GuiBaseWidget e : this.getWidgets()) {
                e.onTextReceived(String.valueOf((char)chr));
            }
        }
        if(ioInterruptRequest) {
            ioInterruptRequest = false;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    public void requestIoInterrupt() {
        this.ioInterruptRequest = true;
    }

    @Override
    public void close() {
        for (GuiBaseWidget e : widgets) {
            e.onClose();
        }
        this.onClose.run();
        this.active = false;
        this.client.setScreen(parent);
    }

    Runnable onOpen = () -> {};
    public void onOpen(Runnable method) {
        this.onOpen = method;
    }
    Runnable onClose = () -> {};
    public void onClose(Runnable method) {
        this.onClose = method;
    }
}
