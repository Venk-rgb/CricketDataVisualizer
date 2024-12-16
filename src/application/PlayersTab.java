package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class PlayersTab {
    private final ApiClient apiClient = new ApiClient();
    private final Map<String, String> playerMap = new HashMap<>(); // Player name -> Player ID

    public Tab getTab() {
        VBox playersTabContent = new VBox();
        ListView<String> playersListView = new ListView<>();
        TextArea playerDetailsArea = new TextArea();
        playerDetailsArea.setEditable(false);
        playersTabContent.getChildren().addAll(playersListView, playerDetailsArea);

        Tab playersTab = new Tab("Players", playersTabContent);

        // Populate Players List
        JsonArray playerArray = apiClient.fetchPlayerList();
        for (int i = 0; i < playerArray.size(); i++) {
            JsonObject player = playerArray.get(i).getAsJsonObject();
            String playerName = player.has("name") && !player.get("name").isJsonNull()
                    ? player.get("name").getAsString()
                    : "Unknown Player";
            String playerId = player.has("id") && !player.get("id").isJsonNull()
                    ? player.get("id").getAsString()
                    : "";

            playersListView.getItems().add(playerName);
            playerMap.put(playerName, playerId);
        }

        // Handle Player Selection
        playersListView.setOnMouseClicked(event -> {
            String selectedPlayer = playersListView.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null && playerMap.containsKey(selectedPlayer)) {
                String playerId = playerMap.get(selectedPlayer);

                // Fetch Player Info
                JsonObject playerInfo = apiClient.fetchPlayerInfo(playerId);
                JsonObject playerData = playerInfo.has("data") ? playerInfo.getAsJsonObject("data") : null;

                if (playerData != null) {
                    StringBuilder playerDetails = new StringBuilder();

                    // Use safe checks for each field
                    playerDetails.append("Name: ").append(getSafeField(playerData, "name")).append("\n");
                    playerDetails.append("Role: ").append(getSafeField(playerData, "role")).append("\n");
                    playerDetails.append("Batting Style: ").append(getSafeField(playerData, "battingStyle")).append("\n");
                    playerDetails.append("Bowling Style: ").append(getSafeField(playerData, "bowlingStyle")).append("\n");
                    playerDetails.append("Country: ").append(getSafeField(playerData, "country")).append("\n");

                    playerDetailsArea.setText(playerDetails.toString());
                } else {
                    playerDetailsArea.setText("No player data available.");
                }
            }
        });

        return playersTab;
    }

    // Utility method to handle missing or null fields safely
    private String getSafeField(JsonObject jsonObject, String fieldName) {
        return (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull())
                ? jsonObject.get(fieldName).getAsString()
                : "Unknown";
    }
}
