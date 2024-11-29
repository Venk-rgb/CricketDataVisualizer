package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Stat {
    private final StringProperty fn;
    private final StringProperty matchType;
    private final StringProperty stat;
    private final StringProperty value;

    public Stat(String fn, String matchType, String stat, String value) {
        this.fn = new SimpleStringProperty(fn);
        this.matchType = new SimpleStringProperty(matchType);
        this.stat = new SimpleStringProperty(stat);
        this.value = new SimpleStringProperty(value);
    }

    public StringProperty fnProperty() {
        return fn;
    }

    public StringProperty matchTypeProperty() {
        return matchType;
    }

    public StringProperty statProperty() {
        return stat;
    }

    public StringProperty valueProperty() {
        return value;
    }
}
