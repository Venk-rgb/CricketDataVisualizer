package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private static final String API_KEY = "1ea530e7-eee6-4c27-a0f7-686feb0b16e7"; // Replace with your CricAPI key
    private static final String API_URL = "https://api.cricapi.com/v1/players?apikey=" + API_KEY + "&offset=0";

    private BarChart<String, Number> barChart;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Set up the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Player Name");
        yAxis.setLabel("Dummy Value");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Player List");

        // Load data from CricAPI
        loadDataFromAPI();

        root.setCenter(barChart);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("CricAPI Player List Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadDataFromAPI() {
        new Thread(() -> {
            try {
                String playerListJson = fetchPlayerList();
                if (playerListJson != null) {
                    // Parse the data on the JavaFX Application Thread
                    Platform.runLater(() -> updateChartWithAPIData(playerListJson));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String fetchPlayerList() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Failed to connect: " + responseCode);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateChartWithAPIData(String playerListJson) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Players");

        try {
            // Basic JSON parsing using substring and indexOf
            String playersData = extractValue(playerListJson, "\"data\":[", "]");
            String[] players = playersData.split("\\},\\{");

            Map<String, Integer> playerCounts = new HashMap<>();
            int count = 1; // Using a dummy value for demonstration

            for (String playerData : players) {
                String playerName = extractValue(playerData, "\"name\":\"", "\"");

                // Add to the map for simplicity; we use 'count' as a dummy value.
                playerCounts.put(playerName, count);
            }

            for (Map.Entry<String, Integer> entry : playerCounts.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        barChart.getData().add(series);
    }

    private String extractValue(String json, String startPattern, String endPattern) {
        int startIndex = json.indexOf(startPattern);
        if (startIndex == -1) {
            return "";
        }
        startIndex += startPattern.length();
        int endIndex = json.indexOf(endPattern, startIndex);
        if (endIndex == -1) {
            return "";
        }
        return json.substring(startIndex, endIndex);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
