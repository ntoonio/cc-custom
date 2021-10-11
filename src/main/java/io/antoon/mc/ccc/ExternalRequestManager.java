package io.antoon.mc.ccc;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

public class ExternalRequestManager {
	static String baseUrl = "http://localhost:8080/";
	static String apiSecret = "yxikaxikolmi";

	private static void request(String url, String parameters) {
		request(url, parameters, httpResponse -> { /* Empty. Can this be defined in a better way? */ });
	}

	private static void request(String url, String parameters, Consumer<HttpResponse<String>> onResponse) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "?secret=" + apiSecret + "&" + parameters)).build();

		try {
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(onResponse).join();
		}
		catch (Exception e) {}
	}

	// Called in an interval and when the player disconnects
	public static void seenPlayer(String uuid, Vec3d pos, Boolean disconnected) {
		request(baseUrl + "ccc/api/seenPlayer", "uuid=" + uuid + "&pos=" + pos.x + "," + pos.y + "," + pos.z + "&disconnected=" + disconnected);
	}

	// Called when a player connects. We know that position has not been changed, only the username can have changed.
	public static void seenPlayer(String username, String uuid) {
		request(baseUrl + "ccc/api/seenPlayer", "uuid=" + uuid + "&username=" + username);
	}

	public static void verifyCode(String uuid, String code, Consumer<Boolean> onComplete) {
		request(baseUrl + "ccc/api/verify", "uuid=" + uuid + "&code=" + code, httpResponse -> {
			onComplete.accept(httpResponse.statusCode() == 200);
		});
	}

	public static void getHeads(Consumer<List<String>> onComplete) {
		request(baseUrl + "ccc/api/heads", "", httpResponse -> {
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
}
