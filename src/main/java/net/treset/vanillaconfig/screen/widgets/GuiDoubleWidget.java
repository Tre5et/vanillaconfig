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

    @Override
    public String getSelectNarration() {
        if(this.config.isSlider())
            return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.slider.select"), this.config.getName(), this.config.getDouble());
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.select"), this.config.getName(), this.config.getDouble());
    }
    @Override
    public String getActivateNarration() {
        if(this.config.isSlider()) return "";
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.activate"), this.config.getName(), this.config.getDouble());
    }
    @Override
    public String getChangeNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.change"), this.getValue()); }
    @Override
    public String getChangeSliderNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.slider.change"), this.config.getDouble()); }
    @Override
    public String getSaveNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.save"), this.config.getName(), this.config.getDouble()); }
    @Override
    public String getResetNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.reset"), this.config.getName(), this.config.getDouble()); }

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
