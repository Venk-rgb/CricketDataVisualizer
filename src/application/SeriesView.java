package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class SeriesView {
    private JsonArray seriesData;

    public SeriesView(JsonArray seriesData) {
        this.seriesData = seriesData;
    }

    public VBox getView() {
        VBox layout = new VBox(); // Create a new VBox instance for each call to avoid duplication
        layout.setSpacing(10);

        ComboBox<String> seriesDropdown = new ComboBox<>();

        for (int i = 0; i < seriesData.size(); i++) {
            JsonObject series = seriesData.get(i).getAsJsonObject();
            seriesDropdown.getItems().add(series.get("name").getAsString());
        }

        // Add only unique components to the VBox
        layout.getChildren().add(seriesDropdown);

        return layout;
    }
}
