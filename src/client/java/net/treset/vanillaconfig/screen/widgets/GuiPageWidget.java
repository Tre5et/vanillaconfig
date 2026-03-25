package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.treset.vanillaconfig.config.PageConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiClickableWidget;

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
    public String getSelectNarration() { return this.config.getSelectNarration(); }

    @Override
    public void onClickL() {
        this.config.click();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        ConfigScreen screen = new ConfigScreen(this.config, Minecraft.getInstance().screen);
        Minecraft.getInstance().setScreen(screen);
    }

    @Override
    public void onClickR() {
        this.onClickL();
    }
}
