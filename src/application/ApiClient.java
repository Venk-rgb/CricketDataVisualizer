package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
	private final String BASE_URL = "https://api.cricapi.com/v1";
	private final String API_KEY = "1ea530e7-eee6-4c27-a0f7-686feb0b16e7";
	private OkHttpClient client = new OkHttpClient();

	public JsonArray fetchSeriesList() {
		String url = BASE_URL + "/series?apikey=" + API_KEY;
		return fetchJsonArray(url);
	}

	public JsonObject fetchSeriesInfo(String seriesId) {
		String url = BASE_URL + "/series_info?apikey=" + API_KEY + "&id=" + seriesId;
		return fetchJsonObject(url);
	}

	public JsonObject fetchMatchInfo(String matchId) {
		String url = BASE_URL + "/match_info?apikey=" + API_KEY + "&id=" + matchId;
		return fetchJsonObject(url);
	}

	public JsonArray fetchPlayerList() {
		String url = BASE_URL + "/players?apikey=" + API_KEY; // Assumes a generic endpoint for player list
		return fetchJsonArray(url);
	}

	public JsonObject fetchPlayerInfo(String playerId) {
		String url = BASE_URL + "/players_info?apikey=" + API_KEY + "&id=" + playerId;
		return fetchJsonObject(url);
	}

	private JsonArray fetchJsonArray(String url) {
		try {
			Request request = new Request.Builder().url(url).build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful())
					throw new RuntimeException("Failed: HTTP " + response.code());

				JsonObject jsonResponse = new com.google.gson.JsonParser().parse(response.body().string())
						.getAsJsonObject();

				if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
					return jsonResponse.getAsJsonArray("data");
				} else {
					throw new RuntimeException("No 'data' field found in the response.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JsonArray();
		}
	}

	private JsonObject fetchJsonObject(String url) {
		try {
			Request request = new Request.Builder().url(url).build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful())
					throw new RuntimeException("Failed: HTTP " + response.code());
				return new com.google.gson.JsonParser().parse(response.body().string()).getAsJsonObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JsonObject();
		}
	}
}
