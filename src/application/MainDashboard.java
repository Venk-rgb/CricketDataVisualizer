package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MainDashboard extends Application {
	private ApiClient apiClient = new ApiClient();
	private Map<String, String> seriesMap = new HashMap<>(); // Stores series name -> series ID
	private Map<String, String> matchMap = new HashMap<>(); // Stores match name -> match ID
	private Map<String, String> playerMap = new HashMap<>(); // Stores player name -> player ID

	@Override
	public void start(Stage primaryStage) {
		TabPane tabPane = new TabPane();

		// Series & Matches Tab
		VBox seriesTabContent = new VBox();
		ComboBox<String> seriesComboBox = new ComboBox<>();
		seriesComboBox.setPromptText("Select a Series");
		ListView<String> matchesListView = new ListView<>();
		TextArea seriesDetailsArea = new TextArea();
		seriesDetailsArea.setEditable(false);
		seriesTabContent.getChildren().addAll(seriesComboBox, matchesListView, seriesDetailsArea);

		Tab seriesTab = new Tab("Series & Matches", seriesTabContent);

		// Populate Series ComboBox
		JsonArray seriesArray = apiClient.fetchSeriesList();
		for (int i = 0; i < seriesArray.size(); i++) {
			JsonObject series = seriesArray.get(i).getAsJsonObject();
			String seriesName = series.get("name").getAsString();
			String seriesId = series.get("id").getAsString();
			seriesComboBox.getItems().add(seriesName);
			seriesMap.put(seriesName, seriesId);
		}

		// Handle Series Selection
		seriesComboBox.setOnAction(event -> {
			String selectedSeries = seriesComboBox.getSelectionModel().getSelectedItem();
			if (selectedSeries != null) {
				String seriesId = seriesMap.get(selectedSeries);

				// Fetch Series Info
				JsonObject seriesInfo = apiClient.fetchSeriesInfo(seriesId);
				JsonObject seriesData = seriesInfo.getAsJsonObject("data").getAsJsonObject("info");
				StringBuilder seriesDetails = new StringBuilder();
				seriesDetails.append("Series Name: ").append(seriesData.get("name").getAsString()).append("\n");
				seriesDetails.append("Start Date: ").append(seriesData.get("startdate").getAsString()).append("\n");
				seriesDetails.append("End Date: ").append(seriesData.get("enddate").getAsString()).append("\n");
				seriesDetails.append("ODIs: ").append(seriesData.get("odi").getAsInt()).append("\n");
				seriesDetails.append("T20s: ").append(seriesData.get("t20").getAsInt()).append("\n");
				seriesDetails.append("Tests: ").append(seriesData.get("test").getAsInt()).append("\n");
				seriesDetailsArea.setText(seriesDetails.toString());

				// Populate Matches List
				matchesListView.getItems().clear();
				JsonArray matches = seriesInfo.getAsJsonObject("data").getAsJsonArray("matchList");
				for (int i = 0; i < matches.size(); i++) {
					JsonObject match = matches.get(i).getAsJsonObject();
					String matchName = match.get("name").getAsString();
					String matchId = match.get("id").getAsString();
					matchesListView.getItems().add(matchName);
					matchMap.put(matchName, matchId);
				}
			}
		});

		// Handle Match Selection
		matchesListView.setOnMouseClicked(event -> {
			String selectedMatch = matchesListView.getSelectionModel().getSelectedItem();
			if (selectedMatch != null) {
				String matchId = matchMap.get(selectedMatch);

				// Fetch Match Info
				JsonObject matchInfo = apiClient.fetchMatchInfo(matchId);
				JsonObject matchData = matchInfo.getAsJsonObject("data");
				StringBuilder matchDetails = new StringBuilder();
				matchDetails.append("Match Name: ").append(matchData.get("name").getAsString()).append("\n");
				matchDetails.append("Venue: ").append(matchData.get("venue").getAsString()).append("\n");
				matchDetails.append("Date: ").append(matchData.get("date").getAsString()).append("\n");
				matchDetails.append("Teams: ").append(matchData.getAsJsonArray("teams").toString()).append("\n");
				matchDetails.append("Status: ").append(matchData.get("status").getAsString()).append("\n");
				seriesDetailsArea.setText(matchDetails.toString());
			}
		});

		// Players Tab
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
				playerDetails.append("Batting Style: ").append(playerData.get("battingStyle").getAsString())
						.append("\n");
				playerDetails.append("Bowling Style: ").append(playerData.get("bowlingStyle").getAsString())
						.append("\n");
				playerDetails.append("Country: ").append(playerData.get("country").getAsString()).append("\n");
				playerDetailsArea.setText(playerDetails.toString());
			}
		});

		// Add Tabs
		tabPane.getTabs().addAll(seriesTab, playersTab);

		// Stage
		Scene scene = new Scene(tabPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Cricket Visualizer");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
