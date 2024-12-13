package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDashboard extends Application {
	private final ApiClient apiClient = new ApiClient();
	private final Map<String, String> seriesMap = new HashMap<>(); // Series name -> Series ID
	private final Map<String, String> matchMap = new HashMap<>(); // Match name -> Match ID
	private final Map<String, String> playerMap = new HashMap<>(); // Player name -> Player ID
	private final Map<String, Integer> roleCount = new HashMap<>(); // Role -> Count

	@Override
	public void start(Stage primaryStage) {
		TabPane tabPane = new TabPane();

		// --- Series & Matches Tab ---
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

		        // Run a background task to fetch series info
		        Task<Void> fetchSeriesTask = new Task<>() {
		            @Override
		            protected Void call() {
		                JsonObject seriesInfo = apiClient.fetchSeriesInfo(seriesId);
		                if (seriesInfo != null && seriesInfo.has("data")) {
		                    JsonObject seriesData = seriesInfo.getAsJsonObject("data").getAsJsonObject("info");
		                    JsonArray matches = seriesInfo.getAsJsonObject("data").getAsJsonArray("matchList");

		                    Platform.runLater(() -> {
		                        // Create and configure BarChart
		                        CategoryAxis xAxis = new CategoryAxis();
		                        xAxis.setLabel("Match Type");

		                        NumberAxis yAxis = new NumberAxis();
		                        yAxis.setLabel("Count");

		                        BarChart<String, Number> seriesChart = new BarChart<>(xAxis, yAxis);
		                        seriesChart.setTitle(seriesData.get("name").getAsString());
		                        
		                        // Set up series data
		                        XYChart.Series<String, Number> chartData = new XYChart.Series<>();
		                        chartData.getData().add(new XYChart.Data<>("ODIs", seriesData.get("odi").getAsInt()));
		                        chartData.getData().add(new XYChart.Data<>("T20s", seriesData.get("t20").getAsInt()));
		                        chartData.getData().add(new XYChart.Data<>("Tests", seriesData.get("test").getAsInt()));
		                        seriesChart.getData().add(chartData);

		                        // Create a popup window for BarChart
		                        Stage chartStage = new Stage();
		                        VBox chartLayout = new VBox(10);

		                        // Add series name and date information as headings
		                        Label seriesNameLabel = new Label("Series: " + seriesData.get("name").getAsString());
		                        seriesNameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

		                        Label dateRangeLabel = new Label("Start Date: " + seriesData.get("startdate").getAsString()
		                                + " | End Date: " + seriesData.get("enddate").getAsString());
		                        dateRangeLabel.setStyle("-fx-font-size: 12; -fx-font-style: italic;");

		                        chartLayout.getChildren().addAll(seriesNameLabel, dateRangeLabel, seriesChart);
		                        chartLayout.setStyle("-fx-padding: 10;");
		                        
		                        Scene chartScene = new Scene(chartLayout, 400, 300);
		                        chartStage.setScene(chartScene);
		                        chartStage.setTitle("Series Details");
		                        chartStage.show();
		                    });
		                } else {
		                    Platform.runLater(() -> seriesDetailsArea.setText("Failed to fetch series details."));
		                }
		                return null;
		            }
		        };

		        // Run the task in a separate thread
		        new Thread(fetchSeriesTask).start();
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

		// --- Players Tab ---
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

		// --- Player Role Distribution Tab ---
		Tab roleDistributionTab = new Tab("Player Role Distribution");
		VBox roleDistributionContent = new VBox();
		roleDistributionContent.setSpacing(20); // Add spacing between the charts

		// Create PieChart for Player Role Distribution
		PieChart roleChart = new PieChart();
		Label roleChartLabel = new Label("Player Role Distribution");

		// Create BarChart for Player Country Distribution
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Country");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Number of Players");

		BarChart<String, Number> countryBarChart = new BarChart<>(xAxis, yAxis);
		countryBarChart.setTitle("Player Country Distribution");

		roleDistributionContent.getChildren().addAll(roleChartLabel, roleChart, countryBarChart);
		roleDistributionTab.setContent(roleDistributionContent);

		// Add a listener to fetch data only when the tab is selected
		roleDistributionTab.setOnSelectionChanged(event -> {
		    if (roleDistributionTab.isSelected() && roleChart.getData().isEmpty() && countryBarChart.getData().isEmpty()) {
		        Task<Void> fetchRolesAndCountriesTask = new Task<>() {
		            @Override
		            protected Void call() {
		                ExecutorService executor = Executors.newFixedThreadPool(10); // Thread pool with 10 threads
		                Map<String, Integer> countryCount = new HashMap<>(); // Country -> Count

		                for (int i = 0; i < playerArray.size(); i++) {
		                    JsonObject player = playerArray.get(i).getAsJsonObject();
		                    String playerId = player.get("id").getAsString();

		                    executor.submit(() -> {
		                        JsonObject playerInfo = apiClient.fetchPlayerInfo(playerId);
		                        JsonObject playerData = playerInfo.has("data") ? playerInfo.getAsJsonObject("data") : null;

		                        if (playerData != null) {
		                            String role = playerData.has("role") ? playerData.get("role").getAsString() : "Unknown";
		                            String country = playerData.has("country") ? playerData.get("country").getAsString() : "Unknown";

		                            synchronized (roleCount) {
		                                roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
		                            }

		                            synchronized (countryCount) {
		                                countryCount.put(country, countryCount.getOrDefault(country, 0) + 1);
		                            }
		                        }
		                    });
		                }

		                executor.shutdown();
		                while (!executor.isTerminated()) {
		                    // Wait for threads to complete
		                }

		                Platform.runLater(() -> {
		                    // Update PieChart for roles
		                    roleChart.getData().clear();
		                    roleCount.forEach((role, count) -> roleChart.getData().add(new PieChart.Data(role, count)));

		                    // Update BarChart for countries
		                    countryBarChart.getData().clear();
		                    XYChart.Series<String, Number> countrySeries = new XYChart.Series<>();
		                    countrySeries.setName("Countries");
		                    countryCount.forEach((country, count) -> countrySeries.getData().add(new XYChart.Data<>(country, count)));
		                    countryBarChart.getData().add(countrySeries);
		                });

		                return null;
		            }
		        };

		        // Run the task in a separate thread
		        new Thread(fetchRolesAndCountriesTask).start();
		    }
		});

		// Add all tabs
		tabPane.getTabs().addAll(seriesTab, playersTab, roleDistributionTab);

		// Stage setup
		Scene scene = new Scene(tabPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Cricket Visualizer");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
