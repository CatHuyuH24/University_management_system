package atbmhttt.atbmcq_16.client.Views.Enrollment;

import atbmhttt.atbmcq_16.client.ViewModels.EnrollmentAndGradesViewModel;
import atbmhttt.atbmcq_16.client.Views.ClientAlertDialogs;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;

public class DeleteEnrollmentView {
    public static void show(EnrollmentAndGradesViewModel viewModel) {
        Stage dialog = new Stage();
        dialog.setTitle("Unenroll");
        try {
            Image iconImage = new Image(
                    DeleteEnrollmentView.class.getResource("/images/app_icon.png").toExternalForm());
            dialog.getIcons().add(iconImage);
        } catch (Exception ex) {
            // ignore if icon not found
        }
        VBox layout = new VBox(12);
        layout.setPadding(new javafx.geometry.Insets(18));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Unenroll");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label masvLabel = new Label("MASV (Student ID):");
        masvLabel.setAlignment(Pos.CENTER_LEFT);
        masvLabel.setStyle("-fx-alignment: center-left;");
        TextField masvField = new TextField();
        masvField.setPromptText("Enter MASV");

        Label mammLabel = new Label("MAMM (Course ID):");
        mammLabel.setAlignment(Pos.CENTER_LEFT);
        mammLabel.setStyle("-fx-alignment: center-left;");
        TextField mammField = new TextField();
        mammField.setPromptText("Enter MAMM");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button unenrollBtn = new Button("Unenroll");
        Button cancelBtn = new Button("Cancel");
        buttonBox.getChildren().addAll(unenrollBtn, cancelBtn);

        unenrollBtn.setOnAction(ev -> {
            String masv = masvField.getText().trim();
            String mamm = mammField.getText().trim();
            if (masv.isEmpty() || mamm.isEmpty()) {
                AlertDialog.showErrorAlert("Missing information", null, "Please fill in both MASV and MAMM.", null, 400,
                        200);
                return;
            }
            try {
                viewModel.deleteEnrollment(masv, mamm);
                AlertDialog.showInformationAlert("Unenrolled", null,
                        "Enrollment with MASV '" + masv.toUpperCase() + "' and MAMM '" + mamm.toUpperCase()
                                + "' has been unenrolled.",
                        null, 400, 200);
                dialog.close();
            } catch (IllegalArgumentException e) {
                AlertDialog.showErrorAlert("Disallowed input", null, e.getMessage(), null, 400, 200);
            } catch (SQLException e) {
                ClientAlertDialogs.displayGeneralSQLErrorDialog();
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null
                        && (msg.contains("not found"))) {
                    AlertDialog.showErrorAlert("Error unenrolling", null,
                            "No enrollment record found for student ID \"" + masv.toUpperCase() +
                                    "in course " + mamm.toUpperCase() +
                                    ".\nPlease verify the information and try again.\nIf you need further assistance, contact your supervisor or authorized personnel.",
                            null, 400, 200);
                } else {
                    ClientAlertDialogs.displayUnexpectedErrorDialog();
                }
            }
        });
        cancelBtn.setOnAction(ev -> dialog.close());

        layout.getChildren().addAll(titleLabel, masvLabel, masvField, mammLabel, mammField, buttonBox);
        Scene scene = new Scene(layout, 370, 260);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }
}
