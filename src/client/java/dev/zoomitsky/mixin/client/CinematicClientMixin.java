package dev.zoomitsky.mixin.client;

import dev.zoomitsky.ZoomState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CinematicClientMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hideGuiDuringCinematic(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ZoomState.isCinematic) {
            renderBars(context);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderCinematicBars(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ZoomState.isCinematic && ZoomState.cinematicBarsProgress > 0.001f) {
            renderBars(context);
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private void renderBars(DrawContext context) {
        float progress = ZoomState.cinematicBarsProgress;
        if (progress <= 0.001f) return;
        int w = context.getScaledWindowWidth();
        int h = context.getScaledWindowHeight();
        int barH = (int) (h * 0.12f * progress);
        context.fill(0, 0, w, barH, 0xFF000000);
        context.fill(0, h - barH, w, h, 0xFF000000);
    }
}