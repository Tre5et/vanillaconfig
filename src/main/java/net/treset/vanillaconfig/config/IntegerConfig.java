package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.SlideableConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.tools.TextTools;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class IntegerConfig extends SlideableConfig {
    int value = 0;
    int minValue = 0;
    int maxValue = 0;
    int defValue = 0;

    public IntegerConfig(int defaultValue, int minValue, int maxValue, String name, String desc, boolean fullWidth, boolean editable, boolean displayed, boolean slider) {
        super(ConfigType.INTEGER, name);

        this.setDesc(desc);

        this.setMinInteger(minValue);
        this.setMaxInteger(maxValue);

        if(this.isIntValid(defaultValue)) this.defValue = defaultValue;
        else this.defValue = this.getMinInteger();

        this.setFullWidth(fullWidth);
        this.setEditable(editable);
        this.setDisplayed(displayed);
        this.setSlider(slider);

        this.setSelectNarration(() -> {
            if(this.isSlider())
                return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.slider.select"), this.getName(), this.getInteger());
            return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.select"), this.getName(), this.getInteger());
        });
        this.setActivateNarration(() -> {
            if(this.isSlider()) return "";
            return String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.activate"), this.getName(), this.getInteger());
        });
        this.setChangeSliderNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.slider.change"), this.getInteger()));
        this.setSaveNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.save"), this.getName(), this.getInteger()));
        this.setResetNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.int.reset"), this.getName(), this.getInteger()));

        this.resetValue();
    }
    public IntegerConfig(int defaultValue, int minValue, int maxValue, String name, String desc, boolean slider) {
        this(defaultValue, minValue, maxValue, name, desc, true, true, true, slider);
    }
    public IntegerConfig(int defaultValue, int minValue, int maxValue, String name, String desc) {
        this(defaultValue, minValue, maxValue, name, desc, false);
    }
    public IntegerConfig(int defaultValue, int minValue, int maxValue, String name) {
        this(defaultValue, minValue, maxValue, name,  "");
    }

    @Override
    public double getDoubleValue() {
        return this.getInteger();
    }
    @Override
    public double getMaxDoubleValue() {
        return this.getMaxInteger();
    }
    @Override
    public double getMinDoubleValue() {
        return this.getMinInteger();
    }

    @Override
    public boolean setDoubleValue(double value) {
        int intVal = (int)Math.rint(value);
        return this.setInteger(intVal);
    }

    public int getInteger() { return this.value; }
    public boolean setInteger(int value) {
        if(!this.isIntValid(value)) return false;
        if(this.getInteger() != value) {
            int prevValue = this.getInteger();
            this.value = value;
            this.onChange.accept(prevValue, this.getKey());
        }
        return true;
    }
    public int getDefaultInteger() { return this.defValue; }
    public int getMinInteger() { return this.minValue; }
    public boolean setMinInteger(int minValue) { this.minValue = minValue; return true; }
    public int getMaxInteger() { return this.maxValue; }
    public boolean setMaxInteger(int maxValue) { this.maxValue = maxValue; return true; }

    public boolean isIntValid(int value) {
        return value >= this.getMinInteger() && value <= this.getMaxInteger();
    }

    @Override
    public boolean resetValue() { return this.setInteger(this.getDefaultInteger()); }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        obj.add(this.getKey(), new JsonPrimitive(this.getInteger()));
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if(element == null || !element.isJsonPrimitive()) return allowNonexistent;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if(!primitive.isNumber() || primitive.getAsDouble() % 1 != 0) return false;
        return this.setInteger(primitive.getAsInt());
    }

    BiConsumer<Integer, String> onChange = (prevValue, name) -> {};
    public boolean onChange(BiConsumer<Integer, String> method) {
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
