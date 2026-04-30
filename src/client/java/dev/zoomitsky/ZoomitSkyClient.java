package dev.zoomitsky;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomitSkyClient implements ClientModInitializer {
    private static KeyBinding zoomKey;
    private static KeyBinding resetZoomKey;
    private static KeyBinding cinematicZoomKey;
    private static KeyBinding toggleModeKey;

    private static boolean isZooming = false;
    private static boolean isCinematicZooming = false;
    private static boolean toggleMode = false;
    private static float targetFov = 1.0f;
    private static float currentFov = 1.0f;

    // Variables para evitar múltiples activaciones
    private static boolean wasZoomPressed = false;
    private static boolean wasCinematicPressed = false;
    private static boolean wasToggleModePressed = false;

    // Zoom Cinematográfico
    private static float cinematicBarsProgress = 0.0f;
    private static final float CINEMATIC_BARS_SPEED = 0.10f;

    // Nivel de zoom dinámico
    private static float zoomLevel = 0.28f;
    private static final float DEFAULT_ZOOM = 0.28f;

    // Límites del zoom
    private static final float MIN_ZOOM = 0.05f;
    private static final float MAX_ZOOM = 0.8f;
    private static final float ZOOM_STEP = 0.08f;
    private static final float TRANSITION_SPEED = 0.22f;

    @Override
    public void onInitializeClient() {
        // Zoom
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.zoomitsky"
        ));
        // Zoom cinemático
        cinematicZoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.cinematic_zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.zoomitsky"
        ));

        // Cambiar modo
        toggleModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.toggle_mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.zoomitsky"
        ));

        // Resetear zoom
        resetZoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.reset_zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.zoomitsky"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean zPressed = zoomKey.isPressed();
            boolean cPressed = cinematicZoomKey.isPressed();
            boolean vPressed = toggleModeKey.isPressed();

            if (vPressed && !wasToggleModePressed) {
                boolean wasZoomingBefore = isZooming || isCinematicZooming;
                boolean wasCinematicBefore = isCinematicZooming;

                toggleMode = !toggleMode;

                if (wasZoomingBefore) {
                    isZooming = !wasCinematicBefore;
                    isCinematicZooming = wasCinematicBefore;
                    targetFov = zoomLevel;
                } else {
                    isZooming = false;
                    isCinematicZooming = false;
                    targetFov = 1.0f;
                }

                String mode = toggleMode ? "Press once" : "Press and hold";
                client.player.sendMessage(
                        net.minecraft.text.Text.literal("§6[ZoomitSky] §e" + mode),
                        true
                );

                wasToggleModePressed = true;
            } else if (!vPressed) {
                wasToggleModePressed = false;
            }

            if (resetZoomKey.wasPressed()) {
                zoomLevel = DEFAULT_ZOOM;
                if (isZooming || isCinematicZooming) {
                    targetFov = zoomLevel;
                }
            }

            if (toggleMode) {
                if (cPressed && !wasCinematicPressed) {
                    isCinematicZooming = !isCinematicZooming;
                    if (isCinematicZooming) {
                        isZooming = false;
                        targetFov = zoomLevel;
                    } else {
                        targetFov = 1.0f;
                    }
                    wasCinematicPressed = true;
                } else if (!cPressed) {
                    wasCinematicPressed = false;
                }

                if (zPressed && !wasZoomPressed) {
                    if (isCinematicZooming) {
                        isCinematicZooming = false;
                        isZooming = true;
                    } else {
                        isZooming = !isZooming;
                        targetFov = isZooming ? zoomLevel : 1.0f;
                    }
                    wasZoomPressed = true;
                } else if (!zPressed) {
                    wasZoomPressed = false;
                }
            }
            else {
                wasZoomPressed = false;
                wasCinematicPressed = false;

                if (cPressed && zPressed) {
                    if (!isCinematicZooming) {
                        isCinematicZooming = true;
                        isZooming = false;
                    }
                    targetFov = zoomLevel;
                } else if (cPressed) {
                    isCinematicZooming = true;
                    isZooming = false;
                    targetFov = zoomLevel;
                } else if (zPressed) {
                    if (!isCinematicZooming) {
                        isZooming = true;
                    } else {
                        isCinematicZooming = false;
                        isZooming = true;
                    }
                    targetFov = zoomLevel;
                } else {
                    isZooming = false;
                    isCinematicZooming = false;
                    targetFov = 1.0f;
                }
            }

            if (currentFov != targetFov) {
                float difference = targetFov - currentFov;
                if (Math.abs(difference) < 0.001f) {
                    currentFov = targetFov;
                } else {
                    currentFov += difference * TRANSITION_SPEED;
                }
            }

            float targetBars = isCinematicZooming ? 1.0f : 0.0f;
            if (cinematicBarsProgress != targetBars) {
                float difference = targetBars - cinematicBarsProgress;
                if (Math.abs(difference) < 0.001f) {
                    cinematicBarsProgress = targetBars;
                } else {
                    cinematicBarsProgress += difference * CINEMATIC_BARS_SPEED;
                }
            }
        });

        ZoomitSky.LOGGER.info("ZoomitSky Client initialized!");
    }

    public static void adjustZoom(double scrollAmount) {
        if (isZooming || isCinematicZooming) {
            zoomLevel -= (float) scrollAmount * ZOOM_STEP;
            zoomLevel = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomLevel));
            targetFov = zoomLevel;
        }
    }

    public static float getFovMultiplier() { return currentFov; }
    public static boolean isZooming() { return isZooming || isCinematicZooming; }
    public static boolean isCinematicZooming() { return isCinematicZooming; }
    public static float getCinematicBarsProgress() { return cinematicBarsProgress; }

    public static double getMouseSensitivityMultiplier() {
        if (!isZooming && !isCinematicZooming) {
            return 1.0;
        }
        double zoomFactor = Math.pow(currentFov, 0.6);
        return Math.max(0.15, Math.min(1.0, zoomFactor));
    }
}