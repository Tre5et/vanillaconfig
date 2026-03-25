package net.treset.vanillaconfig.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import net.treset.vanillaconfig.tools.KeybindTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"), cancellable = true)
    public void onKey(long handle, int action, KeyEvent input, CallbackInfo c) {
        KeybindTools.onKeyEvent(handle, input.key(), input.scancode(), action, input.modifiers());
    }
}
