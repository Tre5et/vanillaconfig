package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.SlideableConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;

import java.util.function.BiConsumer;

public class DoubleConfig extends SlideableConfig {
    double value = 0;
    double minValue = 0;
    double maxValue = 0;
    double defValue = 0;

    public DoubleConfig(double defaultValue, double minValue, double maxValue, String name, String desc) {
        super(ConfigType.DOUBLE, name);

        this.setDesc(desc);

        this.setMinDouble(minValue);
        this.setMaxDouble(maxValue);

        if(this.isDoubleValid(defaultValue)) this.defValue = defaultValue;
        else this.defValue = minValue;
        this.resetValue();
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
}
