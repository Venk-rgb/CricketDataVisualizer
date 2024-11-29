package application;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private static final String API_KEY = "1ea530e7-eee6-4c27-a0f7-686feb0b16e7";
    private static final String BASE_URL = "https://api.cricapi.com/v1";
    private final OkHttpClient client;
    private final Gson gson;

    public ApiClient() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    // Fetch general data as JsonArray
    public JsonArray fetchData(String endpoint) throws Exception {
        String url = BASE_URL + endpoint + "?apikey=" + API_KEY + "&offset=0";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch data: HTTP " + response.code());
            }

            JsonObject responseObject = gson.fromJson(response.body().string(), JsonObject.class);

            return responseObject.getAsJsonArray("data");
        }
    }

    // Fetch player stats (requires player ID)
    public JsonObject fetchPlayerInfo(String playerId) throws Exception {
        String url = BASE_URL + "/players_info?apikey=" + API_KEY + "&id=" + playerId;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch player info: HTTP " + response.code());
            }

            // Parse the JSON response
            JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);

            // Extract the "data" field
            if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                return jsonResponse.getAsJsonObject("data");
            } else {
                throw new RuntimeException("Player data is missing in the response.");
            }
        }
    }

}
