package dev.zoomitsky;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.Identifier;

public class ZoomitSkyClient implements ClientModInitializer {
    private static KeyBinding zoomKey;
    private static KeyBinding resetZoomKey;
    private static KeyBinding cinematicZoomKey;
    private static KeyBinding toggleModeKey;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("zoomitsky", "category"));

    private static boolean isZooming = false;
    private static boolean isCinematicZooming = false;
    private static boolean toggleMode = false;
    private static float targetFov = 1.0f;
    private static float previousFov = 1.0f;
    private static float currentFov = 1.0f;
    private static long lastRenderNanos = -1L;
    private static final ZoomEasing fovEasing = new ZoomEasing();

    // Variables para evitar múltiples activaciones
    private static boolean wasZoomPressed = false;
    private static boolean wasCinematicPressed = false;
    private static boolean wasToggleModePressed = false;

    // Zoom Cinematográfico
    private static float cinematicBarsProgress = 0.0f;

    // Nivel de zoom dinámico
    private static float zoomLevel;

    @Override
    public void onInitializeClient() {
        // Zoom
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                CATEGORY
        ));
        // Zoom cinemático
        cinematicZoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.cinematic_zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY
        ));

        // Cambiar modo
        toggleModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.toggle_mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));

        // Resetear zoom
        resetZoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.reset_zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY
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
                    targetFov = zoomLevel;
                } else {
                    targetFov = 1.0f;
                }

                String modeKey = toggleMode ? "key.zoomitsky.mode.toggle" : "key.zoomitsky.mode.hold";
                client.player.sendMessage(
                        net.minecraft.text.Text.translatable(modeKey),
                        true
                );

                wasToggleModePressed = true;
            } else if (!vPressed) {
                wasToggleModePressed = false;
            }

            if (resetZoomKey.wasPressed()) {
                zoomLevel = Math.max(ZoomitSkyConfig.get().minZoom, Math.min(ZoomitSkyConfig.get().maxZoom, ZoomitSkyConfig.get().defaultZoom));
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

                if (cPressed) {
                    isCinematicZooming = true;
                    isZooming = false;
                    targetFov = zoomLevel;
                } else if (zPressed) {
                    isCinematicZooming = false;
                    isZooming = true;
                    targetFov = zoomLevel;
                } else {
                    isZooming = false;
                    isCinematicZooming = false;
                    targetFov = 1.0f;
                }
            }

            // El tick sigue actualizando targetFov, pero ya no interpola currentFov.
            // La interpolación real ocurre en getFovMultiplier() con tiempo real.
            previousFov = currentFov;

            float targetBars = isCinematicZooming ? 1.0f : 0.0f;
            if (cinematicBarsProgress != targetBars) {
                float barsDifference = targetBars - cinematicBarsProgress;
                if (Math.abs(barsDifference) < 0.001f) {
                    cinematicBarsProgress = targetBars;
                } else {
                    cinematicBarsProgress += barsDifference * ZoomitSkyConfig.get().cinematicBarsSpeed;
                }
            }
        });

        ZoomitSkyConfig.load();
        zoomLevel = ZoomitSkyConfig.get().defaultZoom;
    }

    public static void adjustZoom(double scrollAmount) {
        if (isZooming || isCinematicZooming) {
            zoomLevel -= (float) scrollAmount * ZoomitSkyConfig.get().zoomStep;
            zoomLevel = Math.max(ZoomitSkyConfig.get().minZoom, Math.min(ZoomitSkyConfig.get().maxZoom, zoomLevel));
            targetFov = zoomLevel;
        }
    }

    public static float getFovMultiplier(float tickDelta) {
        long now = System.nanoTime();
        float deltaSeconds;

        if (lastRenderNanos < 0L) {
            deltaSeconds = 0f;
        } else {
            deltaSeconds = (now - lastRenderNanos) / 1_000_000_000f;
            deltaSeconds = Math.min(deltaSeconds, 0.1f);
        }
        lastRenderNanos = now;

        ZoomitSkyConfig cfg = ZoomitSkyConfig.get();
        fovEasing.setTarget(targetFov);
        currentFov = fovEasing.step(deltaSeconds, cfg.transitionDuration, cfg.easingType);

        return currentFov;
    }
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