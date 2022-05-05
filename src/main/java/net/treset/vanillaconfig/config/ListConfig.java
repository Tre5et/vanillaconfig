package net.treset.vanillaconfig.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.vanillaconfig.config.base.SlideableConfig;
import net.treset.vanillaconfig.config.config_type.ConfigType;
import net.treset.vanillaconfig.tools.TextTools;
import net.treset.vanillaconfig.tools.helpers.TriConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConfig extends SlideableConfig {
    List<String> options = new ArrayList<>();
    int optionIndex = 0;
    int defOptionIndex = 0;

    final List<String[]> descs = new ArrayList<>();

    public ListConfig(String[] options, int defaultOptionIndex, String name, String[][] descriptions) {
        super(ConfigType.LIST, name);

        this.setOptions(options);

        this.defOptionIndex = defaultOptionIndex;
        this.resetValue();

        this.setDescs(descriptions);
    }
    public ListConfig(String[] options, String defaultOption, String name, String[][] descriptions) {
        this(options,
            (Arrays.asList(options).contains(defaultOption))? Arrays.asList(options).indexOf(defaultOption) : 0,
            name, descriptions);
    }
    public ListConfig(String[] options, int defaultOptionIndex, String name, String[] descriptions) {
        this(options, defaultOptionIndex, name, TextTools.stringArrayArrayFromStringArray(descriptions));
    }
    public ListConfig(String[] options, String defaultOption, String name, String[] descriptions) {
        this(options, defaultOption, name, TextTools.stringArrayArrayFromStringArray(descriptions));
    }
    public ListConfig(String[] options, int defaultOptionIndex, String name, String description) {
        this(options, defaultOptionIndex, name, new String[]{description});
    }
    public ListConfig(String[] options, String defaultOption, String name, String description) {
        this(options, defaultOption, name, new String[]{description});
    }
    public ListConfig(String[] options, int defaultOptionIndex, String name) {
        this(options, defaultOptionIndex, name, new String[0]);
    }
    public ListConfig(String[] options, String defaultOption, String name) {

        this(options, defaultOption, name, new String[0]);
    }

    @Override
    public double getDoubleValue() {
        return this.getOptionIndex();
    }
    @Override
    public double getMaxDoubleValue() {
        return this.getOptions().length - 1;
    }
    @Override
    public double getMinDoubleValue() {
        return 0D;
    }

    public int getOptionIndex() { return this.optionIndex; }
    public boolean setOptionIndex(int index) {
        if(this.getOptionIndex() != index && this.getOptions().length > index && index >= 0) {
            int prevIndex = this.getOptionIndex();
            this.optionIndex = index;
            this.updateDesc();
            this.onChange.accept(prevIndex, this.getOption(prevIndex), this.getKey());
            return true;
        }
        return false;
    }

    public String getOption(int index) {
        if(index >= this.getOptions().length || index < 0) return "";
        return this.getOptions()[index];
    }
    public String getOption() { return this.getOption(this.getOptionIndex()); }
    public boolean setOption(String option) {
        if(List.of(this.getOptions()).contains(option)) {
            return this.setOptionIndex(List.of(this.getOptions()).indexOf(option));
        }
        return false;
    }

    public int getDefaultOptionIndex() { return this.defOptionIndex; }
    public String getDefaultOption() { return this.getOption(this.getDefaultOptionIndex()); }

    public String[] getOptions() { return options.toArray(new String[0]); }
    public boolean setOptions(String[] options) {
        this.options = Arrays.asList(options);
        return true;
    }

    @Override
    public boolean hasDesc() {
        return this.getDescs().length != 0;
    }
    private boolean updateDesc() {
        if(!this.hasDesc()) this.setDesc("");
        else if(this.getDescs().length <= this.getOptionIndex()) {
            this.setDesc(this.getDescs()[0]);
        } else {
            this.setDesc(this.getDescs()[this.getOptionIndex()]);
        }
        return true;
    }
    public String[][] getDescs() { return this.descs.toArray(new String[][]{}); }
    public boolean setDescs(String[][] descriptions) {
        this.descs.clear();
        this.descs.addAll(Arrays.asList(descriptions));
        this.updateDesc();
        return true;
    }

    public boolean increment(boolean forward) {
        if(forward) {
            this.setOptionIndex((this.getOptionIndex() + 1) % this.getOptions().length);
        } else {
            this.setOptionIndex((this.getOptions().length + this.getOptionIndex() - 1) % this.getOptions().length);
        }
        return true;
    }

    @Override
    public boolean resetValue() { return this.setOptionIndex(this.getDefaultOptionIndex()); }

    @Override
    public JsonObject addToJson(JsonObject obj) {
        obj.add(this.getKey(), new JsonPrimitive(this.getOption()));
        return obj;
    }

    @Override
    public boolean loadJsonElement(JsonElement element, boolean allowNonexistent) {
        if(element == null || !element.isJsonPrimitive()) return allowNonexistent;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if(!primitive.isString()) return false;
        return this.setOption(primitive.getAsString());
    }

    TriConsumer<Integer, String, String> onChange = (prevIndex, prevValue, name) -> {};
    public boolean onChange(TriConsumer<Integer, String, String> method) {
        this.onChange = method;
        return true;
    }
}
