package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.IntegerConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiIntegerWidget extends GuiTypableWidget {
    IntegerConfig config;

    public GuiIntegerWidget(int y, IntegerConfig config, ConfigScreen screen) {
        super(y, 300, config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.NUMBERS.getChars());

        this.initMessage();
    }

    public String initMessage(IntegerConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(TextTools.intToString(config.getInteger()));
        this.setDefaultValue(TextTools.intToString(config.getInteger()));
        return this.getMessage();
    }
    public String initMessage() {
        return initMessage(this.config);
    }

    @Override
    public void setDisplayValue(String value) {
        int intValue = TextTools.stringToInt(value);
        this.setValue(TextTools.asDisplayIntString(value), this.config.isIntValid(intValue));
    }

    @Override
    public void reset() {
        this.setValue(TextTools.intToString(this.config.getInteger()), true);
        this.setFocused(false);
    }

    @Override
    public void save() {
        this.config.setInteger(TextTools.stringToInt(this.getValue()));

        this.setValue(TextTools.intToString(this.config.getInteger()), true);
    }
}
