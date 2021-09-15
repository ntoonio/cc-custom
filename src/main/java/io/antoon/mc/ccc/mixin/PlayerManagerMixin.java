package io.antoon.mc.ccc.mixin;

import io.antoon.mc.ccc.ExternalRequestManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
	@Inject(at = @At(value = "HEAD"), method = "onPlayerConnect")
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		ExternalRequestManager.seenPlayer(player.getName().asString(), player.getUuidAsString());
	}

	@Inject(at = @At(value = "HEAD"), method = "remove")
	public void remove(ServerPlayerEntity player, CallbackInfo info) {
		ExternalRequestManager.seenPlayer(player.getUuidAsString(), player.getPos(), true);
	}
}
