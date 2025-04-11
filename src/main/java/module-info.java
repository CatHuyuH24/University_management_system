module atbmhttt.atbmcq_16 {
    requires transitive javafx.controls; // Make javafx.controls available to dependent modules
    requires transitive javafx.graphics;
    requires java.sql;

    opens atbmhttt.atbmcq_16 to javafx.fxml;

    exports atbmhttt.atbmcq_16;
}