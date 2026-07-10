package dev.zoomitsky;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

public class ZoomController {

    private static boolean wasZoomPressed      = false;
    private static boolean wasCinematicPressed = false;
    private static boolean wasTogglePressed    = false;

    private static final ZoomEasing topBarEasing    = new ZoomEasing();
    private static final ZoomEasing bottomBarEasing = new ZoomEasing();

    public static void registerTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean isTP = CameraController.isThirdPerson();

            handleToggleModeKey(client, isTP);
            handleResetKeys(isTP);
            handleZoomKeys(isTP);
        });
    }

    // ── Modo de presión ─────────────────────────

    private static void handleToggleModeKey(net.minecraft.client.MinecraftClient client, boolean isTP) {
        boolean pressed = ModKeys.toggleMode.isPressed();
        if (pressed && !wasTogglePressed) {
            boolean wasActive   = ZoomState.isZooming || ZoomState.isCinematic;
            boolean wasCinema   = ZoomState.isCinematic;

            ZoomState.toggleMode = !ZoomState.toggleMode;

            if (wasActive) {
                ZoomState.isCinematic = false;
                ZoomState.isZooming   = !wasCinema;
                applyZoomTarget(isTP);
            } else {
                clearZoomTarget(isTP);
            }

            String key = ZoomState.toggleMode ? "key.zoomitsky.mode.toggle" : "key.zoomitsky.mode.hold";
            if (client.player != null) client.player.sendMessage(Text.translatable(key), true);
            wasTogglePressed = true;
        } else if (!pressed) {
            wasTogglePressed = false;
        }
    }

    // ── Reset ──────────────────────────────

    private static void handleResetKeys(boolean isTP) {
        if (ModKeys.resetZoom.wasPressed()) {
            ModConfig cfg = ModConfig.get();
            if (isTP) {
                ZoomState.tpZoomLevel      = cfg.tpDefaultDistance;
                if (ZoomState.isAnyZoomActive())
                    ZoomState.tpTargetDistance = ZoomState.tpZoomLevel;
            } else {
                ZoomState.zoomLevel = Math.clamp(cfg.defaultZoom, cfg.minZoom, cfg.maxZoom);
                if (ZoomState.isAnyZoomActive()) ZoomState.targetFov = ZoomState.zoomLevel;
            }
        }
    }

    // ── Teclas de zoom (Z y C) ───────────────────────

    private static void handleZoomKeys(boolean isTP) {
        boolean zPressed = ModKeys.zoom.isPressed();
        boolean cPressed = ModKeys.cinematicZoom.isPressed();

        if (ZoomState.toggleMode) {
            if (cPressed && !wasCinematicPressed) {
                ZoomState.isCinematic = !ZoomState.isCinematic;
                if (ZoomState.isCinematic) {
                    ZoomState.isZooming = false;
                    applyZoomTarget(isTP);
                } else {
                    clearZoomTarget(isTP);
                }
                wasCinematicPressed = true;
            } else if (!cPressed) {
                wasCinematicPressed = false;
            }

            if (zPressed && !wasZoomPressed) {
                if (ZoomState.isCinematic) {
                    ZoomState.isCinematic = false;
                    ZoomState.isZooming   = true;
                } else {
                    ZoomState.isZooming = !ZoomState.isZooming;
                }
                if (ZoomState.isZooming) applyZoomTarget(isTP);
                else clearZoomTarget(isTP);
                wasZoomPressed = true;
            } else if (!zPressed) {
                wasZoomPressed = false;
            }

        } else {
            wasZoomPressed = false;
            wasCinematicPressed = false;

            boolean wasZooming   = ZoomState.isZooming;
            boolean wasCinematic = ZoomState.isCinematic;

            if (cPressed) {
                ZoomState.isCinematic = true;
                ZoomState.isZooming   = false;
            } else if (zPressed) {
                ZoomState.isCinematic = false;
                ZoomState.isZooming   = true;
            } else {
                ZoomState.isZooming   = false;
                ZoomState.isCinematic = false;
            }

            boolean nowActive = ZoomState.isZooming || ZoomState.isCinematic;
            boolean wasActive = wasZooming || wasCinematic;
            if (nowActive) applyZoomTarget(isTP);
            else if (wasActive) clearZoomTarget(isTP);
        }
    }

    // ── Barras cinemáticas ───────────────────────────
    private static long  lastBarUpdateNanos    = System.nanoTime();
    private static float barUpdateAccumulator  = 0f;

    public static void updateCinematicBars() {
        long now = System.nanoTime();
        float delta = (now - lastBarUpdateNanos) / 1_000_000_000f;
        lastBarUpdateNanos = now;

        ModConfig cfg = ModConfig.get();
        float target = ZoomState.isCinematic ? 1.0f : 0.0f;
        topBarEasing.setTarget(target);
        bottomBarEasing.setTarget(target);

        float step = 1f / cfg.cinematicBarsAnimationFps;
        barUpdateAccumulator += delta;

        while (barUpdateAccumulator >= step) {
            boolean useSame = cfg.cinematicBarsUseSameConfig;
            ZoomState.topBarProgress = topBarEasing.step(
                    step, cfg.cinematicTopBarTransitionDuration, cfg.cinematicTopBarEasingType);
            ZoomState.bottomBarProgress = bottomBarEasing.step(
                    step,
                    useSame ? cfg.cinematicTopBarTransitionDuration : cfg.cinematicBottomBarTransitionDuration,
                    useSame ? cfg.cinematicTopBarEasingType : cfg.cinematicBottomBarEasingType);
            barUpdateAccumulator -= step;
        }
    }

    // ── Scroll ───────────────────────────────────────

    public static void onScroll(double amount) {
        if (!ZoomState.isAnyZoomActive()) return;

        if (CameraController.isThirdPerson()) {
            ModConfig cfg = ModConfig.get();
            ZoomState.tpZoomLevel -= (float) amount * cfg.tpDistanceStep;
            ZoomState.tpZoomLevel  = Math.clamp(
                    ZoomState.tpZoomLevel, cfg.tpMinDistance, cfg.tpMaxDistance);
            ZoomState.tpTargetDistance = ZoomState.tpZoomLevel;
        } else {
            ModConfig cfg = ModConfig.get();
            ZoomState.zoomLevel -= (float) amount * cfg.zoomStep;
            ZoomState.zoomLevel  = Math.clamp(ZoomState.zoomLevel, cfg.minZoom, cfg.maxZoom);
            ZoomState.targetFov  = ZoomState.zoomLevel;
        }
    }

    // ── Helpers internos ─────────────────────────────

    private static void applyZoomTarget(boolean isTP) {
        if (!isTP) ZoomState.targetFov = ZoomState.zoomLevel;
        else ZoomState.tpTargetDistance = ZoomState.tpZoomLevel;
    }

    private static void clearZoomTarget(boolean isTP) {
        if (!isTP) ZoomState.targetFov = 1.0f;
        else ZoomState.tpTargetDistance = ZoomState.tpVanillaDistance;
    }
}