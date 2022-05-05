package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.treset.vanillaconfig.config.KeybindConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiKeybindWidget extends GuiTypableWidget {
    KeybindConfig config;

    public GuiKeybindWidget(KeybindConfig config, ConfigScreen screen) {
        super(310, 150, config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.ALL);

        this.updateMessage();
    }

    public String updateMessage() {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(TextTools.scancodesAsDisplayKeys(config.getKeys()));
        this.setDefaultValue(this.getValue());
        this.currentScancodes.clear();
        Collections.addAll(this.currentScancodes, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
        return this.getMessage();
    }

    List<Integer> currentScancodes = new ArrayList<>();

    @Override
    public void onKeyDown(int key, int scancode) {
        if(!this.isFocused()) return;
        this.requestIoInterrupt();
        if(key == GLFW.GLFW_KEY_BACKSPACE) {
            this.removeLastChar();
        } else if(key == GLFW.GLFW_KEY_ENTER) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setFocused(false);
            this.save();
        } else if(key == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.reset();
        } else {
            currentScancodes.add(scancode);
            String newKey = TextTools.getKeyFromScancode(scancode, true);
            if(newKey == null) return;
            this.setDisplayValue(TextTools.appendKeyToDisplayKeys(newKey, this.getValue()));
        }
    }

    @Override
    public void onTextReceived(String text) {
        return;
    }

    @Override
    public void setDisplayValue(String value) {
        this.setValue(value, this.config.isKeysValid(this.currentScancodes.stream().mapToInt(i->i).toArray()));
    }

    @Override
    public void removeLastChar() {
        this.setValue("", true);
        this.currentScancodes.clear();
    }

    @Override
    public void onClickL() {
        if(!this.isFocused()) {
            this.setValue("", true);
            this.currentScancodes.clear();
        }
        super.onClickL();
    }

    @Override
    public void reset() {
        this.setValue(TextTools.scancodesAsDisplayKeys(this.config.getKeys()), true);
        this.setFocused(false);
    }

    @Override
    public void save() {
        this.config.setKeys(this.currentScancodes.stream().mapToInt(i->i).toArray());
        this.currentScancodes.clear();
        Collections.addAll(this.currentScancodes, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
        this.setValue(TextTools.scancodesAsDisplayKeys(this.config.getKeys()), true);
    }
}
