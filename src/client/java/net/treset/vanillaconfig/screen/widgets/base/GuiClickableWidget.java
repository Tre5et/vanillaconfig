package net.treset.vanillaconfig.screen.widgets.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.tools.TextTools;

public class GuiClickableWidget extends GuiBaseWidget {
    public static final Identifier BUTTON = Identifier.fromNamespaceAndPath("minecraft", "widget/button");
    public static final Identifier BUTTON_HIGHLIGHT = Identifier.fromNamespaceAndPath("minecraft", "widget/button_highlighted");
    public static final Identifier BUTTON_DISABLED = Identifier.fromNamespaceAndPath("minecraft", "widget/button_disabled");

    ConfigScreen parentScreen;

    String title;
    String value;
    String defaultValue = "";

    int clicked = -1;

    boolean prevMouseOver = false;

    public GuiClickableWidget(BaseConfig config, String title, String value, ConfigScreen screen) {
        this.parentScreen = screen;

        this.title = title;
        this.value = value;

        this.setBaseConfig(config);

        this.width = this.getBaseConfig().getWidth()[0];
        this.height = 20;
    }
    public GuiClickableWidget(BaseConfig config, ConfigScreen screen) {
        this(config, "", "", screen);
    }

    public void setTitle(String key) { this.title = key;}
    public String getTitle() { return TextTools.translateOrDefault(this.title); }
    public String getTitleKey() { return this.title; }

    public String getValue() { return TextTools.translateOrDefault(this.value); }
    public void setValue(String key) { this.value = key; }
    public String getValueKey() { return this.value; }
    public String getDefaultValue() { return this.defaultValue; }
    public boolean setDefaultValue(String value) { this.defaultValue = value; return true; }

    public String getMessage() {
        return this.getTitle() +
                ((this.getTitle().isEmpty() || this.getValue().isEmpty())? "" : ": ") +
                this.getValue();
    }

    public ConfigScreen getParentScreen() { return this.parentScreen; }

    @Override
    public boolean select(boolean select) {
        if(!this.getBaseConfig().isEditable()) return false;
        if(select) {
            TextTools.narrateLiteral(this.getSelectNarration());
        }
        this.selected = select;
        return true;
    }

    @Override
    public boolean activate() {
        this.onClickL();
        TextTools.narrateLiteral(this.getActivateNarration());
        return true;
    }

    @Override
    public boolean render(GuiGraphicsExtractor ctx, int index, int mouseX, int mouseY, int scrollOffset) {
        if (!this.isRendered()) return false;

        this.y = 5 + index * 25 + this.parentScreen.getTop();
        if(this.getBaseConfig().isFullWidth()) {
            this.width = this.getBaseConfig().getWidth()[0];
            this.y = 5 + (int)Math.ceil((double)index / 2) * 25 + this.parentScreen.getTop();
            this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - width / 2;
        } else {
            this.width = this.getBaseConfig().getWidth()[1];
            this.y = 5 + index / 2 * 25 + this.parentScreen.getTop();
            if(index % 2 == 0) this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - this.width - 5;
            else this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 5;
        }

        this.screenY = this.y - scrollOffset;
        this.screenX = this.x;

        if(this.screenY > this.parentScreen.getTop() + this.parentScreen.getDisplayAreaHeight()) return true;

        this.onRender();

        handleMouseClick(mouseX, mouseY);

        boolean mouseOver = isHoveredOver(mouseX, mouseY);
        if(this.prevMouseOver != mouseOver) {
            if(mouseOver) this.onMouseEnter();
            else this.onMouseLeave();
            this.prevMouseOver = mouseOver;
        }

        Font font = this.parentScreen.getFont();

        return
                this.renderTexture(ctx, mouseX, mouseY) &&
                this.renderText(ctx, font) &&
                this.renderTooltip(mouseX, mouseY);
    }

    public boolean renderTexture(GuiGraphicsExtractor ctx, int mouseX, int mouseY) {
        Identifier texture = this.getTexture(mouseX, mouseY);
        ctx.blitSprite(RenderPipelines.GUI_TEXTURED, texture, this.screenX, this.screenY, this.width, this.getHeight()); // draw left half
        return true;
    }
    public boolean renderText(GuiGraphicsExtractor ctx, Font font) {
        int textColor = this.getTextColor();
        ctx.centeredText(font, this.getMessage(), this.screenX + this.width / 2, this.screenY + (this.height - 8) / 2, textColor);
        return true;
    }
    public boolean renderTooltip(int mouseX, int mouseY) {
        if(this.isHoveredOver(mouseX, mouseY) && this.getBaseConfig().hasDesc()) {
            return this.getParentScreen().requestTooltip(this.getBaseConfig().getDesc());
        }
        return true;
    }

    public Identifier getTexture(int mouseX, int mouseY) {
        if(!this.getBaseConfig().isEditable()) return BUTTON_DISABLED;
        else if(this.isHoveredOver(mouseX, mouseY) || this.selected) return BUTTON_HIGHLIGHT;
        return BUTTON;
    }

    public int getTextColor() {
        return this.getBaseConfig().isEditable() ? 0xFFFFFFFF : 0xFFA0A0A0;
    }
    public boolean isHoveredOver(int mouseX, int mouseY) {
        return this.screenX < mouseX && mouseX < this.screenX + this.width && this.screenY < mouseY && mouseY < this.screenY + this.height;
    }

    @Override
    public void onMouseDown(int button) {
        if(this.getBaseConfig().isEditable() && this.isRendered()) this.clicked = button;
    }
    private void handleMouseClick(int mouseX, int mouseY) {
        if(this.clicked != -1 && this.isHoveredOver(mouseX, mouseY)) {
            switch (this.clicked) {
                case 0 -> this.onClickL();
                case 1 -> this.onClickR();
                case 3 -> this.onClickM();
            }

            this.getParentScreen().requestUnfocus(this.getBaseConfig().getKey());
        }
        this.clicked = -1;
    }

    public void onRender() {}
    public void onClickL() {}
    public void onClickR() {}
    public void onClickM() {}
    public void onMouseEnter() {
        TextTools.narrateLiteral(this.getSelectNarration());
    }
    public void onMouseLeave() {}

    public void requestIoInterrupt() {
        this.getParentScreen().requestIoInterrupt();
    }
}
