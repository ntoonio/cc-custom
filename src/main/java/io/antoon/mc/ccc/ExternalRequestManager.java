package io.antoon.mc.ccc;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

import static io.antoon.mc.ccc.CCCMain.CONFIG;

public class ExternalRequestManager {
	public static void seenPlayer(ServerPlayerEntity player, boolean online) {
		if (!CONFIG.apiEnabled())
			return;

		String body = "[" + generateSeenPlayerJson(player, online) + "]";
		requestPut(CONFIG.apiUrl + "ccc/api/seenPlayers", body, r -> {});
	}

	public static void seenMultiplePlayers(List<ServerPlayerEntity> players, boolean online) {
		if (!CONFIG.apiEnabled())
			return;

		List<String> playerData = new ArrayList<>();

		for (int i = 0; i < players.size(); i++) {
			playerData.add(generateSeenPlayerJson(players.get(i), online));
		}

		String body = "[" + String.join(",", playerData) + "]";

		requestPut(CONFIG.apiUrl + "ccc/api/seenPlayers", body, r -> {});
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

	private static String generateSeenPlayerJson(ServerPlayerEntity player, boolean online) {
		String uuid = player.getUuidAsString();
		String playername = player.getName().getString();
		Vec3d pos = player.getPos();
		String dimension = player.getWorld().getDimensionEntry().getIdAsString();

		String jsonStr = "{";
		jsonStr += "\"uuid\": \"" + uuid + "\",";
		jsonStr += "\"playername\": \"" + playername + "\",";
		jsonStr += "\"position\": \"" + pos.x + ";" + pos.y + ";" + pos.z + "\",";
		jsonStr += "\"dimension\": \"" + dimension + "\",";
		jsonStr += "\"online\": " + (online ? "true" : "false");
		jsonStr += "}";

		return jsonStr;
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
