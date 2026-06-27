package dev.zoomitsky.mixin.client;

import dev.zoomitsky.CameraController;
import dev.zoomitsky.ZoomState;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
public class CameraDistanceMixin {

    @ModifyArg(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"
            ),
            index = 0
    )
    private float modifyCameraDistance(float original) {
        if (!CameraController.isThirdPerson()) return original;

        ZoomState.tpVanillaDistance = original;
        if (!ZoomState.isAnyZoomActive()) ZoomState.tpTargetDistance = original;

        return CameraController.getTpDistance();
    }
}