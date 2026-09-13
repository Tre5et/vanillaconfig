package net.treset.vanillaconfig.screen.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.treset.vanillaconfig.config.KeybindConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

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
        this.setValue(TextTools.keysAsDisplay(config.getKeys()));
        this.setDefaultValue(this.getValue());
        this.currentKeys.clear();
        Collections.addAll(this.currentKeys, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
        return this.getMessage();
    }

    List<Integer> currentKeys = new ArrayList<>();

    //i really need to come up with a better way to use live values
    @Override
    public String getSelectNarration() {
        if(!this.config.getSelectNarration().isEmpty())
            return this.config.getSelectNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.select"), this.config.getName(), this.getValue());
    }
    @Override
    public String getActivateNarration() {
        if(!this.config.getActivateNarration().isEmpty())
            return this.config.getActivateNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.activate"), this.config.getName(), this.getValue());
    }
    @Override
    public String getChangeNarration() {
        if(!this.config.getChangeNarration().isEmpty())
            return this.config.getChangeNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.change"), this.getValue());
    }
    @Override
    public String getSaveNarration() {
        if(!this.config.getSaveNarration().isEmpty())
            return this.config.getSaveNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.save"), this.config.getName(), this.getValue());
    }
    @Override
    public String getResetNarration() {
        if(!this.config.getResetNarration().isEmpty())
            return this.config.getResetNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.keybind.reset"), this.config.getName(), this.getValue());
    }

    @Override
    public void updateMessage() {
        if(this.isFocused()) return;

        this.currentKeys.clear();
        Collections.addAll(this.currentKeys, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
    }

    @Override
    public void onKeyDown(KeyEvent input) {
        if(!this.isFocused()) return;
        this.requestIoInterrupt();
        if(input.key() == InputConstants.KEY_BACKSPACE) {
            this.removeLastChar();
        } else if(input.isConfirmation()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.setFocused(false);
            this.save();
            TextTools.narrateLiteral(this.getSaveNarration());
        } else if(input.isEscape()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.reset();
            TextTools.narrateLiteral(this.getResetNarration());
        } else {
            InputConstants.Key key = InputConstants.getKey(input);
            currentKeys.add(key.getValue());
            String newKey = key.getDisplayName().getString();
            this.setDisplayValue(TextTools.appendKeyToDisplayKeys(newKey, this.getValue()));
            TextTools.narrateLiteral(this.getChangeNarration());
        }
    }

    @Override
    public void onTextReceived(String text) {
    }

    @Override
    public void setDisplayValue(String value) {
        this.setValue(value, this.config.isKeysValid(this.currentKeys.stream().mapToInt(i->i).toArray()));
    }

    @Override
    public void removeLastChar() {
        this.setValue("", true);
        this.currentKeys.clear();
    }

    @Override
    public void onClickL() {
        if(!this.isFocused()) {
            this.setValue("", true);
            this.currentKeys.clear();
        }
        super.onClickL();
    }

    @Override
    public void reset() {
        this.setValue(TextTools.keysAsDisplay(this.config.getKeys()), true);
        this.setFocused(false);
    }

    @Override
    public void save() {
        this.config.setKeys(this.currentKeys.stream().mapToInt(i->i).toArray());
        this.currentKeys.clear();
        Collections.addAll(this.currentKeys, Arrays.stream(this.config.getKeys()).boxed().toArray(Integer[]::new));
        this.setValue(TextTools.keysAsDisplay(this.config.getKeys()), true);
    }
}
