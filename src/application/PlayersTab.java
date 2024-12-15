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
            String playerName = player.get("name").getAsString();
            String playerId = player.get("id").getAsString();
            playersListView.getItems().add(playerName);
            playerMap.put(playerName, playerId);
        }

        // Handle Player Selection
        playersListView.setOnMouseClicked(event -> {
            String selectedPlayer = playersListView.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null) {
                String playerId = playerMap.get(selectedPlayer);

                // Fetch Player Info
                JsonObject playerInfo = apiClient.fetchPlayerInfo(playerId);
                JsonObject playerData = playerInfo.getAsJsonObject("data");
                StringBuilder playerDetails = new StringBuilder();
                playerDetails.append("Name: ").append(playerData.get("name").getAsString()).append("\n");
                playerDetails.append("Role: ").append(playerData.get("role").getAsString()).append("\n");
                playerDetails.append("Batting Style: ").append(playerData.get("battingStyle").getAsString()).append("\n");
                playerDetails.append("Bowling Style: ").append(playerData.get("bowlingStyle").getAsString()).append("\n");
                playerDetails.append("Country: ").append(playerData.get("country").getAsString()).append("\n");
                playerDetailsArea.setText(playerDetails.toString());
            }
        });

        return playersTab;
    }
}
