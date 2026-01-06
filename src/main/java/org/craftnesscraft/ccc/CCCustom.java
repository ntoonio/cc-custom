package org.craftnesscraft.ccc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCCustom implements ModInitializer {
	public static final String MOD_ID = "cc-custom";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Random randomizer = new Random(); // Maybe we don't need our own randomizer, but I'm scared to mess up something
	public static ModConfig CONFIG;

	public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Override
	public void onInitialize() {
		CONFIG = ModConfig.load("./config/cc-custom.properties");

		// Register command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.includeDedicated) {
				Command.register(dispatcher);
			}
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// Fetch heads
			ExternalRequestManager.getHeads(heads -> {
				CONFIG.skullOwners = heads;
			});

			// Start the players online heart beat
			PlayerList pl = server.getPlayerList();

			CCCustom.scheduler.scheduleAtFixedRate(() -> {
				List<ServerPlayer> players = pl.getPlayers();
				ExternalRequestManager.seenMultiplePlayers(players, true);
			}, 0, 60, TimeUnit.SECONDS);
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			CCCustom.scheduler.shutdown();
		});


		// Disable elytra in overworld and above nether roof
		EntityElytraEvents.ALLOW.register((entity) -> {
			ResourceKey<Level> dimension = entity.level().dimension();

			if (dimension == Level.NETHER) {
				return entity.position().y < 128;
			}

			return dimension != Level.OVERWORLD;
		});
	}
}
