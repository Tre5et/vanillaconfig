package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;

import java.util.function.BiConsumer;

public class BooleanConfig extends BaseConfig {
    String[] descTrue = new String[]{};
    String[] descFalse = new String[]{};
    boolean value = false;
    boolean defValue = false;

    public BooleanConfig(boolean defaultValue, String name, String[] descTrue, String[] descFalse) {
        super(ConfigType.BOOLEAN, name);

        this.descTrue = descTrue;
        this.descFalse = descFalse;

        this.defValue = defaultValue;
        this.resetValue();
    }
    public BooleanConfig(boolean defaultValue, String name, String descTrue, String descFalse) {
        this(defaultValue, name, new String[]{descTrue}, new String[]{descFalse});
    }
    public BooleanConfig(boolean defaultValue, String name, String[] desc) {
        this(defaultValue, name, desc, desc);
    }
    public BooleanConfig(boolean defaultValue, String name, String desc) {
        this(defaultValue, name, new String[]{desc});
    }
    public BooleanConfig(boolean defaultValue, String name) {
        this(defaultValue, name, new String[]{});
    }

    public boolean getBoolean() { return this.value; }
    public boolean setBoolean(boolean value) {
        if(this.getBoolean() != value) {
            this.value = value;
            this.onChange.accept(!this.value, this.getKey());
        }
        return true;
    }
    public boolean getDefaultBoolean() { return this.defValue; }

    @Override
    public boolean hasDesc() { return !(this.getDescFalse().length < 1 && this.getDescTrue().length < 1); }
    @Override
    public String[] getDesc() { return (this.getBoolean()) ? this.getDescTrue() : this.getDescFalse(); }
    @Override
    public boolean setDesc(String[] desc) {
        return this.setDescTrue(desc) && this.setDescFalse(desc);
    }

    public String[] getDescTrue() { return this.descTrue; }
    public boolean setDescTrue(String[] desc) {
        this.descTrue = desc;
        return true;
    }
    public boolean setDescTrue(String desc) { return this.setDescTrue(new String[]{desc}); }

    public String[] getDescFalse() { return this.descFalse; }
    public boolean setDescFalse(String[] desc) {
        this.descFalse = desc;
        return true;
    }
    public boolean setDescFalse(String desc) { return this.setDescFalse(new String[]{desc}); }

    @Override
    public boolean resetValue() { return this.setBoolean(this.getDefaultBoolean()); }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        obj.add(this.getKey(), new JsonPrimitive(this.getBoolean()));
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if(element == null || !element.isJsonPrimitive()) return allowNonexistent;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if(!primitive.isBoolean()) return false;
        return this.setBoolean(primitive.getAsBoolean());
    }

    BiConsumer<Boolean, String> onChange = (prevValue, name) -> {};
    public boolean onChange(BiConsumer<Boolean, String> method) {
        this.onChange = method;
        return true;
    }
}
