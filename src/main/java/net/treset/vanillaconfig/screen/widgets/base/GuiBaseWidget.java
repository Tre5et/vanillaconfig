package net.treset.vanillaconfig.screen.widgets.base;

import net.minecraft.client.util.math.MatrixStack;
import net.treset.vanillaconfig.config.base.BaseConfig;

public class GuiBaseWidget {

    int width, height = 0;
    int x, y = 0;
    int screenX, screenY = 0;
    BaseConfig baseConfig;

    public int getHeight() { return this.height; }
    public int getWidth() { return this.width; }

    public BaseConfig getBaseConfig() { return this.baseConfig; }
    public boolean setBaseConfig(BaseConfig config) {
        if(!(this.baseConfig == null)) return false;
        this.baseConfig = config;
        return true;
    }

    public boolean isRendered() { return this.getBaseConfig().isDisplayed(); }

    public void onClose() {};

    public boolean render(MatrixStack matrices, int y, int mouseX, int mouseY, int scrollOffset) { return false; }
    public boolean render(MatrixStack matrices, int mouseX, int mouseY, int scrollOffset) {
        return this.render(matrices, this.y, mouseX, mouseY, scrollOffset);
    }

    public void onMouseDown(int key) {}
    public void onMouseUp(int key) {}
    public void onKeyDown(int key, int scancode) {}
    public void onKeyUp(int key, int scancode) {}
    public void onTextReceived(String text) {}

}
