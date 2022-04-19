package net.treset.vanillaconfig.tools.helpers;

import java.util.function.Consumer;

public class Keybind {
    String name = "";
    int[] keys = new int[]{};
    KeybindContext context = KeybindContext.IN_GAME;

    public Keybind(String name, int[] keys, KeybindContext context) {
        this.name = name;
        this.keys = keys;
        this.context = context;
    }

    public String getName() { return this.name; }

    public int[] getKeys() { return this.keys; }
    public boolean setKeys(int[] keys) {
        if(this.keys != keys) this.keys = keys;
        return true;
    }

    public int getAmount() { return this.getKeys().length; }

    public KeybindContext getContext() { return this.context; }
    public boolean setContext(KeybindContext context) { this.context = context; return true; }

    public boolean isValid() { return true; }

    Consumer<String> onPressed = keybindName -> {};
    public boolean onPressed(Consumer<String> method) {
        this.onPressed = method;
        return true;
    }

    public void resolve() { this.onPressed.accept(this.getName()); }
}
