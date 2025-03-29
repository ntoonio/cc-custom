package io.antoon.mc.ccc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.world.World;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CCCMain implements ModInitializer {
	public static Random cccRandom = new Random(); // Maybe we don't need our own randomizer, but I'm scared to mess up something
	public static CCCConfig CONFIG;

	public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Override
	public void onInitialize() {
		CONFIG = CCCConfig.load("./config/ccc.properties");

		// Register command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.dedicated) {
				CCCommand.register(dispatcher);
			}
		});

		// Fetch player heads from API
		ExternalRequestManager.getHeads(heads -> {
			CONFIG.skullOwners = heads;
		});
	}

	public static boolean worldIsOverworld(World world) {
		return world.getRegistryKey() == World.OVERWORLD;
	}
}
