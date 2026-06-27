package dev.zoomitsky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("zoomit-sky.json");
    private static ModConfig INSTANCE = new ModConfig();

    // Zoom de primera persona
    public float defaultZoom      = 0.28f;
    public float minZoom          = 0.05f;
    public float maxZoom          = 0.6f;
    public float zoomStep         = 0.08f;
    public float transitionDuration  = 0.32f;
    public float cinematicBarsSpeed  = 0.10f;
    public ZoomEasing.EasingType easingType = ZoomEasing.EasingType.EASE_ZOOMITSKY;

    // Cámara en tercera persona
    public float tpDefaultDistance  = 4.0f;
    public float tpMinDistance      = 1.5f;
    public float tpMaxDistance      = 20.0f;
    public float tpDistanceStep     = 0.5f;
    public float tpTransitionDuration = 0.32f;
    public ZoomEasing.EasingType tpEasingType = ZoomEasing.EasingType.EASE_ZOOMITSKY;

    public static ModConfig get() { return INSTANCE; }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) { save(); return; }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
            if (loaded != null) INSTANCE = loaded;
        } catch (IOException e) {
            ZoomitSky.LOGGER.error("Failed to load config", e);
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            ZoomitSky.LOGGER.error("Failed to save config", e);
        }
    }
}