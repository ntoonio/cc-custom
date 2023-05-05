package io.antoon.mc.ccc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(method = "checkFallFlying", at = @At(value = "HEAD"), cancellable = true)
	void checkFallFlying(CallbackInfoReturnable info) {
		PlayerEntity self = (PlayerEntity) (Object) this;

		// No elytra in overworld
		if (self.world.getDimensionKey().getValue().toString().equals("minecraft:overworld")) {
			info.setReturnValue(false);
		}
	}
}
