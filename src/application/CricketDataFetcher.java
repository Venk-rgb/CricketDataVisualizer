package application;

import okhttp3.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CricketDataFetcher {
    private static final String API_KEY = "1ea530e7-eee6-4c27-a0f7-686feb0b16e7"; // Replace with your CricAPI key
    private static final String API_URL = "https://api.cricapi.com/v1/players";

    public JsonArray fetchPlayerData() throws Exception {
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("apikey", API_KEY)
                .build();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("API call failed");

            JsonObject jsonObject = JsonParser.parseString(response.body().string()).getAsJsonObject();
            return jsonObject.getAsJsonArray("data");
        }
    }
}
