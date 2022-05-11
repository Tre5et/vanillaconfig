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

    static IntegerConfig integ1;

    static BooleanConfig bool1;
    static BooleanConfig bool6;
    static BooleanConfig bool7;

    static ConfigScreen screen;

    @Override
    public void onInitializeClient() {
        ButtonConfig button1 = new ButtonConfig("bbbbuuuuuu1111", "button", false, true, true);
        button1.onClickL(ExampleConfigEntry::onButtonClickedL);
        button1.onClickR(ExampleConfigEntry::onButtonClickedR);
        button1.setCustomWidth(400, 200);

        BooleanConfig boole1 = new BooleanConfig(false, "boole1", new String[]{"test", "line2"});
        IntegerConfig int1 = new IntegerConfig(42, 0, 5, "int1");
        integ1 = int1;
        int1.setCustomWidth(200, 100);
        IntegerConfig int2 = new IntegerConfig(33, -300, 1324, "int2", "", true);

        DoubleConfig double1 = new DoubleConfig(123.4567, -50, 1000000000, "double1");
        double1.setSlider(true);

        BooleanConfig boole2 = new BooleanConfig(true, "boole2");
        PageConfig page2 = new PageConfig("VC");
        page2.setFullWidth(false);

        KeybindConfig openConfig = new KeybindConfig(new int[] { 35 }, 1, 3, "open config");

        page2.addOption(boole2);

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
        list1.setSlider(true);

        ListConfig list2 = new ListConfig(new String[] {
                "testEntry0",
                "testEntry1"
        }, 0, "list2", new String[][]{
                new String[]{"item0", "line2"},
                new String[]{"item1", "line2"}
        });

        PageConfig page1 = new PageConfig("page1", new BaseConfig[] {
                double1,
                page2
        });
        page1.addOption(list1);

        BooleanConfig boolean1 = new BooleanConfig(false, "boolean1", "is true", "is false");
        boolean1.setFullWidth(false);
        BooleanConfig boolean2 = new BooleanConfig(false, "boolean2");
        boolean2.setFullWidth(false);
        BooleanConfig boolean3 = new BooleanConfig(false, "boolean3");
        boolean3.setFullWidth(false);
        BooleanConfig boolean4 = new BooleanConfig(false, "boolean4");
        BooleanConfig boolean5 = new BooleanConfig(false, "boolean5");
        BooleanConfig boolean6 = new BooleanConfig(false, "boolean6");
        boolean6.setFullWidth(false);
        BooleanConfig boolean7 = new BooleanConfig(false, "boolean7");
        BooleanConfig boolean8 = new BooleanConfig(false, "boolean8");
        boolean8.setFullWidth(false);
        BooleanConfig boolean9 = new BooleanConfig(false, "boolean9");
        boolean9.setFullWidth(false);
        BooleanConfig boolean10 = new BooleanConfig(false, "boolean10");
        BooleanConfig boolean11 = new BooleanConfig(false, "boolean11");
        BooleanConfig boolean12 = new BooleanConfig(false, "boolean12");
        BooleanConfig boolean13 = new BooleanConfig(false, "boolean14");
        BooleanConfig boolean14 = new BooleanConfig(false, "boolean15");

        bool1 = boolean1;
        bool6 = boolean6;
        bool7 = boolean7;
        boolean1.onChange(ExampleConfigEntry::onBoolean1Switched);
        boolean5.onChange(ExampleConfigEntry::onBoolean5Switched);

        boolean6.setEditable(false);

        StringConfig str1 = new StringConfig("empty", 0, 5, "str1");

        PageConfig config = new PageConfig("testConfigNew", new BaseConfig[] {
                openConfig,
                button1,
                boole1,
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

        config.setSaveName("myModConfig");
        config.migrateFileFrom("testConfig/testConfig.json");

        bigConfig = config;

        config.setPath("testConfig");
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
        boole2.migrateFrom("bool4");
        boole2.allowNonexistent(false);

        if(config.hasVersion()) VanillaConfigMod.LOGGER.info(config.getVersion().getAsString());

        VanillaConfigMod.LOGGER.info(((config.getVersion().matches(new ConfigVersion("0.0.1")))? "matches   " : "no match  ") +
                ((config.getVersion().biggerOrEqualTo(new ConfigVersion("0.0.1")))? "bigger  " : "no bigger  ") +
                ((config.getVersion().smallerThan(new ConfigVersion("0.0.1")))? "smaller    " : "no smaller "));


        VanillaConfigMod.LOGGER.info(str1.getString());

        double1.setDouble(9182.73645);
        list1.increment(true);

        screen = new ConfigScreen(bigConfig, MinecraftClient.getInstance().currentScreen);

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

    public static void onButtonClickedL(String key) {
        integ1.setFullWidth(!integ1.isFullWidth());
    }
    public static void onButtonClickedR(String key) {
        integ1.setInteger(integ1.getInteger() - 1);
    }


    public static void onBoolean5Switched(boolean prevValue, String key) {
        integ1.setSlider(!prevValue);
    }
    public static void onBoolean1Switched(boolean prevValue, String key) {
        integ1.setInteger(integ1.getInteger() + 1);
    }


    public static void resolveKey(String keybinding) {
        if(keybinding.equals("open config")) MinecraftClient.getInstance().setScreen(screen);
    }

}