package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.IntegerConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiNumberWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiIntegerWidget extends GuiNumberWidget {
    IntegerConfig config;

    public GuiIntegerWidget(IntegerConfig config, ConfigScreen screen) {
        super(310, 150, config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.NUMBERS);

        this.initMessage();
    }

    @Override
    public String initMessage() {
        if(config == null) return "ERROR";
        this.setTitle(this.config.getKey());
        this.setValue(TextTools.intToString(this.config.getInteger()));
        this.setDefaultValue(TextTools.intToString(this.config.getInteger()));
        return this.getMessage();
    }

    @Override
    public void setDisplayValue(String value) {
        int intValue = (int)Math.rint(TextTools.stringToDouble(TextTools.asIntString(value)));
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
