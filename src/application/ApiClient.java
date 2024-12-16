package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

public class ApiClient {
	private final String BASE_URL = "https://api.cricapi.com/v1";
	private static final String DB_URL = "jdbc:sqlite:keys.db"; // SQLite database location
	private static final String ENCRYPTION_KEY = "1234567890abcdef"; // 16-character AES key for decryption
	private OkHttpClient client = new OkHttpClient();
	private String apiKey; // Store the decrypted API key

	public ApiClient() {
		this.apiKey = fetchDecryptedApiKey();
	}
	
	// Function to fetch decrypted api key from the backend
	private String fetchDecryptedApiKey() {
		try (Connection conn = DriverManager.getConnection(DB_URL)) {
			String query = "SELECT api_key FROM api_keys WHERE service_name = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setString(1, "cricketapi"); // Assuming "cricketapi" is the service name
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					String encryptedKey = rs.getString("api_key");
					return decrypt(encryptedKey);
				} else {
					throw new RuntimeException("API key for cricketapi not found in the database.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch or decrypt the API key.");
		}
	}
	
	// Function to decrypt the key using AES
	private String decrypt(String encryptedData) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
		return new String(decryptedBytes);
	}
	
	// Function to fetch series list
	public JsonArray fetchSeriesList() {
		String url = BASE_URL + "/series?apikey=" + apiKey;
		return fetchJsonArray(url);
	}

	// Function to fetch info for each series
	public JsonObject fetchSeriesInfo(String seriesId) {
		String url = BASE_URL + "/series_info?apikey=" + apiKey + "&id=" + seriesId;
		return fetchJsonObject(url);
	}
	
	// Function to get matches
	public JsonObject fetchMatchInfo(String matchId) {
		String url = BASE_URL + "/match_info?apikey=" + apiKey + "&id=" + matchId;
		return fetchJsonObject(url);
	}

	// Function to fetch list of players
	public JsonArray fetchPlayerList() {
		String url = BASE_URL + "/players?apikey=" + apiKey; // Assumes a generic endpoint for player list
		return fetchJsonArray(url);
	}
	
	// Function to fetch info of players
	public JsonObject fetchPlayerInfo(String playerId) {
		String url = BASE_URL + "/players_info?apikey=" + apiKey + "&id=" + playerId;
		return fetchJsonObject(url);
	}

	// Function to fetch array of jsons
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
	
	// Function to fetch json objects
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
