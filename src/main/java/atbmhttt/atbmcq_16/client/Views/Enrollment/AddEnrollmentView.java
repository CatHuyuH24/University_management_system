package atbmhttt.atbmcq_16.client.Views.Enrollment;

import java.sql.SQLException;

import atbmhttt.atbmcq_16.client.ViewModels.EnrollmentViewModel;
import atbmhttt.atbmcq_16.client.Views.ClientAlertDialogs;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddEnrollmentView {
    public static void show(EnrollmentViewModel enrollmentViewModel) {
        Stage dialog = new Stage();
        dialog.setTitle("Add Enrollment");
        try {
            javafx.scene.image.Image iconImage = new javafx.scene.image.Image(
                    AddEnrollmentView.class.getResource("/images/app_icon.png").toExternalForm());
            dialog.getIcons().add(iconImage);
        } catch (Exception ex) {
            // ignore if icon not found
        }
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");
        Label masvLabel = new Label("MASV:");
        TextField masvField = new TextField();
        Label mammLabel = new Label("MAMM:");
        TextField mammField = new TextField();
        grid.add(masvLabel, 0, 0);
        grid.add(masvField, 1, 0);
        grid.add(mammLabel, 0, 1);
        grid.add(mammField, 1, 1);
        Button confirmBtn = new Button("Add");
        grid.add(confirmBtn, 1, 2);
        confirmBtn.setDefaultButton(true);
        confirmBtn.setOnAction(ev -> {
            String masv = masvField.getText().trim();
            String mamm = mammField.getText().trim();
            if (masv.isEmpty() || mamm.isEmpty()) {
                atbmhttt.atbmcq_16.dialogs.AlertDialog.showErrorAlert(
                        "Missing information", null, "Please fill in all required information.",
                        null, 400, 200);
                return;
            }
            try {
                enrollmentViewModel.addEnrollment(masv, mamm);
                AlertDialog.showInformationAlert("Enrolled successfully",
                        null,
                        masv + " has enrolled for " + mamm, null, 300, 200);
                dialog.close();

            } catch (IllegalArgumentException ex) {
                AlertDialog.showErrorAlert("Disallowed input",
                        null, ex.getMessage(),
                        null, 400, 200);
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    AlertDialog.showErrorAlert("Error enrolling",
                            null,
                            masv + " has already enrolled in course " + mamm
                                    + ".\nPlease re-check and try again or contact authorized personnel.",
                            null, 400, 200);
                } else if (e.getErrorCode() == 2291) { // ORA-02291: integrity constraint violation - parent key not
                                                       // found
                    AlertDialog.showErrorAlert("Error enrolling",
                            null,
                            "Cannot find course " + mamm
                                    + ".\nPlease re-check and try again. If you're unsure, contact your department staff or authorized personnel.",
                            null, 400, 200);
                } else {
                    ClientAlertDialogs.displayGeneralSQLErrorDialog();
                }
            } catch (Exception ex) {
                ClientAlertDialogs.displayUnexpectedErrorDialog();
            }
        });
        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.setWidth(400);
        dialog.setHeight(200);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}
