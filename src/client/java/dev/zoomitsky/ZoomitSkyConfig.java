package dev.zoomitsky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZoomitSkyConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("zoomit-sky.json");

    private static ZoomitSkyConfig INSTANCE = new ZoomitSkyConfig();

    public float defaultZoom = 0.28f;
    public float minZoom = 0.05f;
    public float maxZoom = 0.8f;
    public float zoomStep = 0.08f;
    public float transitionSpeed = 0.24f;
    public float cinematicBarsSpeed = 0.10f;

    public static ZoomitSkyConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            INSTANCE = GSON.fromJson(reader, ZoomitSkyConfig.class);
        } catch (IOException e) {
            ZoomitSky.LOGGER.error("Failed to load ZoomitSky config", e);
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            ZoomitSky.LOGGER.error("Failed to save ZoomitSky config", e);
        }
    }
}