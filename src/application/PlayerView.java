package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PlayerView {
	private JsonObject playerInfo;

	public PlayerView(JsonObject playerInfo) {
		this.playerInfo = playerInfo;
	}

	public VBox getView() {
		VBox layout = new VBox();
		layout.setSpacing(10);
		layout.setAlignment(Pos.CENTER);

		// Player Image
		String playerImgUrl = playerInfo.has("playerImg") && !playerInfo.get("playerImg").isJsonNull()
				? playerInfo.get("playerImg").getAsString()
				: "https://via.placeholder.com/150"; // Fallback image
		ImageView playerImage = new ImageView(new Image(playerImgUrl));
		playerImage.setFitWidth(150);
		playerImage.setFitHeight(150);

		// Player Basic Information
		String name = playerInfo.has("name") && !playerInfo.get("name").isJsonNull()
				? playerInfo.get("name").getAsString()
				: "Unknown Name";

		String role = playerInfo.has("role") && !playerInfo.get("role").isJsonNull()
				? playerInfo.get("role").getAsString()
				: "Unknown Role";

		String battingStyle = playerInfo.has("battingStyle") && !playerInfo.get("battingStyle").isJsonNull()
				? playerInfo.get("battingStyle").getAsString()
				: "Unknown Batting Style";

		String bowlingStyle = playerInfo.has("bowlingStyle") && !playerInfo.get("bowlingStyle").isJsonNull()
				? playerInfo.get("bowlingStyle").getAsString()
				: "Unknown Bowling Style";

		String country = playerInfo.has("country") && !playerInfo.get("country").isJsonNull()
				? playerInfo.get("country").getAsString()
				: "Unknown Country";

		Label nameLabel = new Label("Name: " + name);
		Label roleLabel = new Label("Role: " + role);
		Label battingStyleLabel = new Label("Batting Style: " + battingStyle);
		Label bowlingStyleLabel = new Label("Bowling Style: " + bowlingStyle);
		Label countryLabel = new Label("Country: " + country);

		// Safely get stats array
		JsonArray statsArray = playerInfo.has("stats") && !playerInfo.get("stats").isJsonNull()
				? playerInfo.get("stats").getAsJsonArray()
				: null;

		// Player Stats Table
		TableView<Stat> statsTable = createStatsTable(statsArray);

		// Add components to layout
		layout.getChildren().addAll(playerImage, nameLabel, roleLabel, battingStyleLabel, bowlingStyleLabel,
				countryLabel, statsTable);

		return layout;
	}

	private TableView<Stat> createStatsTable(JsonArray statsArray) {
		TableView<Stat> table = new TableView<>();

		// Define columns
		TableColumn<Stat, String> fnColumn = new TableColumn<>("Function");
		fnColumn.setCellValueFactory(data -> data.getValue().fnProperty());

		TableColumn<Stat, String> matchTypeColumn = new TableColumn<>("Match Type");
		matchTypeColumn.setCellValueFactory(data -> data.getValue().matchTypeProperty());

		TableColumn<Stat, String> statColumn = new TableColumn<>("Stat");
		statColumn.setCellValueFactory(data -> data.getValue().statProperty());

		TableColumn<Stat, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(data -> data.getValue().valueProperty());

		// Add columns to the table
		table.getColumns().addAll(fnColumn, matchTypeColumn, statColumn, valueColumn);

		// Safely populate the table
		if (statsArray == null || statsArray.size() == 0) {
			// Add a placeholder row if statsArray is null or empty
			table.setPlaceholder(new Label("No statistics available."));
		} else {
			for (int i = 0; i < statsArray.size(); i++) {
				JsonObject statObj = statsArray.get(i).getAsJsonObject();
				String fn = statObj.has("fn") && !statObj.get("fn").isJsonNull() ? statObj.get("fn").getAsString()
						: "Unknown";

				String matchType = statObj.has("matchtype") && !statObj.get("matchtype").isJsonNull()
						? statObj.get("matchtype").getAsString()
						: "Unknown";

				String stat = statObj.has("stat") && !statObj.get("stat").isJsonNull()
						? statObj.get("stat").getAsString()
						: "Unknown";

				String value = statObj.has("value") && !statObj.get("value").isJsonNull()
						? statObj.get("value").getAsString()
						: "0";

				table.getItems().add(new Stat(fn, matchType, stat, value));
			}
		}

		return table;
	}

}
