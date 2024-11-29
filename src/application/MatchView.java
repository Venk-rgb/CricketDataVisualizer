package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class MatchView {
    private JsonArray matchData;

    public MatchView(JsonArray matchData) {
        this.matchData = matchData;
    }

    public VBox getView() {
        VBox layout = new VBox();
        layout.setSpacing(10);

        ListView<String> matchList = new ListView<>();

        if (matchData.size() == 0) {
            matchList.getItems().add("No matches available.");
        } else {
            for (int i = 0; i < matchData.size(); i++) {
                JsonObject match = matchData.get(i).getAsJsonObject();

                // Extract teams from the "teams" array
                JsonArray teams = match.has("teams") && !match.get("teams").isJsonNull()
                        ? match.get("teams").getAsJsonArray()
                        : new JsonArray();

                String team1 = teams.size() > 0 ? teams.get(0).getAsString() : "Unknown Team 1";
                String team2 = teams.size() > 1 ? teams.get(1).getAsString() : "Unknown Team 2";

                // Extract other fields
                String status = match.has("status") && !match.get("status").isJsonNull()
                        ? match.get("status").getAsString()
                        : "Unknown Status";

                String venue = match.has("venue") && !match.get("venue").isJsonNull()
                        ? match.get("venue").getAsString()
                        : "Unknown Venue";

                // Build the match info string
                String matchInfo = team1 + " vs " + team2 + " - " + status + " @ " + venue;
                matchList.getItems().add(matchInfo);
            }
        }

        layout.getChildren().add(matchList);
        return layout;
    }
}
