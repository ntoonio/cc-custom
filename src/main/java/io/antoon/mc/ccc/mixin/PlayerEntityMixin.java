package io.antoon.mc.ccc.mixin;

import io.antoon.mc.ccc.CCCMain;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Inject(method = "canGlide", at = @At(value = "HEAD"), cancellable = true)
	void canGlide(CallbackInfoReturnable<Boolean> info) {
		PlayerEntity self = (PlayerEntity) (Object) this;

		// No elytra in overworld
		if (CCCMain.worldIsOverworld(self.getWorld())) {
			info.setReturnValue(false);
		}
	}
}
