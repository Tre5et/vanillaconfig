package net.treset.vanillaconfig.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.tools.KeybindTools;
import net.treset.vanillaconfig.tools.helpers.Keybind;
import net.treset.vanillaconfig.tools.helpers.KeybindContext;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeybindConfig extends BaseConfig {
    int[] keys = new int[]{};
    int minAmount = 0;
    int maxAmount = 0;
    int[] defKeys = keys;
    Keybind keybind;

    public KeybindConfig(int[] defaultKeys, int minAmount, int maxAmount, String name, String desc) {
        super(ConfigType.KEYBIND, name);

        this.setDesc(desc);

        this.setMinAmount(minAmount);
        this.setMaxAmount(maxAmount);

        if(this.isKeysValid(defaultKeys)) this.defKeys = defaultKeys;
        else {
            this.defKeys = new int[this.getMinAmount()];
            Arrays.fill(this.defKeys, 0);
        }
        this.keybind = new Keybind(this.getKey(), this.getDefaultKeys(), KeybindContext.IN_GAME);
        this.resetValue();

        KeybindTools.addKeybind(this.keybind);
    }
    public KeybindConfig(int[] defaultKeys, int minAmount, int maxAmount, String name) {
        this(defaultKeys, minAmount, maxAmount, name, "");
    }

    public int[] getKeys() { return this.keys; }
    public boolean setKeys(int[] keys) {
        if(!this.isKeysValid(keys)) return false;
        if(this.getKeys() != keys) {
            int[] prevKeys = this.getKeys();
            this.keys = keys;
            this.keybind.setKeys(this.keys);
            this.onChange.accept(prevKeys, this.getKey());
        }
        return true;
    }

    public Keybind getKeybind() { return this.keybind; }

    public int[] getDefaultKeys() { return this.defKeys; }
    public int getMinAmount() { return this.minAmount; }
    public boolean setMinAmount(int minAmount) { this.minAmount = minAmount; return true; }
    public int getMaxAmount() { return this.maxAmount; }
    public boolean setMaxAmount(int maxAmount) { this.maxAmount = maxAmount; return true; }

    public boolean isKeysValid(int[] keys) { return keys.length >= this.getMinAmount() && keys.length <= this.getMaxAmount(); }

    public KeybindContext getContext() { return this.getKeybind().getContext(); }
    public boolean setContext(KeybindContext context) {
        return this.getKeybind().setContext(context);
    }

    @Override
    public boolean resetValue() { return this.setKeys(this.getDefaultKeys()); }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        JsonArray keysObj = new JsonArray();
        for (int e : this.getKeys()) {
            keysObj.add(e);
        }
        obj.add(this.getKey(), keysObj);
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if(element == null || !element.isJsonArray()) return allowNonexistent;
        JsonArray jsonArray = element.getAsJsonArray();
        int[] keys = new int[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement e = jsonArray.get(i);
            if(!e.isJsonPrimitive() || !((JsonPrimitive)e).isNumber() || e.getAsDouble() % 1 != 0) return false;
            keys[i] = (int)e.getAsDouble();
        }

        return this.setKeys(keys);
    }

    public boolean onPressed(Consumer<String> method) {
        return this.getKeybind().onPressed(method);
    }
    public void resolve() { this.getKeybind().resolve(); }

    BiConsumer<int[], String> onChange = (prevKeys, name) -> {};
    public boolean onChange(BiConsumer<int[], String> method) {
        this.onChange = method;
        return true;
    }
 }
