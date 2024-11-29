package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainDashboard extends Application {
    private ApiClient apiClient = new ApiClient();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Fetch data from APIs
            JsonArray seriesData = apiClient.fetchData("/series");
            JsonArray matchData = apiClient.fetchData("/matches");
            JsonObject playerData = apiClient.fetchPlayerInfo("16592242-ef26-45d9-bf23-fc090e90fbbe");
            System.out.println("Player Info JSON: " + playerData.toString());
            // Create views
            SeriesView seriesView = new SeriesView(seriesData);
            MatchView matchView = new MatchView(matchData);
            PlayerView playerView = new PlayerView(playerData);

            // Add views to tabs
            TabPane tabPane = new TabPane();
            tabPane.getTabs().add(new Tab("Series Overview", seriesView.getView()));
            tabPane.getTabs().add(new Tab("Match Details", matchView.getView()));
            tabPane.getTabs().add(new Tab("Player Stats", playerView.getView()));

            // Create and show the scene
            Scene scene = new Scene(tabPane, 1200, 800);
            primaryStage.setTitle("Cricket Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
