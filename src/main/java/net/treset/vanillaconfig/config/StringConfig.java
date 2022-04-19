package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;

import java.util.function.BiConsumer;

public class StringConfig extends BaseConfig {
    String value = "";
    int minLength = 0;
    int maxLength = 0;
    String defValue = "";

    public StringConfig(String defaultValue, int minLength, int maxLength, String name, String description) {
        super(ConfigType.STRING, name);

        this.setDesc(description);

        this.setMinLength(minLength);
        this.setMaxLength(maxLength);

        if(this.isStringValid(defaultValue)) this.defValue = defaultValue;
        else while(this.defValue.length() < this.getMinLength()) this.defValue += " ";
        this.resetValue();
    }
    public StringConfig(String defaultValue, int minLength, int maxLength, String name) {
        this(defaultValue, minLength, maxLength, name, "");
    }

    public String getString() { return this.value; }
    public boolean setString(String value) {
        if(!this.isStringValid(value)) return false;
        if(!this.getString().equals(value.strip())) {
            String prevValue = this.getString().strip();
            this.value = value.strip();
            this.onChange.accept(prevValue, this.getKey());
        }
        return true;
    }
    public String getDefaultString() { return this.defValue; }
    public int getMinLength() { return this.minLength; }
    public boolean setMinLength(int minLength) { this.minLength = minLength; return true; }
    public int getMaxLength() { return this.maxLength; }
    public boolean setMaxLength(int maxLength) { this.maxLength = maxLength; return true; }

    public boolean isStringValid(String value) {
        value = value.strip();
        return value.length() >= this.getMinLength() && value.length() <= this.getMaxLength();
    }

    @Override
    public boolean resetValue() { return this.setString(this.getDefaultString()); }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        obj.add(this.getKey(), new JsonPrimitive(this.getString()));
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if(element == null || !element.isJsonPrimitive()) return allowNonexistent;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if(!primitive.isString()) return false;
        return this.setString(primitive.getAsString());
    }

    BiConsumer<String, String> onChange = (prevValue, name) -> {};
    public boolean onChange(BiConsumer<String, String> method) {
        this.onChange = method;
        return true;
    }
}
