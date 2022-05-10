package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;

import java.util.function.Consumer;

public class ButtonConfig extends BaseConfig {
    public ButtonConfig(String name, String desc) {
        super(ConfigType.BUTTON, name);

        this.setDesc(desc);
    }
    public ButtonConfig(String name) {
        this(name, "");
    }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        return true;
    }

    public void invokeOnClickL() {
        this.onClickL.accept(this.getKey());
    }
    Consumer<String> onClickL = (name) -> {};
    public boolean onClickL(Consumer<String> method) {
        this.onClickL = method;
        return true;
    }

    public void invokeOnClickR() {
        this.onClickR.accept(this.getKey());
    }
    Consumer<String> onClickR = (name) -> {};
    public boolean onClickR(Consumer<String> method) {
        this.onClickR = method;
        return true;
    }
}
