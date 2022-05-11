package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.DoubleConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiNumberWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiDoubleWidget extends GuiNumberWidget {
    DoubleConfig config;

    public GuiDoubleWidget(DoubleConfig config, ConfigScreen screen) {
        super(config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.DECIMAL_NUMBERS);

        this.initMessage();
    }

    public String initMessage() {
        if(this.config == null) return "ERROR";
        this.setTitle(this.config.getKey());
        String startValue = TextTools.doubleToString(this.config.getDouble());
        this.setValue(startValue);
        this.setDefaultValue(startValue);
        return this.getMessage();
    }

    @Override
    public void updateMessage() {
        if(this.isFocused()) return;

        this.setValue(TextTools.doubleToString(this.config.getDouble()), true);
    }

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
