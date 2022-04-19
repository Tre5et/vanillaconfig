package net.treset.vanillaconfig;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.treset.vanillaconfig.config.*;
import net.treset.vanillaconfig.config.base.BaseConfig;
import net.treset.vanillaconfig.config.managers.SaveLoadManager;
import net.treset.vanillaconfig.config.version.ConfigVersion;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.tools.TextTools;

public class ExampleConfigEntry implements ClientModInitializer {

    static PageConfig bigConfig;

    static BooleanConfig bool6;
    static BooleanConfig bool7;

    @Override
    public void onInitializeClient() {
        BooleanConfig bool1 = new BooleanConfig(false, "bool1", new String[]{"test", "line2"});
        IntegerConfig int1 = new IntegerConfig(42, 0, 1000000000, "int1");
        IntegerConfig int2 = new IntegerConfig(33, -300, 1324, "int2");

        DoubleConfig double1 = new DoubleConfig(123.4567, -50, 1000000000, "double1");

        BooleanConfig bool2 = new BooleanConfig(true, "bool2");
        PageConfig page2 = new PageConfig("VC");

        KeybindConfig openConfig = new KeybindConfig(new int[] { 35 }, 1, 3, "open config");

        page2.addOption(bool2);

        ListConfig list1 = new ListConfig(new String[] {
                "testEntry0",
                "testEntry1",
                "testEntry2",
                "testEntry3"
        }, 0, "list1", new String[]{
                "isOpt0",
                "isOpt1",
                "isOpt2",
                "isOpt3"
        });

        ListConfig list2 = new ListConfig(new String[] {
                "testEntry0",
                "testEntry1"
        }, 0, "list1", new String[][]{
                new String[]{"item0", "line2"},
                new String[]{"item1", "line2"}
        });

        PageConfig page1 = new PageConfig("page1", new BaseConfig[] {
                double1,
                page2
        });
        page1.addOption(list1);

        BooleanConfig boolean1 = new BooleanConfig(false, "boolean1", "is true", "is false");
        BooleanConfig boolean2 = new BooleanConfig(false, "boolean2");
        BooleanConfig boolean3 = new BooleanConfig(false, "boolean3");
        BooleanConfig boolean4 = new BooleanConfig(false, "boolean4");
        BooleanConfig boolean5 = new BooleanConfig(false, "boolean5");
        BooleanConfig boolean6 = new BooleanConfig(false, "boolean6");
        BooleanConfig boolean7 = new BooleanConfig(false, "boolean7");
        BooleanConfig boolean8 = new BooleanConfig(false, "boolean8");
        BooleanConfig boolean9 = new BooleanConfig(false, "boolean9");
        BooleanConfig boolean10 = new BooleanConfig(false, "boolean10");
        BooleanConfig boolean11 = new BooleanConfig(false, "boolean11");
        BooleanConfig boolean12 = new BooleanConfig(false, "boolean12");
        BooleanConfig boolean13 = new BooleanConfig(false, "boolean14");
        BooleanConfig boolean14 = new BooleanConfig(false, "boolean15");

        boolean5.onChange(ExampleConfigEntry::onBoolean5Switched);
        bool6 = boolean6;
        bool7 = boolean7;

        boolean6.setEditable(false);

        StringConfig str1 = new StringConfig("empty", 0, 5, "str1");

        PageConfig config = new PageConfig("testConfig", new BaseConfig[] {
                openConfig,
                bool1,
                int1,
                page1,
                list2,
                int2,
                str1,
                boolean1,
                boolean2,
                boolean3,
                boolean4,
                boolean5,
                boolean6,
                boolean7,
                boolean8,
                boolean9,
                boolean10,
                boolean11,
                boolean12,
                boolean13,
                boolean14
        });

        bigConfig = config;

        config.setPath("testConfig");
        config.migrateFileFrom("testConfig.json");
        SaveLoadManager.globalSaveConfig(config);

        page1.setPath("testConfig");
        page1.migrateFileFrom("page1.json");
        SaveLoadManager.worldSaveConfig(page1);

        config.onLoad(ExampleConfigEntry::onLoad);
        config.onSave(ExampleConfigEntry::onSave);
        page1.onLoadPerWorld(ExampleConfigEntry::onLoadWorld);
        page1.onSavePerWorld(ExampleConfigEntry::onSaveWorld);

        config.loadVersion();

        page1.migrateFrom("page2");
        page1.allowNonexistent(false);
        int1.allowNonexistent(false);
        bool2.migrateFrom("bool4");
        bool2.allowNonexistent(false);

        boolean2.setDisplayed(false);
        boolean3.setDisplayed(false);

        if(config.hasVersion()) VanillaConfigMod.LOGGER.info(config.getVersion().getAsString());

        VanillaConfigMod.LOGGER.info(((config.getVersion().matches(new ConfigVersion("0.0.1")))? "matches   " : "no match  ") +
                ((config.getVersion().biggerOrEqualTo(new ConfigVersion("0.0.1")))? "bigger  " : "no bigger  ") +
                ((config.getVersion().smallerThan(new ConfigVersion("0.0.1")))? "smaller    " : "no smaller "));


        VanillaConfigMod.LOGGER.info(str1.getString());

        double1.setDouble(9182.73645);
        list1.increment(true);

        openConfig.onPressed(ExampleConfigEntry::resolveKey);
    }

    public static void onLoad(boolean success, String key) {
        VanillaConfigMod.LOGGER.info("loaded " + key + " " + TextTools.booleanToString(success));
    }
    public static void onSave(boolean success, String key) {
        VanillaConfigMod.LOGGER.info("saved " + key + " " + TextTools.booleanToString(success));
    }

    public static void onLoadWorld(boolean success, String worldId, String key) {
        VanillaConfigMod.LOGGER.info("loaded " + key + " " + TextTools.booleanToString(success) + " " + worldId);
    }
    public static void onSaveWorld(boolean success, String worldId, String key) {
        VanillaConfigMod.LOGGER.info("saved " + key + " " + TextTools.booleanToString(success) + " " + worldId);
    }


    public static void onBoolean5Switched(boolean prevValue, String key) {
        bool6.setEditable(!prevValue);
        bool7.setDisplayed(!prevValue);
        VanillaConfigMod.LOGGER.info(key);
    }


    public static void resolveKey(String keybinding) {
        if(keybinding.equals("open config")) MinecraftClient.getInstance().setScreen(new ConfigScreen(bigConfig, MinecraftClient.getInstance().currentScreen));
    }

}