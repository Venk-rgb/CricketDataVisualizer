package application;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SeriesTab {
    private final ApiClient apiClient = new ApiClient();
    private final Map<String, String> seriesMap = new HashMap<>(); // Series name -> Series ID
    private final Map<String, String> matchMap = new HashMap<>(); // Match name -> Match ID

    public Tab getTab() {
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

                // Fetch and display series data
                Task<Void> fetchSeriesTask = new Task<>() {
                    @Override
                    protected Void call() {
                        JsonObject seriesInfo = apiClient.fetchSeriesInfo(seriesId);
                        if (seriesInfo != null && seriesInfo.has("data")) {
                            JsonObject seriesData = seriesInfo.getAsJsonObject("data").getAsJsonObject("info");
                            JsonArray matches = seriesInfo.getAsJsonObject("data").getAsJsonArray("matchList");

                            Platform.runLater(() -> {
                                // Update the background details
                                StringBuilder seriesDetails = new StringBuilder();
                                seriesDetails.append("Series Name: ").append(seriesData.get("name").getAsString()).append("\n");
                                seriesDetails.append("Start Date: ").append(seriesData.get("startdate").getAsString()).append("\n");
                                seriesDetails.append("End Date: ").append(seriesData.get("enddate").getAsString()).append("\n");
                                seriesDetails.append("ODIs: ").append(seriesData.get("odi").getAsInt()).append("\n");
                                seriesDetails.append("T20s: ").append(seriesData.get("t20").getAsInt()).append("\n");
                                seriesDetails.append("Tests: ").append(seriesData.get("test").getAsInt()).append("\n");
                                seriesDetailsArea.setText(seriesDetails.toString());

                                // Create and configure BarChart
                                CategoryAxis xAxis = new CategoryAxis();
                                xAxis.setLabel("Match Type");

                                NumberAxis yAxis = new NumberAxis();
                                yAxis.setLabel("Count");

                                BarChart<String, Number> seriesChart = new BarChart<>(xAxis, yAxis);
                                seriesChart.setTitle(seriesData.get("name").getAsString());

                                XYChart.Series<String, Number> chartData = new XYChart.Series<>();
                                chartData.getData().add(new XYChart.Data<>("ODIs", seriesData.get("odi").getAsInt()));
                                chartData.getData().add(new XYChart.Data<>("T20s", seriesData.get("t20").getAsInt()));
                                chartData.getData().add(new XYChart.Data<>("Tests", seriesData.get("test").getAsInt()));
                                seriesChart.getData().add(chartData);

                                // Popup window for BarChart
                                Stage chartStage = new Stage();
                                VBox chartLayout = new VBox(10);

                                Label seriesNameLabel = new Label("Series: " + seriesData.get("name").getAsString());
                                seriesNameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

                                Label dateRangeLabel = new Label("Start Date: " + seriesData.get("startdate").getAsString()
                                        + " | End Date: " + seriesData.get("enddate").getAsString());
                                dateRangeLabel.setStyle("-fx-font-size: 12; -fx-font-style: italic;");

                                chartLayout.getChildren().addAll(seriesNameLabel, dateRangeLabel, seriesChart);
                                Scene chartScene = new Scene(chartLayout, 400, 300);
                                chartStage.setScene(chartScene);
                                chartStage.setTitle("Series Details");
                                chartStage.show();

                                // Populate matches list in the background
                                matchesListView.getItems().clear();
                                for (int i = 0; i < matches.size(); i++) {
                                    JsonObject match = matches.get(i).getAsJsonObject();
                                    String matchName = match.get("name").getAsString();
                                    String matchId = match.get("id").getAsString();
                                    matchesListView.getItems().add(matchName);
                                    matchMap.put(matchName, matchId);
                                }
                            });
                        }
                        return null;
                    }
                };

                new Thread(fetchSeriesTask).start();
            }
        });

        return seriesTab;
    }
}
