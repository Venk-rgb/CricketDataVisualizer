package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MatchView {
	private JsonObject matchInfo;

	public MatchView(JsonObject matchInfo) {
		this.matchInfo = matchInfo;
	}

	public VBox getView() {
		VBox layout = new VBox();
		layout.setSpacing(10);
		layout.setAlignment(Pos.CENTER);

		String name = matchInfo.get("name").getAsString();
		String venue = matchInfo.get("venue").getAsString();
		String date = matchInfo.get("date").getAsString();
		JsonArray teams = matchInfo.getAsJsonArray("teams");

		String team1 = teams.size() > 0 ? teams.get(0).getAsString() : "Unknown Team";
		String team2 = teams.size() > 1 ? teams.get(1).getAsString() : "Unknown Team";

		Label nameLabel = new Label("Match: " + name);
		Label venueLabel = new Label("Venue: " + venue);
		Label dateLabel = new Label("Date: " + date);
		Label teamsLabel = new Label("Teams: " + team1 + " vs " + team2);

		layout.getChildren().addAll(nameLabel, venueLabel, dateLabel, teamsLabel);
		return layout;
	}
}
