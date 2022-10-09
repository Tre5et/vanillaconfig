package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.treset.vanillaconfig.config.ButtonConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiClickableWidget;
import net.treset.vanillaconfig.tools.TextTools;

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
    public String getSelectNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.button.select"), this.config.getName()); }
    @Override
    public String getActivateNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.button.activate"), this.config.getName()); }

    @Override
    public void onClickL() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.config.invokeOnClickL();
    }

    @Override
    public void onClickR() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.config.invokeOnClickR();
    }
}
