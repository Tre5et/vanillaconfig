package net.treset.vanillaconfig.screen.widgets;

import net.treset.vanillaconfig.config.IntegerConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiNumberWidget;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.AllowedChars;

public class GuiIntegerWidget extends GuiNumberWidget {
    IntegerConfig config;

    public GuiIntegerWidget(IntegerConfig config, ConfigScreen screen) {
        super(config, screen);

        this.config = config;

        this.setAllowedChars(AllowedChars.NUMBERS);

        this.initMessage();
    }

    @Override
    public String getSelectNarration() {
        if(this.config.isSlider())
            return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.slider.select"), this.config.getName(), this.config.getInteger());
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.select"), this.config.getName(), this.config.getInteger());
    }
    @Override
    public String getActivateNarration() {
        if(this.config.isSlider()) return "";
        return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.activate"), this.config.getName(), this.config.getInteger());
    }
    @Override
    public String getChangeNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.change"), this.getValue()); }
    @Override
    public String getChangeSliderNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.slider.change"), this.config.getInteger()); }
    @Override
    public String getSaveNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.save"), this.config.getName(), this.config.getInteger()); }
    @Override
    public String getResetNarration() { return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.reset"), this.config.getName(), this.config.getInteger()); }

    @Override
    public void updateMessage() {
        if(this.isFocused()) return;

        this.setValue(TextTools.intToString(this.config.getInteger()), true);
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
