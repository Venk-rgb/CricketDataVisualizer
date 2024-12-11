package application;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InsertApiKey {
	private static final String DB_URL = "jdbc:sqlite:keys.db";
	private static final String ENCRYPTION_KEY = "1234567890abcdef"; // 16-char key for AES

	private static String encrypt(String data) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	
	public static void main(String[] args) {
		String apiKey = "1ea530e7-eee6-4c27-a0f7-686feb0b16e7"; // My API key
		String serviceName = "cricketapi"; // Service name (e.g., "cricketapi")

		try (Connection conn = DriverManager.getConnection(DB_URL)) {
			String encryptedApiKey = encrypt(apiKey);
			String sql = "INSERT INTO api_keys (service_name, api_key) VALUES (?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, serviceName);
				pstmt.setString(2, encryptedApiKey);
				pstmt.executeUpdate();
				System.out.println("API Key inserted successfully!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
