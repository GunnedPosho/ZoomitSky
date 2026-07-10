package dev.zoomitsky;

public class ZoomState {

    // ── Primera persona ──────────────────────────────
    public static boolean isZooming       = false;
    public static boolean isCinematic     = false;
    public static boolean toggleMode      = false;
    public static float   zoomLevel;
    public static float   targetFov       = 1.0f;
    public static float   currentFov      = 1.0f;
    public static float   topBarProgress    = 0.0f;
    public static float   bottomBarProgress = 0.0f;


    // ── Tercera persona ──────────────────────────────
    public static float tpTargetDistance;
    public static float tpCurrentDistance;
    public static float tpVanillaDistance = 4.0f;
    public static float tpZoomLevel;

    public static void initFromConfig() {
        ModConfig cfg = ModConfig.get();
        zoomLevel          = cfg.defaultZoom;
        tpZoomLevel        = cfg.tpDefaultDistance;
        tpTargetDistance   = tpVanillaDistance;
        tpCurrentDistance  = tpVanillaDistance;
    }

    // ── Helpers de lectura ───────────────────────────

    public static boolean isFirstPersonZooming() {
        return (isZooming || isCinematic) && !CameraController.isThirdPerson();
    }

    public static boolean isAnyZoomActive() {
        return isZooming || isCinematic;
    }
}