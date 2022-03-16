package io.antoon.mc.ccc.mixin;

import io.antoon.mc.ccc.CCCMain;
import io.antoon.mc.ccc.ExternalRequestManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

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

	@Inject(at = @At(value = "TAIL"), method = "<init>()V")
	public void init(MinecraftServer server, DynamicRegistryManager.Immutable registryManager, WorldSaveHandler saveHandler, int maxPlayers, CallbackInfo info) {
		final ScheduledFuture<?> seenUpdateHandle = CCCMain.scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// A bit weird to getPlayerManager though server but...
				for (int i = 0; i < server.getPlayerManager().getPlayerList().size(); i++) {
					ExternalRequestManager.seenPlayer(server.getPlayerManager().getPlayerList().get(i).getUuidAsString(), server.getPlayerManager().getPlayerList().get(i).getPos(), false);
				}
			}
		}, 0, 60, SECONDS);
	}
}
