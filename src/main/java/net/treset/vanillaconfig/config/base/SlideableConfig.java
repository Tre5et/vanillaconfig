package net.treset.vanillaconfig.config.base;

import net.treset.vanillaconfig.config.config_type.ConfigType;

public class SlideableConfig extends BaseConfig{
    boolean slider = false;

    public SlideableConfig(ConfigType type, String name) {
        super(type, name);
    }

    public boolean isSlider() { return slider; }
    public boolean setSlider(boolean slider) { this.slider = slider; return true; }

    public double getDoubleValue() { return -1; }
    public double getMinDoubleValue() { return -1; }
    public double getMaxDoubleValue() { return -1; }
}
