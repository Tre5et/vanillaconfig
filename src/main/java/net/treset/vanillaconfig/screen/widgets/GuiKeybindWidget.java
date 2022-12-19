package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.NarratorManager;
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
        super(config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.ALL);

        this.initMessage();
    }

    public String initMessage() {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(TextTools.scancodesAsDisplayKeys(config.getKeys()));
        this.setDefaultValue(this.getValue());
        this.currentScancodes.clear();
        Collections.addAll(this.currentScancodes, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
        return this.getMessage();
    }

    List<Integer> currentScancodes = new ArrayList<>();

    //i really need to come up with a better way to use live values
    @Override
    public String getSelectNarration() {
        if(!this.config.getSelectNarration().equals(""))
            return this.config.getSelectNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.select"), this.config.getName(), this.getValue());
    }
    @Override
    public String getActivateNarration() {
        if(!this.config.getActivateNarration().equals(""))
            return this.config.getActivateNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.activate"), this.config.getName(), this.getValue());
    }
    @Override
    public String getChangeNarration() {
        if(!this.config.getChangeNarration().equals(""))
            return this.config.getChangeNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.change"), this.getValue());
    }
    @Override
    public String getSaveNarration() {
        if(!this.config.getSaveNarration().equals(""))
            return this.config.getSaveNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.save"), this.config.getName(), this.getValue());
    }
    @Override
    public String getResetNarration() {
        if(!this.config.getResetNarration().equals(""))
            return this.config.getResetNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.reset"), this.config.getName(), this.getValue());
    }

    @Override
    public void updateMessage() {
        if(this.isFocused()) return;

        this.currentScancodes.clear();
        Collections.addAll(this.currentScancodes, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
    }

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
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getSaveNarration());
            }
        } else if(key == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.reset();
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getResetNarration());
            }
        } else {
            currentScancodes.add(scancode);
            String newKey = TextTools.getKeyFromScancode(scancode, true);
            if(newKey == null) return;
            this.setDisplayValue(TextTools.appendKeyToDisplayKeys(newKey, this.getValue()));
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getChangeNarration());
            }
        }
    }

    @Override
    public void onTextReceived(String text) {
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
