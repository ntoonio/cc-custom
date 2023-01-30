package io.antoon.mc.ccc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CCCMain implements ModInitializer {
	public static Random cccRandom = new Random(); // Maybe we don't need our own randomizer, but I'm scared to mess up something
	public static List<String> skullOwners;

	public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static String getRandomSkullOwner() {
		if (skullOwners == null || skullOwners.isEmpty())
			return "PonchooMannen";

		return skullOwners.get(cccRandom.nextInt(skullOwners.size()));
	}

	@Override
	public void onInitialize() {
		System.out.println("CCC mod initialized");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.dedicated) {
				CCCommand.register(dispatcher);
			}
		});

		ExternalRequestManager.getHeads(heads -> {
			skullOwners = heads;
		});
	}
}
