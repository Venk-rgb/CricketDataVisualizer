package application;

import com.google.gson.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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

		String name = playerInfo.has("name") ? playerInfo.get("name").getAsString() : "Unknown Name";
		String role = playerInfo.has("role") ? playerInfo.get("role").getAsString() : "Unknown Role";
		String battingStyle = playerInfo.has("battingStyle") ? playerInfo.get("battingStyle").getAsString()
				: "Unknown Batting Style";
		String bowlingStyle = playerInfo.has("bowlingStyle") ? playerInfo.get("bowlingStyle").getAsString()
				: "Unknown Bowling Style";
		String country = playerInfo.has("country") ? playerInfo.get("country").getAsString() : "Unknown Country";

		Label nameLabel = new Label("Name: " + name);
		Label roleLabel = new Label("Role: " + role);
		Label battingLabel = new Label("Batting Style: " + battingStyle);
		Label bowlingLabel = new Label("Bowling Style: " + bowlingStyle);
		Label countryLabel = new Label("Country: " + country);

		layout.getChildren().addAll(nameLabel, roleLabel, battingLabel, bowlingLabel, countryLabel);
		return layout;
	}
}
