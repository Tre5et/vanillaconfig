package net.treset.vanillaconfig.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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
    public static final Identifier BUTTON = new Identifier("textures/gui/sprites/widget/button.png");
    public static final Identifier BUTTON_HIGHLIGHT = new Identifier("textures/gui/sprites/widget/button_highlighted.png");
    public static final Identifier MENU_LIST_BACKGROUND = new Identifier("textures/gui/menu_list_background.png");
    public static final Identifier HEADER_SEPARATOR = new Identifier("textures/gui/header_separator.png");
    public static final Identifier FOOTER_SEPARATOR = new Identifier("textures/gui/footer_separator.png");

    Screen parent;

    PageConfig config;
    List<GuiBaseWidget> widgets;

    boolean active = false;
    boolean listBackground = true;

    double scrollOffset = 0;

    int displayedIndexes = 0;

    private boolean scrolling = false;

    private final int marginY = 33;
    private final int top = this.marginY;
    private int bottom = this.height - this.marginY;

    private final int marginX = 0;
    private final int left = this.marginX;
    private int right = this.width - this.marginX;

    private final int scrollbarCenterX = 156;
    private int scrollbarX = this.width / 2 + this.scrollbarCenterX;

    private int scrollbarHeight = 0;

    private String[] renderTooltip = new String[]{};

    private int currentSelected = -1;

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

        this.currentSelected = -1;

        this.active = true;
        this.onOpenDep.run();
        this.onOpen.accept(this.getConfig().getKey());
    }

    public boolean isActive() { return this.active; }

    public boolean isListBackground() {
        return listBackground;
    }

    public void setListBackground(boolean listBackground) {
        this.listBackground = listBackground;
    }

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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.renderOptions(context, mouseX, mouseY);
        this.renderDoneButton(context, mouseX, mouseY);
        this.renderTooltip(context, mouseX, mouseY);
        this.renderScrollbar(context);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        if(this.isListBackground()) {
            RenderSystem.enableBlend();
            context.drawTexture(MENU_LIST_BACKGROUND, 0, this.top, (float)this.getRight(), (float)(this.getBottom() + (int)this.getScrollHeight()), this.width, this.bottom - this.top, 32, 32);
            context.drawTexture(HEADER_SEPARATOR, 0, this.top - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
            context.drawTexture(FOOTER_SEPARATOR, 0, this.getBottom(), 0.0F, 0.0F, this.width, 2, 32, 2);
            RenderSystem.disableBlend();
        }
        context.drawCenteredTextWithShadow(textRenderer, this.config.getName(), this.width / 2, Math.round(MathHelper.lerp(0.5f, 0f, (float)this.top - 9)), 16777215);
    }

    private void renderScrollbar(DrawContext context) {
        if(this.getScrollHeight() == 0) return;

        int scrollBarHeight = (int)((float)(this.getDisplayAreaHeight() * this.getDisplayAreaHeight()) / (float)this.getOptionsHeight());
        scrollBarHeight = MathHelper.clamp(scrollBarHeight, this.top, this.getDisplayAreaHeight() - 8);

        this.scrollbarHeight = scrollBarHeight;

        int scrollBarY = (int)this.getScrollOffset() * (this.getDisplayAreaHeight() - scrollBarHeight) / (int)this.getScrollHeight() + this.top;
        if (scrollBarY < this.top) {
            scrollBarY = this.top;
        }

        int scrollAreaL = this.scrollbarX;
        int scrollAreaR = scrollAreaL + 6;

        context.fill(scrollAreaL, this.top, scrollAreaR , this.bottom, -16777216);
        context.drawGuiTexture(new Identifier("widget/scroller"), scrollAreaL, scrollBarY, 6, scrollBarHeight);
    }

    private void renderOptions(DrawContext ctx, int mouseX, int mouseY) {
        ctx.enableScissor(this.left, this.top, this.right, this.bottom);
        if (!this.isMouseOverOptions(mouseX, mouseY)) mouseX = mouseY = -1;
        int displayedOptions = 0;
        for (int i = 0; i < this.getWidgets().length; i++) {
            if (this.getWidgets()[i].isRendered()) {
                this.getWidgets()[i].render(ctx, displayedOptions, mouseX, mouseY, (int) this.getScrollOffset());
                displayedOptions += this.getWidgets()[i].getBaseConfig().isFullWidth() ? ((displayedOptions % 2 == 0) ? 2 : 3) : 1;
            }
        }
        if (this.displayedIndexes != displayedOptions) {
            this.displayedIndexes = displayedOptions;
        }
        ctx.disableScissor();
    }

    private void renderDoneButton(DrawContext context, int mouseX, int mouseY) {
        final Identifier WIDGETS_TEXTURE = (this.isHoveredOverDone(mouseX, mouseY) || this.currentSelected == this.getWidgets().length) ? BUTTON_HIGHLIGHT : BUTTON;
        context.drawTexture(WIDGETS_TEXTURE, this.width / 2 - 100, Math.round(MathHelper.lerp(0.5f, (float)this.bottom, (float)this.height - 20)), 0, 0, 200, 20, 200, 20);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("gui.done"), this.width / 2, Math.round(MathHelper.lerp(0.5f, (float)this.bottom, (float)this.height - 8)), 16777215);
    }

    private boolean isHoveredOverDone(int mouseX, int mouseY) {
        return this.width / 2 - 100 <= mouseX && mouseX <= this.width / 2 + 100 && this.bottom + 5 <= mouseY && mouseY <= this.bottom + 25;
    }

    private void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if(this.renderTooltip != null && this.renderTooltip.length > 0 && !this.renderTooltip[0].isEmpty()) {
            List<Text> texts = new ArrayList<>();
            for (String s : this.renderTooltip) {
                if(!TextTools.translateOrDefault(s).isEmpty())
                    texts.add(Text.translatable(s));
            }
            context.drawTooltip(textRenderer, texts, mouseX, mouseY);
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
        if (0 <= this.currentSelected && this.currentSelected < this.getWidgets().length) this.getWidgets()[this.currentSelected].select(false);
        this.currentSelected = ((this.getWidgets().length + 1 + this.currentSelected + step) % (this.getWidgets().length + 1)); //range 0..amountOfWidgets

        //done button handles itself
        if(this.currentSelected == this.getWidgets().length) {
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(TextTools.translateOrDefault("vanillaconfig.narration.button.done.select"));
            }
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
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scroll(-verticalAmount * this.getAverageWidgetHeight() / 2D);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
            else if(this.currentSelected == -1) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.close();
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
