module CricketVisualizer {
	requires javafx.controls;
	requires com.google.gson;
	requires kotlin.stdlib;
	requires okhttp3;
	requires okio;
	requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
	requires javafx.graphics;
    
	opens application to javafx.graphics, javafx.fxml, gson;
}
