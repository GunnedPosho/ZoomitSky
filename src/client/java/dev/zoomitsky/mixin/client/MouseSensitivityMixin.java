package dev.zoomitsky.mixin.client;

import dev.zoomitsky.CameraController;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Mouse.class)
public class MouseSensitivityMixin {

    @ModifyArg(method = "updateMouse",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"),
            index = 0)
    private double adjustX(double x) { return x * CameraController.getMouseSensitivityMultiplier(); }

    @ModifyArg(method = "updateMouse",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"),
            index = 1)
    private double adjustY(double y) { return y * CameraController.getMouseSensitivityMultiplier(); }
}