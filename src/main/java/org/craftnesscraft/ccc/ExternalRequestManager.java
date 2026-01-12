package org.craftnesscraft.ccc;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

import static org.craftnesscraft.ccc.CCCustom.CONFIG;;

public class ExternalRequestManager {
	public static void seenPlayer(ServerPlayer player, boolean online) {
		if (!CONFIG.apiEnabled())
			return;

		JsonArray playerData = new JsonArray();
		playerData.add(generateSeenPlayerJson(player, online));
		JsonObject bodyJson = new JsonObject();
		bodyJson.add("players", playerData);

		requestPut(CONFIG.apiUrl + "ccc/api/seenPlayers", bodyJson.toString(), r -> {});
	}

	public static void seenMultiplePlayers(List<ServerPlayer> players, boolean online) {
		if (!CONFIG.apiEnabled())
			return;

		JsonArray playerData = new JsonArray();

		for (int i = 0; i < players.size(); i++) {
			playerData.add(generateSeenPlayerJson(players.get(i), online));
		}

		JsonObject bodyJson = new JsonObject();
		bodyJson.add("players", playerData);

		requestPut(CONFIG.apiUrl + "ccc/api/seenPlayers", bodyJson.toString(), r -> {});
	}

	public static void getHeads(Consumer<List<String>> onComplete) {
		if (!CONFIG.apiEnabled())
			return;

		requestGet(CONFIG.apiUrl + "ccc/api/heads", httpResponse -> {
			String json = httpResponse.body();

			try {
				Gson gson = new Gson();
				Type datasetListType = new TypeToken<Collection<String>>() {}.getType();
				onComplete.accept(gson.fromJson(json, datasetListType));
			}
			catch (Exception e) {
				onComplete.accept(Collections.emptyList());
			}
		});
	}

	private static JsonObject generateSeenPlayerJson(ServerPlayer player, boolean online) {
		String uuid = player.getStringUUID();
		String playername = player.getName().getString();
		Vec3 pos = player.position();
		String dimension = player.level().dimension().toString();

		JsonObject j = new JsonObject();
		j.addProperty("uuid", uuid);
		j.addProperty("playername", playername);
		j.addProperty("position", pos.x + ";" + pos.y + ";" + pos.z);
		j.addProperty("dimension", dimension);
		j.addProperty("online", online);

		return j;
	}

	private static void requestGet(String url, Consumer<HttpResponse<String>> onResponse) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authentication", CONFIG.apiSecret)
				.build();

		try {
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(onResponse).join();
		}
		catch (Exception e) {}
	}

	private static void requestPut(String url, String bodyJson, Consumer<HttpResponse<String>> onResponse) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.header("Authentication", CONFIG.apiSecret)
				.PUT(HttpRequest.BodyPublishers.ofString(bodyJson))
				.build();
		try {
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(onResponse).join();
		}
		catch (Exception e) {}
	}
}

