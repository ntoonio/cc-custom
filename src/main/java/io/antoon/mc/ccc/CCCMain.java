package io.antoon.mc.ccc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import java.util.Random;

public class CCCMain implements ModInitializer {
	public static Random cccRandom = new Random(); // Maybe we don't need our own randomizer, but I'm scared to mess up something
	public static String skullOwners[] = new String[] {"ntoonio", "sosseskalman", "Pat_ASW2"};

	public static String getRandomSkullOwner() {
		return skullOwners[cccRandom.nextInt(skullOwners.length)];
	}

	@Override
	public void onInitialize() {
		System.out.println("CCC mod initialized");

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			if (dedicated) {
				CCCommand.register(dispatcher);
			}
		});
	}
}
