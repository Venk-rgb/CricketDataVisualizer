package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainDashboard extends Application {
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        // Add tabs: Series, Players and Role Distribution tab
        tabPane.getTabs().add(new SeriesTab().getTab());
        tabPane.getTabs().add(new PlayersTab().getTab());
        tabPane.getTabs().add(new PlayerRoleDistributionTab().getTab());

        // Stage setup: a popup with all the three tabs and their functionalities
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cricket Visualizer");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
