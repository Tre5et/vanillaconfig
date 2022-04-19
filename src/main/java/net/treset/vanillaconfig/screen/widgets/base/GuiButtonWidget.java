package net.treset.vanillaconfig.screen.widgets.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.tools.TextTools;

public class GuiButtonWidget extends GuiBaseWidget {
    public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    ConfigScreen parentScreen;

    String title = "";
    String value = "";
    String defaultValue = "";

    int clicked = -1;

    public GuiButtonWidget(int y, int width, BaseConfig config, String title, String value, ConfigScreen screen) {
        this.parentScreen = screen;

        this.screenX = this.x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - width / 2;
        this.screenY = this.y = y;
        this.title = title;
        this.value = value;

        this.width = width;
        this.height = 20;

        this.setBaseConfig(config);
    }
    public GuiButtonWidget(int y, int width, BaseConfig config, ConfigScreen screen) {
        this(y, width, config, "", "", screen);
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
    public boolean render(MatrixStack matrices, int y, int mouseX, int mouseY, int scrollOffset) {
        if (!this.isRendered()) return false;

        this.y = y;
        this.screenY = this.y - scrollOffset;
        this.screenX = this.x;

        this.onRender();

        handleMouseClick(mouseX, mouseY);

        MinecraftClient cli = MinecraftClient.getInstance();
        if (cli == null) return false;
        TextRenderer textRenderer = cli.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        //draw texture
        int textureOffset = this.getTextureOffset(mouseX, mouseY);
        DrawableHelper drawHelper = new DrawableHelper() {};
        drawHelper.drawTexture(matrices, this.screenX, this.screenY, 0, 46 + textureOffset, this.width / 2, this.getHeight()); // draw left half
        drawHelper.drawTexture(matrices, this.screenX + this.width / 2, this.screenY, 200 - this.width / 2, 46 + textureOffset, this.width / 2, this.getHeight()); //draw right half

        //render text
        int textColor = this.getTextColor();
        DrawableHelper.drawCenteredText(matrices, textRenderer, this.getMessage(), this.screenX + this.width / 2, this.screenY + (this.height - 8) / 2, textColor);

        //render tooltip
        if(this.isHoveredOver(mouseX, mouseY) && this.getBaseConfig().hasDesc()) {
            this.getParentScreen().requestTooltip(this.getBaseConfig().getDesc());
        }

        return true;
    }

    private int getTextureOffset(int mouseX, int mouseY) {
        int offset;
        if(!this.getBaseConfig().isEditable()) offset = 0;
        else if(this.isHoveredOver(mouseX, mouseY)) offset = 40;
        else offset = 20;
        return offset;
    }

    public int getTextColor() { return this.getBaseConfig().isEditable() ? Formatting.WHITE.getColorValue() : 10526880; }
    private boolean isHoveredOver(int mouseX, int mouseY) {
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
        }
        this.clicked = -1;
    }

    public void onRender() {}
    public void onClickL() {}
    public void onClickR() {}
    public void onClickM() {}

    public void requestIoInterrupt() {
        this.getParentScreen().requestIoInterrupt();
    }
}
