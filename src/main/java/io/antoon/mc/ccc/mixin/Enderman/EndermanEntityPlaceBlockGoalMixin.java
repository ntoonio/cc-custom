package io.antoon.mc.ccc.mixin.Enderman;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "net.minecraft.entity.mob.EndermanEntity$PlaceBlockGoal" })
public class EndermanEntityPlaceBlockGoalMixin {
	// Don't place any blocks
	@Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
	void canStart(CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(false);
	}
}
