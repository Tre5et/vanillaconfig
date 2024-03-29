package net.treset.vanillaconfig.screen.widgets.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
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
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(this.getActivateNarration());
            }
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
        if(key == GLFW.GLFW_KEY_BACKSPACE && this.getValue().length() > 0) {
            this.removeLastChar();
            if (MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(this.getChangeNarration());
            }
        } else if(key == GLFW.GLFW_KEY_ENTER) {
            this.confirmText();
        } else if(key == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setValue(this.getDefaultValue());
            if (MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(this.getResetNarration());
            }
            this.setFocused(false);
            this.save();
        } else if(this.allowedChars.ignoreShift()) {
            String keyName = GLFW.glfwGetKeyName(-1, scancode);
            if(keyName != null && !keyName.isEmpty() && Arrays.asList(this.allowedChars.getChars()).contains(keyName)) {
                this.setDisplayValue(this.getValue() + keyName);
                if (MinecraftClient.getInstance().getNarratorManager().isActive()) {
                    MinecraftClient.getInstance().getNarratorManager().narrate(this.getChangeNarration());
                }
            }
        }
    }

    public void removeLastChar() {
        this.setDisplayValue(this.getValue().substring(0, this.getValue().length() - 1));
    }

    public void confirmText() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.setFocused(false);
        this.save();
        if (MinecraftClient.getInstance().getNarratorManager().isActive()) {
            MinecraftClient.getInstance().getNarratorManager().narrate(this.getSaveNarration());
        }
        this.setDefaultValue(this.getValue());
    }

    @Override
    public void onTextReceived(String text) {
        if(!this.focused) return;
        this.requestIoInterrupt();
        if((Arrays.asList(this.allowedChars.getChars()).contains(text) && !this.allowedChars.ignoreShift()) || this.allowedChars == AllowedChars.ALL) {
            this.setDisplayValue(this.getValue() + text);
            if (MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(this.getChangeNarration());
            }
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
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setFocused(true);
        }
    }

    @Override
    public void onClickR() {
        if(this.isFocused()) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(this.getResetNarration());
            }
            this.reset();
        }
    }

    @Override
    public int getTextColor() {
        if(!this.valid) return Formatting.RED.getColorValue();
        if(this.focused) return Formatting.YELLOW.getColorValue();
        return super.getTextColor();
    }

    @Override
    public void onClose() {
        this.save();
    }
}
