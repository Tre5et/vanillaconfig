package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.treset.vanillaconfig.config.ButtonConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiClickableWidget;

public class GuiButtonWidget extends GuiClickableWidget {
    ButtonConfig config;

    public GuiButtonWidget(ButtonConfig config, ConfigScreen screen) {
        super(config, screen);

        this.config = config;

        initMessage();
    }

    public String initMessage() {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        return this.getMessage();
    }

    @Override
    public void onClickL() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.config.invokeOnClickL();
    }

    @Override
    public void onClickR() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.config.invokeOnClickR();
    }
}
