package dev.zoomitsky.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.zoomitsky.ModConfig;
import dev.zoomitsky.ZoomController;
import dev.zoomitsky.ZoomState;
import org.spongepowered.asm.mixin.Unique;

@Mixin(InGameHud.class)
public class CinematicClientMixin {

    @Unique
    private static final float BAR_VISIBILITY_THRESHOLD = 0.001f;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hideGuiDuringCinematic(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ZoomController.updateCinematicBars();
        if (ZoomState.isCinematic) {
            renderBars(context);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderCinematicBars(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ZoomState.isCinematic
                && Math.max(ZoomState.topBarProgress, ZoomState.bottomBarProgress) > BAR_VISIBILITY_THRESHOLD) {
            renderBars(context);
        }
    }

    @Unique
    private void renderBars(DrawContext context) {
        ModConfig cfg = ModConfig.get();
        int w = context.getScaledWindowWidth();
        int h = context.getScaledWindowHeight();
        boolean useSame = cfg.cinematicBarsUseSameConfig;

        if (!cfg.cinematicTopBarHidden && ZoomState.topBarProgress > BAR_VISIBILITY_THRESHOLD) {
            int barH = (int) (h * cfg.cinematicTopBarHeight * ZoomState.topBarProgress);
            int color = 0xFF000000 | cfg.cinematicTopBarColor;
            context.fill(0, 0, w, barH, color);
        }

        if (!cfg.cinematicBottomBarHidden && ZoomState.bottomBarProgress > BAR_VISIBILITY_THRESHOLD) {
            float height = useSame ? cfg.cinematicTopBarHeight : cfg.cinematicBottomBarHeight;
            int rgb      = useSame ? cfg.cinematicTopBarColor  : cfg.cinematicBottomBarColor;
            int color = 0xFF000000 | rgb;
            int barH = (int) (h * height * ZoomState.bottomBarProgress);
            context.fill(0, h - barH, w, h, color);
        }
    }
}