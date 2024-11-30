package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SeriesView {
	private JsonObject seriesInfo;

	public SeriesView(JsonObject seriesInfo) {
		this.seriesInfo = seriesInfo;
	}

	public VBox getView() {
		VBox layout = new VBox();
		layout.setSpacing(10);
		layout.setAlignment(Pos.CENTER);

		JsonObject info = seriesInfo.getAsJsonObject("data").getAsJsonObject("info");
		String name = info.get("name").getAsString();
		String startDate = info.get("startdate").getAsString();
		String endDate = info.get("enddate").getAsString();
		int t20s = info.get("t20").getAsInt();
		int odis = info.get("odi").getAsInt();
		int tests = info.get("test").getAsInt();

		Label nameLabel = new Label("Series: " + name);
		Label dateLabel = new Label("Start Date: " + startDate + " | End Date: " + endDate);
		Label t20Label = new Label("T20s: " + t20s);
		Label odiLabel = new Label("ODIs: " + odis);
		Label testLabel = new Label("Tests: " + tests);

		JsonArray matchList = seriesInfo.getAsJsonObject("data").getAsJsonArray("matchList");
		VBox matchesBox = new VBox();
		matchesBox.setSpacing(5);
		for (int i = 0; i < matchList.size(); i++) {
			JsonObject match = matchList.get(i).getAsJsonObject();
			String matchName = match.get("name").getAsString();
			Label matchLabel = new Label("Match: " + matchName);
			matchesBox.getChildren().add(matchLabel);
		}

		layout.getChildren().addAll(nameLabel, dateLabel, t20Label, odiLabel, testLabel, matchesBox);
		return layout;
	}
}
