package dev.zoomitsky;

import net.fabricmc.api.ClientModInitializer;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModKeys.register();
        ModConfig.load();
        ZoomState.initFromConfig();
        ZoomController.registerTick();
    }
}