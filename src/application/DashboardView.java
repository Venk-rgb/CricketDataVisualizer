package application;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DashboardView {
    private BarChart<String, Number> barChart;

    public VBox getView(JsonArray players) {
        VBox layout = new VBox();

        // Create the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Player Name");
        yAxis.setLabel("Dummy Value");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Player List");

        updateChart(players);

        layout.getChildren().add(barChart);
        return layout;
    }

    private void updateChart(JsonArray players) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Players");

        for (int i = 0; i < players.size(); i++) {
            JsonObject player = players.get(i).getAsJsonObject();
            String playerName = player.get("name").getAsString();
            series.getData().add(new XYChart.Data<>(playerName, i + 1)); // Dummy values for demonstration
        }

        barChart.getData().clear();
        barChart.getData().add(series);
    }
}
