package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.config.version.ConfigVersion;
import net.treset.vanillaconfig.tools.ClientTools;
import net.treset.vanillaconfig.tools.FileTools;
import net.treset.vanillaconfig.tools.helpers.TriConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PageConfig extends BaseConfig {
    final List<BaseConfig> options = new ArrayList<>();

    public PageConfig(String name, BaseConfig[] options, String description, boolean fullWidth, boolean editable, boolean displayed) {
        super(ConfigType.PAGE, name);

        this.setOptions(options);

        this.setDesc(description);

        this.setFullWidth(fullWidth);
        this.setEditable(editable);
        this.setDisplayed(displayed);

        this.setCustomWidth(250, 150);
    }
    public PageConfig(String name, BaseConfig[] options, String description) {
        this(name, options, description, true, true, true);
    }
    public PageConfig(String name, BaseConfig[] options) { this(name, options, "");}
    public PageConfig(String name, String description) { this(name, new BaseConfig[]{}, description); }
    public PageConfig(String name) {
        this(name, new BaseConfig[]{});
    }


    //general functions
    public BaseConfig[] getOptions() {
        return options.toArray(new BaseConfig[0]);
    }

    public boolean setOptions(BaseConfig[] options) {
        this.options.clear();
        this.options.addAll(Arrays.asList(options));
        return true;
    }

    public boolean addOption(BaseConfig option) {
        this.options.add(option);
        return true;
    }

    public boolean removeOption(String name) {
        for (int i = 0; i < this.options.size(); i++) {
            if (this.getOptions()[i].getKey().equals(name)) {
                return this.options.remove(i) != null;
            }
        }
        return false;
    }

    @Override
    public boolean resetValue() {
        boolean success = true;
        for (BaseConfig e : getOptions()) {
            if(!e.resetValue()) success = false;
        }
        return success;
    }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        JsonObject nestedObj = new JsonObject();

        for (BaseConfig option : this.getOptions()) {
            nestedObj = option.addToJson(nestedObj);
        }

        obj.add(this.getKey(), nestedObj);
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if (element == null || !element.isJsonObject()) return allowNonexistent;
        JsonObject object = element.getAsJsonObject();
        boolean succeded = true;
        for (BaseConfig e : this.getOptions()) {
            if (!e.loadFromJson(object, this.getBaseObject())) succeded = false;
        }
        return succeded;
    }

    Consumer<String> onClick = name -> {};
    public boolean onClick(Consumer<String> method) {
        this.onClick = method;
        return true;
    }
    public void click() {
        this.onClick.accept(this.getKey());
    }

    //save specific functions
    String saveName = "";
    String path = "";
    String migratePath = "";
    ConfigVersion version = new ConfigVersion("x.x.x");

    public boolean setSaveName(String name) { this.saveName = name; return true; }
    public String getSaveName() { return saveName; }
    public String getRealSaveName() { return (saveName.isEmpty())? this.getKey() : this.getSaveName(); }

    public boolean setPath(String path) { this.path = path; return true; }
    public String getPath() { return this.path; }
    public String getFullPath() { return FileTools.assemblePathString(this.getRealSaveName(), this.getPath()); }

    public String getMigratePath() { return this.migratePath; }
    public String getFullMigratePath() { return this.getMigrateDir() + "/" + this.getMigrateSaveName() + ".json"; }
    public String getMigrateSaveName() {
        if(!this.getMigratePath().endsWith(".json")) return this.getRealSaveName();
        String[] steps = this.getMigratePath().split("/");
        return steps[steps.length - 1].substring(0, steps[steps.length - 1].length() - 5);
    }
    public String getMigrateDir() {
        if(this.getMigratePath() == null || (!this.getMigratePath().contains("/") && this.getMigratePath().endsWith(".json"))) return "";
        if(!this.getMigratePath().endsWith(".json")) return this.getMigratePath();
        String[] steps = this.getMigratePath().split("/");

        StringBuilder dir = new StringBuilder();
        for(int i = 0; i < steps.length - 1; i++) {
            dir.append("/").append(steps[i]);
        }
        return dir.substring(1);
    }

    public boolean migrateFileFrom(String oldPath) {
        this.migratePath = oldPath;
        return true;
    }

    public File getFile(boolean migrate, String worldId) {
        if(migrate) {
            if(worldId == null) return FileTools.getConfigFile(this.getMigrateSaveName(), this.getMigrateDir());
            return FileTools.getConfigFile(worldId, this.getMigrateDir() + ((this.getPath().endsWith("/"))? "" : "/") + this.getMigrateSaveName());
        }
        if(worldId == null) return FileTools.getConfigFile(this.getRealSaveName(), this.getPath());
        return FileTools.getConfigFile(worldId, this.getPath() + ((path.endsWith("/"))? "" : "/") + this.getRealSaveName());
    }
    public File getFile(boolean migrate) { return this.getFile(migrate, null); }

    public boolean hasVersion() { return this.getVersion().isDefinite(); }
    public ConfigVersion getVersion() { return this.version; }
    public boolean setVersion(ConfigVersion version) { this.version = version; return true; }

    private boolean saveConfig(ConfigVersion version, File configFile) {
        if(!FileTools.fileExists(configFile)) {
            if(!FileTools.createFile(configFile)) return false;
        }

        JsonObject obj = new JsonObject();

        for (BaseConfig option : this.getOptions()) {
            obj = option.addToJson(obj);
        }

        FileTools.writeVersion(this.getKey(), version);

        return FileTools.writeJsonToFile(obj, configFile);
    }
    public boolean save(ConfigVersion version, File configFile) {
        boolean success = this.saveConfig(version, configFile);
        this.onSave.accept(success, this.getKey());
        return success;
    }
    public boolean save(ConfigVersion version) {
        return this.save(version, this.getFile(false));
    }
    public boolean save() {
        return this.save(this.getVersion());
    }

    public boolean savePerWorld(ConfigVersion version, String worldId) {
        boolean success = this.saveConfig(version, this.getFile(false, worldId));
        this.onSavePerWorld.accept(success, worldId, this.getKey());
        return success;
    }
    public boolean savePerWorld(ConfigVersion version) {
        String worldID = ClientTools.getWorldId();
        if(worldID == null) return false;
        return savePerWorld(version, worldID);
    }
    public boolean savePerWorld(String worldId) {
        return this.savePerWorld(this.getVersion(), worldId);
    }
    public boolean savePerWorld() {
        return this.savePerWorld(this.getVersion());
    }

    private boolean loadConfig(File config, File migrateConfig, boolean perWorld) {
        if(migrateConfig != null) {
            if(this.loadOptions(migrateConfig, false)) {
                if(!FileTools.removeFile(migrateConfig)) return false;

                if(perWorld) return this.savePerWorld();
                return this.save();
            }
        }
        if(config == null) return false;
        return this.loadOptions(config, true);
    }
    public boolean load(File config, File migrateConfig) {
        boolean success = this.loadConfig(config, migrateConfig, false);
        this.onLoad.accept(success, this.getKey());
        return success;
    }
    public boolean load(File config) {
        return this.load(config, null);
    }
    public boolean load() {
        return this.load(this.getFile(false), this.getFile(true));
    }

    public boolean loadPerWorld(String worldId) {
        boolean success = this.loadConfig(this.getFile(false, worldId), this.getFile(true, worldId), true);
        this.onLoadPerWorld.accept(success, worldId, this.getKey());
        return success;
    }
    public boolean loadPerWorld() {
        String worldID = ClientTools.getWorldId();
        if(worldID == null) return false;
        return loadPerWorld(worldID);
    }

    public boolean loadOptions(File configFile, boolean ignoreNone) {
        if(!FileTools.fileExists(configFile)) return ignoreNone;

        JsonObject obj = FileTools.readJsonFile(configFile);

        if(obj == null || !obj.isJsonObject()) return false;

        boolean success = true;
        for (BaseConfig e : this.getOptions()) {
            if(!e.loadFromJson(obj, obj)) success = false;
        }

        return success;
    }
    public boolean loadVersion() {
        return this.loadVersionOf(this.getKey());
    }

    public boolean loadVersionOf(String name) {
        this.setVersion(FileTools.readVersion(name));
        return this.hasVersion();
    }

    BiConsumer<Boolean, String> onLoad = (success, name) -> {};
    public void onLoad(BiConsumer<Boolean, String> method) {
        this.onLoad = method;
    }
    BiConsumer<Boolean, String> onSave = (success, name) -> {};
    public void onSave(BiConsumer<Boolean, String> method) {
        this.onSave = method;
    }
    TriConsumer<Boolean, String, String> onLoadPerWorld = (success, worldId, name) -> {};
    public void onLoadPerWorld(TriConsumer<Boolean, String, String> method) {
        this.onLoadPerWorld = method;
    }
    TriConsumer<Boolean, String, String> onSavePerWorld = (success, worldId, name) -> {};
    public void onSavePerWorld(TriConsumer<Boolean, String, String> method) {
        this.onSavePerWorld = method;
    }
}
