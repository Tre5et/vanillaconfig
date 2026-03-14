package net.treset.vanillaconfig.screen.widgets.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class GuiTypableWidget extends GuiClickableWidget {
    boolean focused = false;

    AllowedChars allowedChars = AllowedChars.NONE;

    public GuiTypableWidget(BaseConfig config, String title, String value, ConfigScreen screen) {
        super(config, title, value, screen);

        this.defaultValue = value;
    }
    public GuiTypableWidget(BaseConfig config, ConfigScreen screen) {
        this(config, "", "", screen);
    }

    public void setFocused(boolean focused) {
        if (focused) {
            getParentScreen().requestUnfocus(this.getBaseConfig().getKey());
            TextTools.narrateLiteral(this.getActivateNarration());
        }
        else if(this.isFocused()) {
            this.save();
        }
        this.focused = focused;
    }

    public boolean isFocused() { return this.focused; }

    public void setDisplayValue(String value) {}

    public void updateMessage() {}

    @Override
    public void onKeyDown(int key, int scancode) {
        if(!this.isFocused()) return;
        this.requestIoInterrupt();
        if(key == GLFW.GLFW_KEY_BACKSPACE && !this.getValue().isEmpty()) {
            this.removeLastChar();
            TextTools.narrateLiteral(this.getChangeNarration());
        } else if(key == GLFW.GLFW_KEY_ENTER) {
            this.confirmText();
        } else if(key == GLFW.GLFW_KEY_ESCAPE) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setValue(this.getDefaultValue());
            TextTools.narrateLiteral(this.getResetNarration());
            this.setFocused(false);
            this.save();
        } else if(this.allowedChars.ignoreShift()) {
            String keyName = GLFW.glfwGetKeyName(-1, scancode);
            if(keyName != null && !keyName.isEmpty() && Arrays.asList(this.allowedChars.getChars()).contains(keyName)) {
                this.setDisplayValue(this.getValue() + keyName);
                TextTools.narrateLiteral(this.getChangeNarration());
            }
        }
    }

    public void removeLastChar() {
        this.setDisplayValue(this.getValue().substring(0, this.getValue().length() - 1));
    }

    public void confirmText() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.setFocused(false);
        this.save();
        TextTools.narrateLiteral(this.getSaveNarration());
        this.setDefaultValue(this.getValue());
    }

    @Override
    public void onTextReceived(String text) {
        if(!this.focused) return;
        this.requestIoInterrupt();
        if((Arrays.asList(this.allowedChars.getChars()).contains(text) && !this.allowedChars.ignoreShift()) || this.allowedChars == AllowedChars.ALL) {
            this.setDisplayValue(this.getValue() + text);
            TextTools.narrateLiteral(this.getChangeNarration());
        }
    }

    public void setAllowedChars(AllowedChars allowedChars) {
        this.allowedChars = allowedChars;
    }

    boolean valid = true;
    public void setValue(String key, boolean valid) {
        this.valid = valid;
        super.setValue(key);
    }

    public String getSaveNarration() { return ""; }
    public String getResetNarration() { return ""; }
    public String getChangeNarration() { return ""; }

    public void reset() {}
    public void save() {}

    @Override
    public String getMessage() {
        if(this.isFocused()) return "> " + this.getTitle() + ": " + this.getValue() + " <";
        return this.getTitle() + ": " + this.getValue();
    }

    @Override
    public void onRender() {
        this.updateMessage();
        super.onRender();
    }

    @Override
    public void onClickL() {
        if(this.isFocused()) {
            this.confirmText();
        } else {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setFocused(true);
        }
    }

    @Override
    public void onClickR() {
        if(this.isFocused()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            TextTools.narrateLiteral(this.getResetNarration());
            this.reset();
        }
    }

    @Override
    public int getTextColor() {
        if(!this.valid) return 0xFF0000FF;
        if(this.focused) return 0xFFFFFF55;
        return super.getTextColor();
    }

    @Override
    public void onClose() {
        this.save();
    }
}
