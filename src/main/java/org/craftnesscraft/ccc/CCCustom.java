package org.craftnesscraft.ccc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
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

		if (CONFIG.apiEnabled()) {
			LOGGER.info("Will be sending player status to: " + CONFIG.apiUrl);
		}

		/*TradeOfferHelper.registerWanderingTraderOffers(builder -> {
            // Common trades
            builder.addOffersToPool(
                TradeOfferHelper.WanderingTraderOffersBuilder.SELL_COMMON_ITEMS_POOL,
				(level, entity, random) -> {
				//new MerchantOffer(itemCost, itemStack, i, j, f)
				ItemCost cost = null;
				ItemStack head = new ItemStack(Items.PLAYER_HEAD, 1);

				Optional<GameProfile> profile = S.getProfileCache().get(username);

				head.set(DataComponents.PROFILE, profile);

				return new MerchantOffer(
						cost,
						head,
						2, 5, 0.05f
						);
				});
		});*/

		// Register command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.includeDedicated) {
				Command.register(dispatcher);
			}
		});

		// Set up interval to update heads and online players
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			CCCustom.scheduler.scheduleAtFixedRate(() -> {
				List<ServerPlayer> players = server.getPlayerList().getPlayers();
				ExternalRequestManager.seenMultiplePlayers(players, true);

				// Fetch heads
				ExternalRequestManager.getHeads(heads -> {
					CONFIG.skullOwners = heads;
				});
			}, 0, 60, TimeUnit.SECONDS);
		});

		// Stop the scheduler so it doesn't keep the process running
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			CCCustom.scheduler.shutdown();
		});

		// Send player connected to the API
		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			ExternalRequestManager.seenPlayer(handler.getPlayer(), true);
		});

		// Send player disconnect to the API
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ExternalRequestManager.seenPlayer(handler.getPlayer(), false);
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
