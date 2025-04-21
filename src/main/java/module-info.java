module atbmhttt.atbmcq_16 {
    requires transitive javafx.controls; // Make javafx.controls available to dependent modules
    requires transitive javafx.graphics;
    requires java.sql;

    opens atbmhttt.atbmcq_16 to javafx.fxml;
    opens atbmhttt.atbmcq_16.admin.Views to javafx.graphics;

    exports atbmhttt.atbmcq_16;
}