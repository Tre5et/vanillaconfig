package net.treset.vanillaconfig.mixin;

import net.minecraft.client.Minecraft;
import net.treset.vanillaconfig.config.managers.SaveLoadManager;
import net.treset.vanillaconfig.tools.ClientTools;
import net.treset.vanillaconfig.tools.KeybindTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    private static String prevWorldId = null;

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo c) {
        updateWorld();
        KeybindTools.tick();
    }

    private static void updateWorld() {
        String worldId = ClientTools.getWorldId();

        if(prevWorldId == null && worldId != null) SaveLoadManager.onWorldOpened(worldId);
        else if(prevWorldId != null && worldId == null) SaveLoadManager.onWorldClosed(prevWorldId);
        else return;

        prevWorldId = worldId;
    }
}
