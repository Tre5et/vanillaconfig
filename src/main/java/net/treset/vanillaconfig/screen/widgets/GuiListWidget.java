package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.treset.vanillaconfig.config.ListConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiButtonWidget;

public class GuiListWidget extends GuiButtonWidget {
    ListConfig config;

    public GuiListWidget(int y, ListConfig config, ConfigScreen screen) {
        super(y, 300, config, screen);

        this.config = config;
    }

    public String updateMessage(ListConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(config.getOption());
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
        config.increment(true);
    }

    @Override
    public void onClickR() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        config.increment(false);
    }
}
