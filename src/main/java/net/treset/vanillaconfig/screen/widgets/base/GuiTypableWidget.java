package net.treset.vanillaconfig.screen.widgets.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiTypableWidget extends GuiButtonWidget{
    boolean focused = false;

    String keyPressed = "";

    List<String> allowedChars = new ArrayList<>();
    boolean allowShift = false;

    public GuiTypableWidget(int y, int width, BaseConfig config, String title, String value, boolean active, ConfigScreen screen) {
        super(y, width, config, title, value, screen);

        this.defaultValue = value;
    }
    public GuiTypableWidget(int y, int width, BaseConfig config, ConfigScreen screen) {
        this(y, width, config, "", "", true, screen);
    }

    public void setFocused(boolean focused) {
        if (focused) {
            for (GuiBaseWidget e : parentScreen.getWidgets()) {
                if (e instanceof GuiTypableWidget) ((GuiTypableWidget) e).setFocused(false);
            }
        }
        this.focused = focused;
    }

    public boolean isFocused() {return this.focused; }

    public void setDisplayValue(String value) {}

    @Override
    public void onKeyDown(int key, int scancode) {
        if(!this.isFocused()) return;
        this.requestIoInterrupt();
        if(key == GLFW.GLFW_KEY_BACKSPACE && this.getValue().length() > 0) {
            this.removeLastChar();
        } else if(key == GLFW.GLFW_KEY_ENTER) {
            this.confirmText();
        } else if(key == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setValue(this.getDefaultValue());
            this.setFocused(false);
            this.save();
        }
    }

    public void removeLastChar() {
        this.setDisplayValue(this.getValue().substring(0, this.getValue().length() - 1));
    }

    public void confirmText() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.setFocused(false);
        this.save();
        this.setDefaultValue(this.getValue());
    }

    @Override
    public void onTextReceived(String text) {
        if(!this.focused) return;
        this.requestIoInterrupt();
        if(this.allowedChars.contains(text) || (this.allowedChars.get(0).equals("*") && this.allowedChars.size() == 1)) {
            this.setDisplayValue(this.getValue() + text);
        }
    }

    public void setAllowedChars(String[] keys) {
        this.allowedChars.clear();
        this.allowedChars.addAll(Arrays.asList(keys));
    }

    boolean valid = true;
    public void setValue(String key, boolean valid) {
        this.valid = valid;
        super.setValue(key);
    }

    public void reset() {}
    public void save() {}

    @Override
    public String getMessage() {
        if(this.isFocused()) return "> " + this.getTitle() + ": " + this.getValue() + " <";
        return this.getTitle() + ": " + this.getValue();
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
