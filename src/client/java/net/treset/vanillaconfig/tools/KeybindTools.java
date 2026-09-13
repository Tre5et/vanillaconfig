package net.treset.vanillaconfig.tools;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.treset.vanillaconfig.tools.helpers.Keybind;
import net.treset.vanillaconfig.tools.helpers.KeybindContext;

import java.util.ArrayList;
import java.util.List;

public class KeybindTools {
    static List<Keybind> keybinds = new ArrayList<>();
    static List<Integer> pressedKeys = new ArrayList<>();
    static List<Integer> newKeys = new ArrayList<>();

    public static Keybind[] getKeybinds() { return keybinds.toArray(new Keybind[]{}); }
    public static boolean addKeybind(Keybind keybind) {
        if(getKeybind(keybind.getName()) != null) return false;
        return keybinds.add(keybind);
    }
    public static boolean removeKeybind(String name) {
        return keybinds.remove(getKeybind(name));
    }

    public static Keybind getKeybind(String name) {
        for (Keybind e : keybinds) {
            if(e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static void tick() {
        resolveKeybinds();
    }

    public static void resolveKeybinds() {
        for (Keybind e : keybinds) {
            if(e.getContext() == KeybindContext.IN_GAME && !ClientTools.isInGame()) continue;
            boolean containsNew = false;
            for (int j : e.getKeys()) {
                if (newKeys.contains(j)) {
                    containsNew = true;
                    break;
                }
            }
            if(!containsNew) continue;

            boolean containsAll = true;
            for (int j : e.getKeys()) {
                if (!pressedKeys.contains(j)) {
                    containsAll = false;
                    break;
                }
            }
            if(!containsAll) continue;

            e.resolve();
        }
        newKeys.clear();
    }

    public static void onKeyEvent(long window, KeyEvent input, int action, int mods) {
        if(action == InputConstants.PRESS) onKeyDown(input);
        else if(action == InputConstants.RELEASE) onKeyUp(input);
    }
    public static void onKeyDown(KeyEvent input) {
        pressedKeys.add(input.key());
        newKeys.add(input.key());
    }
    public static void onKeyUp(KeyEvent input) {
        pressedKeys.remove((Integer)input.key());
        newKeys.remove((Integer)input.key());
    }
}
