package net.treset.vanillaconfig.mixin;

import net.minecraft.client.Keyboard;
import net.treset.vanillaconfig.tools.KeybindTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey(JIIII)V", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo c) {
        KeybindTools.onKeyEvent(window, key, scancode, action, modifiers);
    }
}
