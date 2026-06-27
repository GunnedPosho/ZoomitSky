package dev.zoomitsky;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

public class CameraController {

    private static final ZoomEasing fovEasing = new ZoomEasing();
    private static final ZoomEasing tpEasing  = new ZoomEasing();

    private static long lastFovNanos = -1L;
    private static long lastTpNanos  = -1L;

    // ── Perspectiva ──────────────────────────────────

    public static boolean isThirdPerson() {
        Perspective p = MinecraftClient.getInstance().options.getPerspective();
        return p == Perspective.THIRD_PERSON_BACK || p == Perspective.THIRD_PERSON_FRONT;
    }

    // ── FOV (primera persona) ────────────────────────

    public static float getFovMultiplier() {
        long now = System.nanoTime();
        float delta = lastFovNanos < 0 ? 0f :
                Math.min((now - lastFovNanos) / 1_000_000_000f, 0.1f);
        lastFovNanos = now;

        ModConfig cfg = ModConfig.get();
        fovEasing.setTarget(ZoomState.targetFov);
        ZoomState.currentFov = fovEasing.step(delta, cfg.transitionDuration, cfg.easingType);
        return ZoomState.currentFov;
    }

    // ── Distancia TP (tercera persona) ───────────────

    public static float getTpDistance() {
        long now = System.nanoTime();
        float delta = lastTpNanos < 0 ? 0f :
                Math.min((now - lastTpNanos) / 1_000_000_000f, 0.1f);
        lastTpNanos = now;

        ModConfig cfg = ModConfig.get();
        tpEasing.setTarget(ZoomState.tpTargetDistance);
        ZoomState.tpCurrentDistance = tpEasing.step(delta, cfg.tpTransitionDuration, cfg.tpEasingType);
        return ZoomState.tpCurrentDistance;
    }

    // ── Sensibilidad del mouse ───────────────────────

    public static double getMouseSensitivityMultiplier() {
        if (!ZoomState.isFirstPersonZooming()) return 1.0;
        double factor = Math.pow(ZoomState.currentFov, 0.6);
        return Math.max(0.15, Math.min(1.0, factor));
    }
}