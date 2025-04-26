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
            iconPath = "/images/app_icon.png";
        }

        try {
            alertStage.getIcons().add(new Image(AlertDialog.class.getResource(iconPath).toExternalForm()));
        } catch (Exception e) {
            System.err.println("AlertDialog: Error occurred when trying to get icon image");
        }
        // Show the alert
        alert.showAndWait();
    }

    /**
     * Displays a confirmation alert dialog with the specified title, header text,
     * content text, and an optional icon. The method waits for the user to respond
     * and returns the result as a ButtonType.
     *
     * @param title       The title of the alert dialog.
     * @param headerText  The header text displayed in the alert dialog.
     * @param contentText The main content text displayed in the alert dialog.
     * @param iconPath    The path to the icon image to be displayed in the title
     *                    bar
     *                    of the alert dialog. If null, the app icon is used.
     * @return The ButtonType representing the user's response. Possible outcomes
     *         are:
     *         - ButtonType.OK: If the user confirms the action.
     *         - ButtonType.CANCEL: If the user cancels the action.
     *         - null: If the dialog is closed without any response.
     */
    public static ButtonType showAndGetResultConfirmationAlert(String title, String headerText, String contentText,
            String iconPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Set the icon in the title bar
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        if (null == iconPath) {
            iconPath = "/images/app_icon.png";
        }

        try {
            alertStage.getIcons().add(new Image(AlertDialog.class.getResource(iconPath).toExternalForm()));
        } catch (Exception e) {
            System.err.println("AlertDialog: Error occurred when trying to get icon image");
        }

        // Adjust the size of the dialog
        alert.getDialogPane().setPrefSize(400, 200); // Set preferred width and height

        // Show the alert
        ButtonType response;
        if (alert.showAndWait().isPresent()) {
            response = alert.getResult();
        } else {
            response = null;
        }
        return response;

    }

    public static void showInformationAlert(String title, String headerText, String contentText, String iconPath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Set the icon in the title bar
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        if (null == iconPath) {
            iconPath = "/images/app_icon.png";
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
