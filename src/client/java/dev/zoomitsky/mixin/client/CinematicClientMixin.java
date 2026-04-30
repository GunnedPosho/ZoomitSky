package dev.zoomitsky.mixin.client;

import dev.zoomitsky.ZoomitSkyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CinematicClientMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideGuiDuringCinematic(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ZoomitSkyClient.isCinematicZooming()) {
            renderCinematicBarsDirectly(context);
            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void renderCinematicBars(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ZoomitSkyClient.isCinematicZooming()) {
            renderCinematicBarsDirectly(context);
        }
    }

    private void renderCinematicBarsDirectly(DrawContext context) {
        float progress = ZoomitSkyClient.getCinematicBarsProgress();

        if (progress > 0.001f) {
            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();

            int barHeight = (int) (screenHeight * 0.12f * progress);// Altura de las barras
            context.fill(0, 0, screenWidth, barHeight, 0xFF000000); // Dibujar barra superior
            context.fill(0, screenHeight - barHeight, screenWidth, screenHeight, 0xFF000000); // Dibujar barra inferior
        }
    }
}