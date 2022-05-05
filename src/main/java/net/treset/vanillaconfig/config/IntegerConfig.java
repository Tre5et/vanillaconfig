package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.SlideableConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;

import java.util.function.BiConsumer;

public class IntegerConfig extends SlideableConfig {
    int value = 0;
    int minValue = 0;
    int maxValue = 0;
    int defValue = 0;

    public IntegerConfig(int defaultValue, int minValue, int maxValue, String name, String desc) {
        super(ConfigType.INTEGER, name);

        this.setDesc(desc);

        this.setMinInteger(minValue);
        this.setMaxInteger(maxValue);

        if(this.isIntValid(defaultValue)) this.defValue = defaultValue;
        else this.defValue = this.getMinInteger();
        this.resetValue();
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
}
