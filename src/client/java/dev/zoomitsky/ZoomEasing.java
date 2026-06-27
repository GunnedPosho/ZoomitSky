package dev.zoomitsky;

public class ZoomEasing {

    // Estado de la transición activa
    private float startValue   = 1.0f;
    private float targetValue  = 1.0f;
    private float currentValue = 1.0f;
    private float elapsed      = 0f;   // segundos transcurridos desde que inició la transición

    /**
     * Notifica que el target cambió — reinicia la transición desde el valor actual.
     */
    public void setTarget(float target) {
        if (target != targetValue) {
            startValue  = currentValue;
            targetValue = target;
            elapsed     = 0f;
        }
    }

    /**
     * Avanza la transición un frame y devuelve el valor actual.
     *
     * @param deltaSeconds Tiempo transcurrido desde el último frame
     * @param duration     Duración total de la transición en segundos (0 = instantáneo)
     * @param easingType   Curva a aplicar
     */
    public float step(float deltaSeconds, float duration, EasingType easingType) {
        if (duration <= 0f || startValue == targetValue) {
            currentValue = targetValue;
            return currentValue;
        }

        elapsed += deltaSeconds;
        float t = Math.min(elapsed / duration, 1f);  // progreso normalizado [0, 1]
        float eased = applyEasing(t, easingType);

        currentValue = startValue + (targetValue - startValue) * eased;
        return currentValue;
    }

    // ------------------------------------------------------------------

    public static float applyEasing(float t, EasingType type) {
        return switch (type) {
            case LINEAR        -> t;
            case EASE_OUT      -> 1f - (1f - t) * (1f - t);
            case EASE_IN       -> t * t;
            case EASE_IN_OUT   -> t < 0.5f
                    ? 2f * t * t
                    : 1f - (-2f * t + 2f) * (-2f * t + 2f) / 2f;
            case EASE_OUT_SINE -> (float) Math.sin(t * Math.PI / 2f);
            case EASE_IN_SINE  -> 1f - (float) Math.cos(t * Math.PI / 2f);
            case EASE_OUT_EXPO -> t >= 1f ? 1f : 1f - (float) Math.pow(2f, -10f * t);
            case EASE_OUT_CUBIC -> 1f - (1f - t) * (1f - t) * (1f - t);
            case EASE_ZOOMITSKY -> {
                float skewed = t < 0.5f
                        ? 0.5f * (float) Math.pow(2f * t, 1.6f)
                        : 1f - 0.5f * (float) Math.pow(2f * (1f - t), 2.4f);
                float smoothed = skewed * skewed * (3f - 2f * skewed);
                yield (float) Math.log1p(smoothed * (Math.E - 1f));
            }
        };
    }

    public enum EasingType {
        EASE_ZOOMITSKY,
        EASE_OUT_CUBIC,
        LINEAR,
        EASE_OUT,
        EASE_IN,
        EASE_IN_OUT,
        EASE_OUT_SINE,
        EASE_IN_SINE,
        EASE_OUT_EXPO;

        public String getTranslationKey() {
            return "config.zoomitsky.easing." + this.name().toLowerCase();
        }
    }
}