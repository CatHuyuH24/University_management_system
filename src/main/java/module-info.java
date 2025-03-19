module atbmhttt.atbmcq_16 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens atbmhttt.atbmcq_16 to javafx.fxml;

    exports atbmhttt.atbmcq_16;
}