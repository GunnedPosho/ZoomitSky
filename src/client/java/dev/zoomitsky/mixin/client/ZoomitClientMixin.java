package dev.zoomitsky.mixin.client;

import dev.zoomitsky.CameraController;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class ZoomitClientMixin {

	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void modifyFov(Camera camera, float tickDelta, boolean changingFov,
						   CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(cir.getReturnValue() * CameraController.getFovMultiplier());
	}
}