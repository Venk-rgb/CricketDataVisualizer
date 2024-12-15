package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerRoleDistributionTab {
    private final ApiClient apiClient = new ApiClient();

    public Tab getTab() {
        VBox roleDistributionContent = new VBox();
        roleDistributionContent.setSpacing(20);

        PieChart roleChart = new PieChart();
        Label roleChartLabel = new Label("Player Role Distribution");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Country");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Players");

        BarChart<String, Number> countryBarChart = new BarChart<>(xAxis, yAxis);
        countryBarChart.setTitle("Player Country Distribution");

        roleDistributionContent.getChildren().addAll(roleChartLabel, roleChart, countryBarChart);

        Tab roleDistributionTab = new Tab("Player Role Distribution", roleDistributionContent);

        roleDistributionTab.setOnSelectionChanged(event -> {
            if (roleDistributionTab.isSelected() && roleChart.getData().isEmpty() && countryBarChart.getData().isEmpty()) {
                Task<Void> fetchRolesAndCountriesTask = new Task<>() {
                    @Override
                    protected Void call() {
                        ExecutorService executor = Executors.newFixedThreadPool(10);
                        Map<String, Integer> roleCount = new HashMap<>();
                        Map<String, Integer> countryCount = new HashMap<>();

                        JsonArray playerArray = apiClient.fetchPlayerList();

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
                            roleChart.getData().clear();
                            roleCount.forEach((role, count) -> roleChart.getData().add(new PieChart.Data(role, count)));

                            countryBarChart.getData().clear();
                            XYChart.Series<String, Number> countrySeries = new XYChart.Series<>();
                            countrySeries.setName("Countries");
                            countryCount.forEach((country, count) -> countrySeries.getData().add(new XYChart.Data<>(country, count)));
                            countryBarChart.getData().add(countrySeries);
                        });

                        return null;
                    }
                };

                new Thread(fetchRolesAndCountriesTask).start();
            }
        });

        return roleDistributionTab;
    }
}
