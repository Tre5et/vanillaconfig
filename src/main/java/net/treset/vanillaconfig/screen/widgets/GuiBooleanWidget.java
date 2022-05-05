package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.treset.vanillaconfig.config.BooleanConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiButtonWidget;
import net.treset.vanillaconfig.tools.TextTools;

public class GuiBooleanWidget extends GuiButtonWidget {
    BooleanConfig config;

    public GuiBooleanWidget(BooleanConfig config, ConfigScreen screen) {
        super(310, 150, config, screen);

        this.config = config;

        updateMessage();
    }

    public String updateMessage(BooleanConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(TextTools.booleanToString(config.getBoolean()));
        return this.getMessage();
    }
    public String updateMessage() {
        return updateMessage(this.config);
    }

    @Override
    public void onRender() {
        updateMessage();
    }

    @Override
    public void onClickL() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        config.setBoolean(!config.getBoolean());
    }

    @Override
    public void onClickR() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.config.resetValue();
    }
}
