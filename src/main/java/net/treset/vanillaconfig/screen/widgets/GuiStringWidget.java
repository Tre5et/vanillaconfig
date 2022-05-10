package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.StringConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiStringWidget extends GuiTypableWidget {
    StringConfig config;

    public GuiStringWidget(StringConfig config, ConfigScreen screen) {
        super(310, 150, config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.ALL);

        initMessage();
    }

    public String initMessage(StringConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(config.getString());
        this.setDefaultValue(config.getString());
        return this.getMessage();
    }
    public String initMessage() {
        return initMessage(this.config);
    }

    @Override
    public void updateMessage() {
        if(this.isFocused()) return;

        this.setValue(this.config.getString(), true);
    }

    @Override
    public void setDisplayValue(String value) {
        this.setValue(value, this.config.isStringValid(value));
    }

    @Override
    public void reset() {
        this.setValue(this.config.getString(), true);
        this.setFocused(false);
    }

    @Override
    public void save() {
        this.config.setString(this.getValue());
        this.setValue(this.config.getString(), true);
    }
}
