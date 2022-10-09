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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.treset.vanillaconfig.config.*;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.screen.widgets.*;
import net.treset.vanillaconfig.screen.widgets.base.GuiBaseWidget;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigScreen extends Screen {

    Screen parent;

    PageConfig config;
    List<GuiBaseWidget> widgets;

    boolean active = false;
    boolean textureBackground = true;

    double scrollOffset = 0;

    int displayedIndexes = 0;

    private boolean scrolling = false;

    private final int marginY = 32;
    private final int top = this.marginY;
    private int bottom = this.height - this.marginY;

    private final int marginX = 0;
    private final int left = this.marginX;
    private int right = this.width - this.marginX;

    private final int scrollbarCenterX = 156;
    private int scrollbarX = this.width / 2 + this.scrollbarCenterX;

    private int scrollbarHeight = 0;

    private String[] renderTooltip = new String[]{};

    private int currentSelected = -2;

    public ConfigScreen(PageConfig config, Screen parent) {
        super(Text.literal(config.getName()));

        this.shouldCloseOnEsc();
        this.config = config;

        this.parent = parent;
    }

    @Override
    protected void init() {
        if(MinecraftClient.getInstance() == null) return;

        this.widgets = new ArrayList<>();
        this.addOptions(config.getOptions());

        this.bottom = this.height - this.marginY;
        this.right = this.width - this.marginX;
        this.scrollbarX = this.width / 2 + this.scrollbarCenterX;

        this.setScroll(0);

        this.active = true;
        this.onOpenDep.run();
        this.onOpen.accept(this.getConfig().getKey());
    }

    public boolean isActive() { return this.active; }

    public boolean isBackgroundTextured() { return this.textureBackground; }
    public boolean setBackgroundTextured(boolean textured) { this.textureBackground = textured; return true; }

    public boolean isMouseOverOptions(int mouseX, int mouseY) { return mouseX >= this.left && mouseX <= this.right && mouseY >= this.top && mouseY <= this.bottom; }

    public void addOptions(BaseConfig[] options) {
        for (BaseConfig e : options) {

            if(e.getType() == ConfigType.BOOLEAN) this.widgets.add(new GuiBooleanWidget((BooleanConfig)e, this));
            else if(e.getType() == ConfigType.BUTTON) this.widgets.add(new GuiButtonWidget((ButtonConfig)e, this));
            else if(e.getType() == ConfigType.LIST) this.widgets.add(new GuiListWidget((ListConfig)e, this));
            else if(e.getType() == ConfigType.PAGE) this.widgets.add(new GuiPageWidget((PageConfig)e, this));
            else if(e.getType() == ConfigType.INTEGER) this.widgets.add(new GuiIntegerWidget((IntegerConfig)e, this));
            else if(e.getType() == ConfigType.STRING) this.widgets.add(new GuiStringWidget((StringConfig)e, this));
            else if(e.getType() == ConfigType.DOUBLE) this.widgets.add(new GuiDoubleWidget((DoubleConfig)e, this));
            else if(e.getType() == ConfigType.KEYBIND) this.widgets.add(new GuiKeybindWidget((KeybindConfig)e, this));
        }
    }

    public PageConfig getConfig() { return this.config; }
    public GuiBaseWidget[] getWidgets() { return this.widgets.toArray(new GuiBaseWidget[]{}); }

    private int getOptionsHeight() {
        return (int)Math.ceil((double)this.displayedIndexes / 2) * (int)this.getAverageWidgetHeight() + 5;
    }
    public double getScrollOffset() { return this.scrollOffset; }
    public double getScrollHeight() { return Math.max(0, this.getOptionsHeight() - (this.getDisplayAreaHeight())); }

    public double getAverageWidgetHeight() { return 25; }

    public int getDisplayAreaHeight() { return this.bottom - this.top; }

    public int getTop() { return this.top; }
    public int getBottom() { return this.bottom; }
    public int getLeft() { return this.left; }
    public int getRight() { return this.right; }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if(this.isBackgroundTextured()) this.renderBackground(t, b);
        this.renderOptions(matrices, mouseX, mouseY);
        if(this.isBackgroundTextured()) this.renderOverlay(matrices, t, b);
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
        scrollBarHeight = MathHelper.clamp(scrollBarHeight, this.top, this.getDisplayAreaHeight() - 8);

        this.scrollbarHeight = scrollBarHeight;

        int scrollBarY = (int)this.getScrollOffset() * (this.getDisplayAreaHeight() - scrollBarHeight) / (int)this.getScrollHeight() + this.top;
        if (scrollBarY < this.top) {
            scrollBarY = this.top;
        }

        int scrollAreaL = this.scrollbarX;
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
        int displayedOptions = 0;
        for (int i = 0; i < this.getWidgets().length; i++) {
            if(this.getWidgets()[i].isRendered()) {
                this.getWidgets()[i].render(matrices, displayedOptions, mouseX, mouseY, (int) this.getScrollOffset());
                displayedOptions += this.getWidgets()[i].getBaseConfig().isFullWidth()? ((displayedOptions % 2 == 0)? 2 : 3) : 1;
            }
        }
        if(this.displayedIndexes != displayedOptions) {
            this.displayedIndexes = displayedOptions;
            setScroll(this.getScrollOffset());
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
        int texY = (this.isHoveredOverDone(mouseX, mouseY) || this.currentSelected == -1) ? 86 : 66;
        DrawableHelper drawHelper = new DrawableHelper() {};
        drawHelper.drawTexture(matrices, this.width / 2 - 100, this.bottom + 5, 0, texY, 200, 20);
        DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("gui.done"), this.width / 2, this.bottom + 11, Formatting.WHITE.getColorValue());
    }

    private boolean isHoveredOverDone(int mouseX, int mouseY) {
        return this.width / 2 - 100 <= mouseX && mouseX <= this.width / 2 + 100 && this.bottom + 5 <= mouseY && mouseY <= this.bottom + 25;
    }

    private void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if(this.renderTooltip != null && this.renderTooltip.length > 0 && !this.renderTooltip[0].isEmpty()) {
            List<Text> texts = new ArrayList<>();
            for (String s : this.renderTooltip) {
                if(!TextTools.translateOrDefault(s).isEmpty())
                    texts.add(Text.translatable(s));
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
        if(this.getOptionsHeight() + this.top <= this.bottom) return;

        this.setScroll(this.getScrollOffset() + amount);
    }
    private void setScroll(double scroll) {
        this.scrollOffset = Math.max(0, Math.min(this.getOptionsHeight() - this.getDisplayAreaHeight(), scroll));
    }

    boolean ioInterruptRequest = false;

    public void requestUnfocus(String exception) {
        for (GuiBaseWidget e : this.getWidgets()) {
            if (e instanceof GuiTypableWidget && !e.getBaseConfig().getKey().equals(exception)) ((GuiTypableWidget) e).setFocused(false);
        }
    }

    public void tabNavigate(int step) {
        if (this.currentSelected >= 0) this.getWidgets()[this.currentSelected].select(false);
        this.currentSelected = ((this.getWidgets().length + this.currentSelected + step + 2) % (this.getWidgets().length + 1)) - 1; //range -1..amountOfWidgets-1

        //done button handles itself
        if(this.currentSelected == -1) {
            return;
        }

        //check that selected widget is editable and displayed
        if( !this.getWidgets()[this.currentSelected].getBaseConfig().isEditable() || !this.getWidgets()[this.currentSelected].getBaseConfig().isDisplayed() ) {
            if(step >= 0) tabNavigate(1);
            if(step < 0) tabNavigate(-1);
            return;
        }

        this.getWidgets()[this.currentSelected].select(true);

        //make it scroll
        if(this.getWidgets()[this.currentSelected].getY() >= this.getScrollOffset() + this.getBottom() - this.getWidgets()[this.currentSelected].getHeight() - 3 ) {
            this.setScroll(this.getWidgets()[this.currentSelected].getY() - this.getTop() - 3);
        }
        else if(this.getWidgets()[this.currentSelected].getY() <= this.getScrollOffset() + this.getTop() + 3 ) {
            this.setScroll(this.getWidgets()[this.currentSelected].getY() - this.getBottom() + this.getWidgets()[this.currentSelected].getHeight() + 3);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.ioInterruptRequest = false;
        if(button == 0 && this.getScrollHeight() != 0 && mouseX >= this.scrollbarX && mouseX <= this.scrollbarX + 6) this.scrolling = true;
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
                double scrollPerScrollbarPixel = this.getScrollHeight() / (double)(this.getDisplayAreaHeight() - this.scrollbarHeight);
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

        if(key == GLFW.GLFW_KEY_TAB) {
            int step = (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) ?
                    -1 : 1;
            tabNavigate(step);
        }

        if(key == GLFW.GLFW_KEY_ENTER) {
            if(this.currentSelected >= 0) {
                this.getWidgets()[this.currentSelected].activate();
            }
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
                e.onTextReceived(String.valueOf(chr));
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
        this.onCloseDep.run();
        this.onClose.accept(this.getConfig().getKey());
        this.active = false;
        this.client.setScreen(parent);
    }

    Runnable onOpenDep = () -> {};
    @Deprecated
    public void onOpen(Runnable method) {
        this.onOpenDep = method;
    }
    Consumer<String> onOpen = (name) -> {};
    public void onOpen(Consumer<String> method) {
        this.onOpen = method;
    }

    Runnable onCloseDep = () -> {};
    @Deprecated
    public void onClose(Runnable method) {
        this.onCloseDep = method;
    }
    Consumer<String> onClose = (name) -> {};
    public void onClose(Consumer<String> method) {
        this.onClose = method;
    }
}
