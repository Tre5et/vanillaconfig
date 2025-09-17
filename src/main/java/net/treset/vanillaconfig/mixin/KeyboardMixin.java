package net.treset.vanillaconfig.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import net.treset.vanillaconfig.tools.KeybindTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey(JILnet/minecraft/client/input/KeyInput;)V", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int action, KeyInput input, CallbackInfo c) {
        KeybindTools.onKeyEvent(window, input.key(), input.scancode(), action, input.modifiers());
    }
}
