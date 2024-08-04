package io.antoon.mc.ccc;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.core.jmx.Server;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

public class ExternalRequestManager {
	static String baseUrl = "http://localhost:5000/";
	static String apiSecret = "yxikaxikolmi";

	public static void seenPlayer(ServerPlayerEntity player, boolean online) {
		String body = generateSeenPlayerJson(player, online);
		requestPut(baseUrl + "ccc/api/seenPlayer", body, r -> {});
	}

	public static void seenMultiplePlayers(List<ServerPlayerEntity> players, boolean online) {
		List<String> playerData = new ArrayList<>();

		for (int i = 0; i < players.size(); i++) {
			playerData.add(generateSeenPlayerJson(players.get(i), online));
		}

		String body = "[" + String.join(",", playerData) + "]";

		requestPut(baseUrl + "ccc/api/seenMultiplePlayers", body, r -> {});
	}

	public static void verifyCode(String uuid, String code, Consumer<Boolean> onComplete) {
		String body = "{";
		body += "\"uuid\": \"" + uuid + "\",";
		body += "\"code\": \"" + code + "\"";
		body += "}";

		requestPut(baseUrl + "ccc/api/verify", body, httpResponse -> {
			onComplete.accept(httpResponse.statusCode() == 200);
		});
	}

	public static void getHeads(Consumer<List<String>> onComplete) {
		requestGet(baseUrl + "ccc/api/heads", httpResponse -> {
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
				.header("Authentication", apiSecret)
				.build();

		try {
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(onResponse).join();
		}
		catch (Exception e) {}
	}

	private static void requestPut(String url, String body, Consumer<HttpResponse<String>> onResponse) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.header("Authentication", apiSecret)
				.PUT(HttpRequest.BodyPublishers.ofString(body))
				.build();
		try {
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(onResponse).join();
		}
		catch (Exception e) {}
	}
}
