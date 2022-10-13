package net.treset.vanillaconfig.screen.widgets.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.tools.TextTools;

public class GuiClickableWidget extends GuiBaseWidget {
    public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

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
                ((this.getTitle().equals("") || this.getValue().equals(""))? "" : ": ") +
                this.getValue();
    }

    public ConfigScreen getParentScreen() { return this.parentScreen; }

    @Override
    public boolean select(boolean select) {
        if(!this.getBaseConfig().isEditable()) return false;
        if(select) {
            if (NarratorManager.INSTANCE.isActive()) {
                NarratorManager.INSTANCE.narrate(this.getSelectNarration());
            }
        }
        this.selected = select;
        return true;
    }

    @Override
    public boolean activate() {
        this.onClickL();
        if(NarratorManager.INSTANCE.isActive()) {
            NarratorManager.INSTANCE.narrate(this.getActivateNarration());
        }
        return true;
    }

    @Override
    public boolean render(MatrixStack m, int index, int mouseX, int mouseY, int scrollOffset) {
        if (!this.isRendered()) return false;

        this.y = 5 + index * 25 + this.parentScreen.getTop();
        if(this.getBaseConfig().isFullWidth()) {
            this.width = this.getBaseConfig().getWidth()[0];
            this.y = 5 + (int)Math.ceil((double)index / 2) * 25 + this.parentScreen.getTop();
            this.x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - width / 2;
        } else {
            this.width = this.getBaseConfig().getWidth()[1];
            this.y = 5 + index / 2 * 25 + this.parentScreen.getTop();
            if(index % 2 == 0) this.x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - this.width - 5;
            else this.x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 + 5;
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

        MinecraftClient cli = MinecraftClient.getInstance();
        if (cli == null) return false;
        TextRenderer t = cli.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        DrawableHelper d = new DrawableHelper() {};

        return
                this.renderTexture(d, m, mouseX, mouseY) &&
                this.renderText(m, t) &&
                this.renderTooltip(mouseX, mouseY);
    }

    public boolean renderTexture(DrawableHelper d, MatrixStack m, int mouseX, int mouseY) {
        int textureOffset = this.getTextureOffset(mouseX, mouseY);
        d.drawTexture(m, this.screenX, this.screenY, 0, 46 + textureOffset, this.width / 2, this.getHeight()); // draw left half
        d.drawTexture(m, this.screenX + this.width / 2, this.screenY, 200 - this.width / 2, 46 + textureOffset, this.width / 2, this.getHeight()); //draw right half
        return true;
    }
    public boolean renderText(MatrixStack m, TextRenderer t) {
        int textColor = this.getTextColor();
        DrawableHelper.drawCenteredText(m, t, this.getMessage(), this.screenX + this.width / 2, this.screenY + (this.height - 8) / 2, textColor);
        return true;
    }
    public boolean renderTooltip(int mouseX, int mouseY) {
        if(this.isHoveredOver(mouseX, mouseY) && this.getBaseConfig().hasDesc()) {
            return this.getParentScreen().requestTooltip(this.getBaseConfig().getDesc());
        }
        return true;
    }

    public int getTextureOffset(int mouseX, int mouseY) {
        int offset;
        if(!this.getBaseConfig().isEditable()) offset = 0;
        else if(this.isHoveredOver(mouseX, mouseY) || this.selected) offset = 40;
        else offset = 20;
        return offset;
    }

    public int getTextColor() { return this.getBaseConfig().isEditable() ? Formatting.WHITE.getColorValue() : 10526880; }
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
        if(NarratorManager.INSTANCE.isActive()) {
            NarratorManager.INSTANCE.narrate(getSelectNarration());
        }
    }
    public void onMouseLeave() {}

    public void requestIoInterrupt() {
        this.getParentScreen().requestIoInterrupt();
    }
}
