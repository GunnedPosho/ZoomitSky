package dev.zoomitsky.mixin.client;

import dev.zoomitsky.ZoomitSkyClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class ScrollClientMixin {

    @Inject(
            method = "onMouseScroll",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomitSkyClient.isZooming()) {
            ZoomitSkyClient.adjustZoom(vertical);
            ci.cancel();
        }
    }
}