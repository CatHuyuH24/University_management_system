package atbmhttt.atbmcq_16.client.Views.Students;

import atbmhttt.atbmcq_16.client.ClientAlertDialogs;
import atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel;
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

public class DeleteStudentView {
    public static void show(StudentsViewModel studentsViewModel) {
        Stage dialog = new Stage();
        dialog.setTitle("DELETE STUDENT");
        try {
            Image iconImage = new Image(DeleteStudentView.class.getResource("/images/app_icon.png").toExternalForm());
            dialog.getIcons().add(iconImage);
        } catch (Exception ex) {
            // ignore if icon not found
        }
        VBox layout = new VBox(12);
        layout.setPadding(new javafx.geometry.Insets(18));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("DELETE STUDENT");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label masvLabel = new Label("MASV (STUDENT ID):");
        masvLabel.setAlignment(Pos.CENTER_LEFT);
        masvLabel.setStyle("-fx-alignment: center-left;");
        TextField masvField = new TextField();
        masvField.setPromptText("ENTER MASV TO DELETE");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button deleteBtn = new Button("Delete");
        Button cancelBtn = new Button("Cancel");
        buttonBox.getChildren().addAll(deleteBtn, cancelBtn);

        deleteBtn.setOnAction(ev -> {
            String masv = masvField.getText().trim();

            if (masv.isEmpty()) {
                AlertDialog.showErrorAlert("MISSING INFORMATION", null, "Please fill in MASV.",
                        null, 400, 200);
                return;
            }
            try {
                studentsViewModel.deleteStudentByMASV(masv);
                AlertDialog.showInformationAlert("STUDENT DELETED", null,
                        "Student with MASV '" + masv + "' has been deleted.", null, 400, 200);
                dialog.close();
            } catch (IllegalArgumentException e) {
                AlertDialog.showErrorAlert("INVALID INPUT", null, e.getMessage(), null, 400, 200);
            } catch (SQLException e) {
                ClientAlertDialogs.displayGeneralSQLErrorDialog();
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && (msg.contains("not found"))) {
                    AlertDialog.showErrorAlert("ERROR DELETING STUDENT", null,
                            "No student record found with the provided MASV. Please re-check and try again.\nIf you need further assistance, please contact your supervisor or authorized personnel.",
                            null, 400, 200);
                } else {
                    ClientAlertDialogs.displayUnexpectedErrorDialog();
                }
            }
        });
        cancelBtn.setOnAction(ev -> dialog.close());

        layout.getChildren().addAll(titleLabel, masvLabel, masvField, buttonBox);
        Scene scene = new Scene(layout, 350, 220);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }
}
