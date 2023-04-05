package io.antoon.mc.ccc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.PlayerManager;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CCCMain implements ModInitializer {
	public static Random cccRandom = new Random(); // Maybe we don't need our own randomizer, but I'm scared to mess up something
	public static List<String> skullOwners;

	public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Override
	public void onInitialize() {
		// Register command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.dedicated) {
				CCCommand.register(dispatcher);
			}
		});

		// Fetch player heads from API
		ExternalRequestManager.getHeads(heads -> {
			skullOwners = heads;
		});
	}

	public static String getRandomSkullOwner() {
		if (skullOwners == null || skullOwners.isEmpty())
			return "PonchooMannen";

		return skullOwners.get(cccRandom.nextInt(skullOwners.size()));
	}
}
