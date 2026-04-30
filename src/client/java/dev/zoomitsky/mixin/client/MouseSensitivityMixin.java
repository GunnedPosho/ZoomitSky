package dev.zoomitsky.mixin.client;

import dev.zoomitsky.ZoomitSkyClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Mouse.class)
public class MouseSensitivityMixin {

    @ModifyArg(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            ),
            index = 0
    )
    private double adjustMouseSensitivityX(double cursorDeltaX) {
        return cursorDeltaX * ZoomitSkyClient.getMouseSensitivityMultiplier();
    }

    @ModifyArg(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            ),
            index = 1
    )
    private double adjustMouseSensitivityY(double cursorDeltaY) {
        return cursorDeltaY * ZoomitSkyClient.getMouseSensitivityMultiplier();
    }
}