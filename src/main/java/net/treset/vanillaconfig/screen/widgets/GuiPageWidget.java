package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.treset.vanillaconfig.config.PageConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiClickableWidget;
import net.treset.vanillaconfig.tools.TextTools;

public class GuiPageWidget extends GuiClickableWidget {
    PageConfig config;

    public GuiPageWidget(PageConfig config, ConfigScreen screen) {
        super(config, screen);

        this.config = config;

        initMessage();
    }

    public String initMessage(PageConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        return this.getMessage();
    }
    public String initMessage() {
        return initMessage(this.config);
    }

    @Override
    public String getSelectNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.page.select"), this.config.getName()); }

    @Override
    public void onClickL() {
        this.config.click();
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        ConfigScreen screen = new ConfigScreen(this.config, MinecraftClient.getInstance().currentScreen);
        MinecraftClient.getInstance().setScreen(screen);
    }

    @Override
    public void onClickR() {
        this.onClickL();
    }
}
