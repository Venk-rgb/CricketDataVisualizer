module CricketVisualizer {
	requires javafx.controls;
	requires com.google.gson;
	requires kotlin.stdlib;
	requires okhttp3;
	requires okio;

	opens application to javafx.graphics, javafx.fxml, gson;
}
