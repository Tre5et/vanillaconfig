package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.DoubleConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiTypableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiDoubleWidget extends GuiTypableWidget {
    DoubleConfig config;

    public GuiDoubleWidget(int y, DoubleConfig config, ConfigScreen screen) {
        super(y, 300, config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.DECIMAL_NUMBERS.getChars());

        this.initMessage();
    }

    public String initMessage(DoubleConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        String startValue = TextTools.doubleToString(config.getDouble());
        this.setValue(startValue);
        this.setDefaultValue(startValue);
        return this.getMessage();
    }
    public String initMessage() { return this.initMessage(this.config); }

    @Override
    public void setDisplayValue(String value) {
        double doubleValue = TextTools.stringToDouble(value);
        this.setValue(TextTools.asDisplayDoubleString(value), this.config.isDoubleValid(doubleValue));
    }

    @Override
    public void reset() {
        this.setValue(TextTools.doubleToString(this.config.getDouble()), true);
        this.setFocused(false);
    }

    @Override
    public void save() {
        this.config.setDouble(TextTools.stringToDouble(this.getValue()));

        this.setValue(TextTools.doubleToString(this.config.getDouble()), true);
    }
}
