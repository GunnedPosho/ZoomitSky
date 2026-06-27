package dev.zoomitsky;

/**
 * Estado compartido del sistema de zoom.
 * Solo ZoomController escribe aquí; el resto solo lee.
 */
public class ZoomState {

    // ── Primera persona ──────────────────────────────
    public static boolean isZooming       = false;
    public static boolean isCinematic     = false;
    public static boolean toggleMode      = false;
    public static float   zoomLevel;          // nivel FOV actual (min–max)
    public static float   targetFov       = 1.0f;
    public static float   currentFov      = 1.0f;
    public static float   cinematicBarsProgress = 0.0f;


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

    /** Devuelve true si hay zoom activo en primera persona. */
    public static boolean isFirstPersonZooming() {
        return (isZooming || isCinematic) && !CameraController.isThirdPerson();
    }

    /** Devuelve true si cualquier tipo de zoom está activo. */
    public static boolean isAnyZoomActive() {
        return isZooming || isCinematic;
    }
}