package net.treset.vanillaconfig.config.managers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.treset.vanillaconfig.VanillaConfigMod;
import net.treset.vanillaconfig.config.PageConfig;
import net.treset.vanillaconfig.tools.ClientTools;

import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {
    private static boolean verboseLoading = false;

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            onClientStarted();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            onClientStopping();
        });
    }

    public static boolean getVerbose() { return verboseLoading; }
    public static boolean setVerbose(boolean verbose) { verboseLoading = verbose; return true; }

    static List<PageConfig> globalConfigs = new ArrayList<>();
    static List<PageConfig> worldConfigs = new ArrayList<>();

    public static PageConfig[] getGlobalSaveConfigs() { return globalConfigs.toArray(new PageConfig[] {}); }
    public static boolean globalSaveConfig(PageConfig config) {
        for (PageConfig e : globalConfigs) {
            if(e.getKey().equals(config.getKey())) return false; //check that config name isn't overridden
        }
        return globalConfigs.add(config);
    }
    public static boolean removeGlobalSaveConfig(PageConfig config) {
        return globalConfigs.remove(config);
    }

    public static PageConfig[] getWorldSaveConfigs() { return worldConfigs.toArray(new PageConfig[] {}); }
    public static boolean worldSaveConfig(PageConfig config) {
        for (PageConfig e : worldConfigs) {
            if(e.getKey().equals(config.getKey())) return false; //check that config name isn't overridden
        }
        return worldConfigs.add(config);
    }
    public static boolean removeWorldSaveConfig(PageConfig config) {
        return worldConfigs.remove(config);
    }

    private static boolean loadAllGlobal() {
        boolean success = true;
         for (PageConfig e : globalConfigs) {
            if(!e.load()) success = false;
        }
        return success;
    }
    private static boolean saveAllGlobal() {
        boolean success = true;
        for (PageConfig e : globalConfigs) {
            if(!e.save()) success = false;
        }
        return success;
    }

    private static boolean loadAllWorld(String worldId) {
        boolean success = true;
        for (PageConfig e : worldConfigs) {
            if(globalConfigs.contains(e)) e.save();
            if(!e.loadPerWorld(worldId)) success = false;
        }
        return success;
    }
    private static boolean saveAllWorld(String worldId) {
        boolean success = true;
        for (PageConfig e : worldConfigs) {
            if(!e.savePerWorld(worldId)) success = false;
            if(globalConfigs.contains(e)) e.load();
            else e.resetValue();
        }
        return success;
    }

    public static void onWorldOpened(String worldId) {
        boolean succ = loadAllWorld(worldId);

        if(verboseLoading) {
            if(succ) VanillaConfigMod.LOGGER.info("VanillaConfig loaded " + worldConfigs.size() + " per world configs.");
            else VanillaConfigMod.LOGGER.warn("VanillaConfig failed to load per world configs.");
        }
    }
    public static void onWorldClosed(String worldId) {
        boolean succ = saveAllWorld(worldId);
        if(verboseLoading) {
            if(succ) VanillaConfigMod.LOGGER.info("VanillaConfig saved " + worldConfigs.size() + " per world configs.");
            else VanillaConfigMod.LOGGER.warn("VanillaConfig failed to save per world configs.");
        }
    }

    public static void onWorldOpened() {
        if(ClientTools.getWorldId() != null) onWorldOpened(ClientTools.getWorldId());
    }
    public static void onWorldClosed() {
        if(ClientTools.getWorldId() != null) onWorldClosed(ClientTools.getWorldId());
    }
    public static void onClientStarted() {
        boolean succ = loadAllGlobal();
        if(verboseLoading) {
            if(succ) VanillaConfigMod.LOGGER.info("VanillaConfig loaded " + globalConfigs.size() + " global configs.");
            else VanillaConfigMod.LOGGER.warn("VanillaConfig failed to load global configs.");
        }
    }
    public static void onClientStopping() {
        boolean succ = saveAllGlobal();
        if(verboseLoading) {
            if(succ) VanillaConfigMod.LOGGER.info("VanillaConfig saved " + globalConfigs.size() + " global configs.");
            else VanillaConfigMod.LOGGER.warn("VanillaConfig failed to save global configs.");
        }
        onWorldClosed(); //saves if client is stopped while world is open
    }
}
