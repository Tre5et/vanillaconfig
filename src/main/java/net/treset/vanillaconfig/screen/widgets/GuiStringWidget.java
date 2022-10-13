package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.StringConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiStringWidget extends GuiTypableWidget {
    StringConfig config;

    public GuiStringWidget(StringConfig config, ConfigScreen screen) {
        super(config, screen);

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
    public String getSelectNarration() { return this.config.getSelectNarration(); }
    @Override
    public String getActivateNarration() { return this.config.getActivateNarration(); }
    @Override
    public String getChangeNarration() {
        if(!this.config.getChangeNarration().equals(""))
            return this.config.getChangeNarration();
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.string.change"), this.getValue());
    }
    @Override
    public String getSaveNarration() { return this.config.getSaveNarration(); }
    @Override
    public String getResetNarration() { return this.config.getResetNarration(); }

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
