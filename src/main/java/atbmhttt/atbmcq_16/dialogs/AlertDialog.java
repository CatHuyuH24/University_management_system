package atbmhttt.atbmcq_16.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertDialog {
    public static void showErrorAlert(String title, String headerText, String contentText, String iconPath) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Set the icon in the title bar
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        if (null == iconPath) {
            iconPath = "/images/error_icon.jpg";
        }

        try {
            alertStage.getIcons().add(new Image(AlertDialog.class.getResource(iconPath).toExternalForm()));
        } catch (Exception e) {
            System.err.println("AlertDialog: Error occurred when trying to get icon image");
        }
        // Show the alert
        alert.showAndWait();
    }
}
