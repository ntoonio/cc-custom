package io.antoon.mc.ccc;

import net.fabricmc.api.ModInitializer;

import java.util.Random;

public class CCCMain implements ModInitializer {
	public static Random cccRandom = new Random();
	public static String skullOwners[] = new String[] {"ntoonio", "sosseskalman", "Pat_ASW2"};

	public static String getRandomSkullOwner() {
		return skullOwners[cccRandom.nextInt(skullOwners.length)];
	}

	@Override
	public void onInitialize() {
		System.out.println("CCC mod initialized");
	}
}
