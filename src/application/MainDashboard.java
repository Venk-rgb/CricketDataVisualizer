package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.*;
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

        // Bar Chart for Series Statistics
        BarChart<String, Number> seriesBarChart = createBarChart();
        seriesTabContent.getChildren().add(seriesBarChart);

        // Populate Series ComboBox
        Task<Void> fetchSeriesTask = new Task<>() {
            @Override
            protected Void call() {
                JsonArray seriesArray = apiClient.fetchSeriesList();
                for (int i = 0; i < seriesArray.size(); i++) {
                    JsonObject series = seriesArray.get(i).getAsJsonObject();
                    String seriesName = series.get("name").getAsString();
                    String seriesId = series.get("id").getAsString();
                    seriesMap.put(seriesName, seriesId);

                    Platform.runLater(() -> seriesComboBox.getItems().add(seriesName));
                }
                return null;
            }
        };
        new Thread(fetchSeriesTask).start();

        // Handle Series Selection
        seriesComboBox.setOnAction(event -> {
            String selectedSeries = seriesComboBox.getSelectionModel().getSelectedItem();
            if (selectedSeries != null) {
                String seriesId = seriesMap.get(selectedSeries);

                Task<JsonObject> fetchSeriesInfoTask = new Task<>() {
                    @Override
                    protected JsonObject call() {
                        return apiClient.fetchSeriesInfo(seriesId);
                    }
                };

                fetchSeriesInfoTask.setOnSucceeded(workerStateEvent -> {
                    JsonObject seriesInfo = fetchSeriesInfoTask.getValue();
                    JsonObject seriesData = seriesInfo.getAsJsonObject("data").getAsJsonObject("info");

                    // Update Series Details
                    StringBuilder seriesDetails = new StringBuilder();
                    seriesDetails.append("Series Name: ").append(seriesData.get("name").getAsString()).append("\n");
                    seriesDetails.append("Start Date: ").append(seriesData.get("startdate").getAsString()).append("\n");
                    seriesDetails.append("End Date: ").append(seriesData.get("enddate").getAsString()).append("\n");
                    seriesDetails.append("ODIs: ").append(seriesData.get("odi").getAsInt()).append("\n");
                    seriesDetails.append("T20s: ").append(seriesData.get("t20").getAsInt()).append("\n");
                    seriesDetails.append("Tests: ").append(seriesData.get("test").getAsInt()).append("\n");
                    seriesDetailsArea.setText(seriesDetails.toString());

                    // Update Bar Chart
                    updateBarChart(seriesBarChart, seriesData);

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
                });

                new Thread(fetchSeriesInfoTask).start();
            }
        });

        // Players Tab
        VBox playersTabContent = new VBox();
        PieChart playersPieChart = new PieChart();
        TextArea playerDetailsArea = new TextArea();
        playerDetailsArea.setEditable(false);
        playersTabContent.getChildren().addAll(playersPieChart, playerDetailsArea);

        Tab playersTab = new Tab("Players", playersTabContent);

        // Populate Players Pie Chart
        Task<Void> fetchPlayersTask = new Task<>() {
            @Override
            protected Void call() {
                JsonArray playerArray = apiClient.fetchPlayerList();
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                for (int i = 0; i < playerArray.size(); i++) {
                    JsonObject player = playerArray.get(i).getAsJsonObject();
                    String playerName = player.get("name").getAsString();
                    String playerId = player.get("id").getAsString();
                    playerMap.put(playerName, playerId);
                    pieChartData.add(new PieChart.Data(playerName, Math.random() * 100)); // Example values
                }
                Platform.runLater(() -> playersPieChart.setData(pieChartData));
                return null;
            }
        };
        new Thread(fetchPlayersTask).start();

        // Add Tabs
        tabPane.getTabs().addAll(seriesTab, playersTab);

        // Stage
        Scene scene = new Scene(tabPane, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cricket Visualizer");
        primaryStage.show();
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Match Type");
        yAxis.setLabel("Count");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Series Statistics");
        return barChart;
    }

    private void updateBarChart(BarChart<String, Number> barChart, JsonObject seriesData) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Match Types");
        series.getData().add(new XYChart.Data<>("ODIs", seriesData.get("odi").getAsInt()));
        series.getData().add(new XYChart.Data<>("T20s", seriesData.get("t20").getAsInt()));
        series.getData().add(new XYChart.Data<>("Tests", seriesData.get("test").getAsInt()));
        barChart.getData().add(series);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
