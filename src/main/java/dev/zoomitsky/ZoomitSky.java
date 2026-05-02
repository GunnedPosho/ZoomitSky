package dev.zoomitsky;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomitSky implements ModInitializer {
	public static final String MOD_ID = "zoomit-sky";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("ZoomitSky initialized!");
	}
}