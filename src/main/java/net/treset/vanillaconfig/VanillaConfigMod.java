package net.treset.vanillaconfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.treset.vanillaconfig.config.managers.SaveLoadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaConfigMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("vanillaconfig");

	@Override
	public void onInitialize() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			LOGGER.info("Environment is server, disabling VanillaConfig.");
			return;
		}
		SaveLoadManager.init();
	}
}
