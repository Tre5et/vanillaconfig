package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.SlideableConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.tools.TextTools;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DoubleConfig extends SlideableConfig {
    double value = 0;
    double minValue = 0;
    double maxValue = 0;
    double defValue = 0;

    public DoubleConfig(double defaultValue, double minValue, double maxValue, String name, String desc, boolean fullWidth, boolean editable, boolean displayed, boolean slider) {
        super(ConfigType.DOUBLE, name);

        this.setDesc(desc);

        this.setMinDouble(minValue);
        this.setMaxDouble(maxValue);

        if(this.isDoubleValid(defaultValue)) this.defValue = defaultValue;
        else this.defValue = minValue;

        this.setFullWidth(fullWidth);
        this.setEditable(editable);
        this.setDisplayed(displayed);
        this.setSlider(slider);

        this.setSelectNarration(() -> {
            if(this.isSlider())
                return String.format(TextTools.translateOrDefault("vanillaconfig.narration.double.slider.select"), this.getName(), this.getDouble());
            return String.format(TextTools.translateOrDefault("vanillaconfig.narration.double.select"), this.getName(), this.getDouble());
        });
        this.setActivateNarration(() -> {
            if(this.isSlider()) return "";
            return String.format(TextTools.translateOrDefault("vanillaconfig.narration.double.activate"), this.getName(), this.getDouble());
        });
        this.setChangeSliderNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.double.slider.change"), this.getDouble()));
        this.setSaveNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.save"), this.getName(), this.getDouble()));
        this.setResetNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.reset"), this.getName(), this.getDouble()));

        this.resetValue();
    }
    public DoubleConfig(double defaultValue, double minValue, double maxValue, String name, String desc, boolean slider) {
        this(defaultValue, minValue, maxValue, name, desc, true, true, true, slider);
    }
    public DoubleConfig(double defaultValue, double minValue, double maxValue, String name, String desc) {
        this(defaultValue, minValue, maxValue, name, desc, false);
    }
    public DoubleConfig(double defaultValue, double minValue, double maxValue, String name) {
        this(defaultValue, minValue, maxValue, name, "");
    }

    @Override
    public double getDoubleValue() {
        return this.getDouble();
    }
    @Override
    public double getMaxDoubleValue() {
        return this.getMaxDouble();
    }
    @Override
    public double getMinDoubleValue() {
        return this.getMinDouble();
    }
    @Override
    public boolean setDoubleValue(double value) { return this.setDouble(value); }

    public double getDouble() { return this.value; }
    public boolean setDouble(double value) {
        if(!this.isDoubleValid(value)) return false;
        if(this.getDouble() != value) {
            double prevValue = this.getDouble();
            this.value = value;
            this.onChange.accept(prevValue, this.getKey());
        }
        return true;
    }
    public double getDefaultDouble() { return this.defValue; }
    public double getMinDouble() { return this.minValue; }
    public boolean setMinDouble(double minValue) {this.minValue = minValue; return true; }
    public double getMaxDouble() { return this.maxValue; }
    public boolean setMaxDouble(double maxValue) {this.maxValue = maxValue; return true; }

    public boolean isDoubleValid(double value) { return value >= this.getMinDouble() && value <= this.getMaxDouble(); }

    @Override
    public boolean resetValue() { return this.setDouble(this.getDefaultDouble()); }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        obj.add(this.getKey(), new JsonPrimitive(this.getDouble()));
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if(element == null || !element.isJsonPrimitive()) return allowNonexistent;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if(!primitive.isNumber()) return false;
        return this.setDouble(primitive.getAsDouble());
    }

    BiConsumer<Double, String> onChange = (prevValue, name) -> {};
    public boolean onChange(BiConsumer<Double, String> method) {
        this.onChange = method;
        return true;
    }

    public String getChangeNarration() { return this.getChangeNarration.get(); }
    Supplier<String> getChangeNarration = () -> "";
    public boolean setChangeNarration(Supplier<String> method) {
        this.getChangeNarration = method;
        return true;
    }

    public String getChangeSliderNarration() { return this.getChangeSliderNarration.get(); }
    Supplier<String> getChangeSliderNarration = () -> "";
    public boolean setChangeSliderNarration(Supplier<String> method) {
        this.getChangeSliderNarration = method;
        return true;
    }

    public String getSaveNarration() { return this.getSaveNarration.get(); }
    Supplier<String> getSaveNarration = () -> "";
    public boolean setSaveNarration(Supplier<String> method) {
        this.getSaveNarration = method;
        return true;
    }

    public String getResetNarration() { return this.getResetNarration.get(); }
    Supplier<String> getResetNarration = () -> "";
    public boolean setResetNarration(Supplier<String> method) {
        this.getResetNarration = method;
        return true;
    }
}
