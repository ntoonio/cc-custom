package io.antoon.mc.ccc.mixin;

import io.antoon.mc.ccc.CCCMain;
import io.antoon.mc.ccc.ExternalRequestManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.util.concurrent.TimeUnit.SECONDS;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

	@Inject(at = @At(value = "HEAD"), method = "onPlayerConnect")
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info) {
		ExternalRequestManager.seenPlayer(player, true);
	}

	@Inject(at = @At(value = "HEAD"), method = "remove")
	public void remove(ServerPlayerEntity player, CallbackInfo info) {
		ExternalRequestManager.seenPlayer(player, false);
	}

	@Inject(at = @At(value = "TAIL"), method = "<init>")
	public void init(MinecraftServer server, CombinedDynamicRegistries registryManager, PlayerSaveHandler saveHandler, int maxPlayers, CallbackInfo info) {
		// Send players status now and every minute
		CCCMain.scheduler.scheduleAtFixedRate(() -> {
			if (server.getPlayerManager() != null) {
				ExternalRequestManager.seenMultiplePlayers(server.getPlayerManager().getPlayerList(), true);
			}
		}, 0, 60, SECONDS);

	}
}
