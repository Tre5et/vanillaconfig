package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.tools.TextTools;

import java.util.function.Consumer;

public class ButtonConfig extends BaseConfig {
    public ButtonConfig(String name, String desc, boolean fullWidth, boolean editable, boolean displayed) {
        super(ConfigType.BUTTON, name);

        this.setDesc(desc);

        this.setFullWidth(fullWidth);
        this.setEditable(editable);
        this.setDisplayed(displayed);

        this.setSelectNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.button.select"), this.getName()));
        this.setActivateNarration(() -> String.format(TextTools.translateOrDefault("vanillaconfig.narration.button.activate"), this.getName()));
    }
    public ButtonConfig(String name, String desc) {
        this(name, desc, true, true, true);
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
