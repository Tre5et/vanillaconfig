package net.treset.vanillaconfig.config.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.tools.FileTools;
import net.treset.vanillaconfig.tools.TextTools;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseConfig {
    String name;
    String[] desc = new String[]{};
    boolean allowNonexistent = true;
    String migrateKey = "";
    ConfigType type;
    JsonObject baseObject = null;
    boolean editable = true;
    boolean displayed = true;
    boolean fullWidth = true;
    int widthFull = 310;
    int widthHalf = 150;

    public BaseConfig(ConfigType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() { return TextTools.translateOrDefault(this.name); }
    public String getKey() { return this.name; }

    public boolean hasDesc() { return this.desc.length > 0 && !this.desc[0].isEmpty(); }
    public String[] getDesc() { return this.desc; }
    public boolean setDesc(String[] desc) {
        this.desc = desc;
        return true;
    }
    public boolean setDesc(String desc) {
        return this.setDesc(new String[]{desc});
    }

    public ConfigType getType() { return this.type; }

    public boolean migrateFrom(String oldLocation) {
        this.migrateKey = oldLocation;
        return true;
    }
    public boolean shouldMigrate() { return !this.getMigrateKey().isEmpty(); }
    public String getMigrateKey() { return this.migrateKey; }

    public boolean isNonexistentAllowed() { return this.allowNonexistent; }
    public boolean allowNonexistent(boolean allow) {
        this.allowNonexistent = allow;
        return true;
    }

    public boolean isDisplayed() { return this.displayed; }
    public boolean setDisplayed(boolean displayed) { this.displayed = displayed; return true; }

    public boolean isEditable() { return this.editable; }
    public boolean setEditable(boolean editable) { this.editable = editable; return true; }

    public boolean isFullWidth() { return this.fullWidth; }
    public boolean setFullWidth(boolean fullWidth) { this.fullWidth = fullWidth; return true; }

    public int[] getWidth() { return new int[]{widthFull, widthHalf}; }
    public boolean setCustomWidth(int widthFull, int widthHalf) {
        if(widthFull <= 0 || widthHalf <= 0) return false;
        this.widthFull = widthFull;
        this.widthHalf = widthHalf;
        return true;
    }

    public boolean resetValue() { return false; }

    public JsonObject getBaseObject() { return this.baseObject; }
    private boolean setBaseObject(JsonObject obj) { this.baseObject = obj; return true; }

    private JsonElement getMigrateObject(JsonObject obj) {
        if(!this.getMigrateKey().contains("/")) return obj.get(this.getMigrateKey());
        return FileTools.findJsonElementFromPath(this.getBaseObject(), this.getMigrateKey());
    }

    public JsonObject addToJson(JsonObject obj) {
        return obj;
    }

    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) { return false; }

    public boolean loadFromJson(JsonObject obj, JsonObject baseObj) {
        this.setBaseObject(baseObj);
        if(this.shouldMigrate())
            if(this.loadJsonElement(this.getMigrateObject(obj), false)) return true;
        return this.loadJsonElement(obj.get(this.getKey()), this.isNonexistentAllowed());
    }

    public String getSelectNarration() { return this.getSelectNarration.get(); }
    private Supplier<String> getSelectNarration = () -> "";
    public boolean setSelectNarration(Supplier<String> method) {
        this.getSelectNarration = method;
        return true;
    }

    public String getActivateNarration() { return this.getActivateNarration.get(); }
    private Supplier<String> getActivateNarration = () -> "";
    public boolean setActivateNarration(Supplier<String> method) {
        this.getActivateNarration = method;
        return true;
    }

}
