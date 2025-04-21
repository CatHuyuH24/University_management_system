package atbmhttt.atbmcq_16.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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

    public static ButtonType showAndGetResultConfirmationAlert(String title, String headerText, String contentText,
            String iconPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Set the icon in the title bar
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        if (null == iconPath) {
            iconPath = "/images/question_icon.jpg";
        }

        try {
            alertStage.getIcons().add(new Image(AlertDialog.class.getResource(iconPath).toExternalForm()));
        } catch (Exception e) {
            System.err.println("AlertDialog: Error occurred when trying to get icon image");
        }
        // Show the alert
        ButtonType response;
        if (alert.showAndWait().isPresent()) {
            response = alert.getResult();
        } else {
            response = null;
        }
        return response;

    }
}
