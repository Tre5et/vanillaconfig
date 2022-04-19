package net.treset.vanillaconfig;

import net.fabricmc.api.ModInitializer;
import net.treset.vanillaconfig.config.managers.SaveLoadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaConfigMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("vanillaconfig");

	@Override
	public void onInitialize() {
		SaveLoadManager.init();
	}
}
