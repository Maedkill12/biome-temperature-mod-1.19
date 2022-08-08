package net.maed.biomestemperature;

import net.fabricmc.api.ModInitializer;
import net.maed.biomestemperature.config.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiomesTemperature implements ModInitializer {

	public static final String MOD_ID = "biomestemperature";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Configs.registerConfigs();

	}
}
