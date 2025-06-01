package atbmhttt.atbmcq_16.client.Views.Enrollment;

import atbmhttt.atbmcq_16.client.ClientAlertDialogs;
import atbmhttt.atbmcq_16.client.ViewModels.EnrollmentAndGradesViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateGradesView {
    public static void show(EnrollmentAndGradesViewModel viewModel) {
        Stage dialog = new Stage();
        dialog.setTitle("UPDATE GRADES");
        try {
            Image iconImage = new Image(UpdateGradesView.class.getResource("/images/app_icon.png").toExternalForm());
            dialog.getIcons().add(iconImage);
        } catch (Exception ex) {
        }

        VBox layout = new VBox(12);
        layout.setPadding(new javafx.geometry.Insets(18));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("UPDATE GRADES");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TextField masvField = new TextField();
        masvField.setPromptText("MASV (STUDENT ID)");
        TextField mammField = new TextField();
        mammField.setPromptText("MAMM (COURSE ID)");

        TextField diemthField = new TextField();
        diemthField.setPromptText("DIEMTH (0-10, BLANK FOR NULL)");
        TextField diemqtField = new TextField();
        diemqtField.setPromptText("DIEMQT (0-10, BLANK FOR NULL)");
        TextField diemckField = new TextField();
        diemckField.setPromptText("DIEMCK (0-10, BLANK FOR NULL)");
        TextField diemtkField = new TextField();
        diemtkField.setPromptText("DIEMTK (0-10, BLANK FOR NULL)");

        layout.getChildren().addAll(
                titleLabel,
                new Label("MASV"), masvField,
                new Label("MAMM"), mammField,
                new Label("DIEMTH"), diemthField,
                new Label("DIEMQT"), diemqtField,
                new Label("DIEMCK"), diemckField,
                new Label("DIEMTK"), diemtkField);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button updateBtn = new Button("Update");
        Button cancelBtn = new Button("Cancel");
        buttonBox.getChildren().addAll(updateBtn, cancelBtn);

        layout.getChildren().add(buttonBox);

        updateBtn.setOnAction(ev -> {
            String masv = masvField.getText().trim();
            String mamm = mammField.getText().trim();
            Double diemth = parseGrade(diemthField.getText().trim(), "DIEMTH");
            Double diemqt = parseGrade(diemqtField.getText().trim(), "DIEMQT");
            Double diemck = parseGrade(diemckField.getText().trim(), "DIEMCK");
            Double diemtk = parseGrade(diemtkField.getText().trim(), "DIEMTK");
            if (masv.isEmpty() || mamm.isEmpty()) {
                AlertDialog.showErrorAlert("MISSING INFORMATION", null, "Please fill in MASV and MAMM.", null, 400,
                        200);
                return;
            }
            if (diemth == null && !diemthField.getText().trim().isEmpty())
                return;
            if (diemqt == null && !diemqtField.getText().trim().isEmpty())
                return;
            if (diemck == null && !diemckField.getText().trim().isEmpty())
                return;
            if (diemtk == null && !diemtkField.getText().trim().isEmpty())
                return;
            try {
                viewModel.updateGrades(masv, mamm, diemth, diemqt, diemck, diemtk);
                AlertDialog.showInformationAlert("GRADES UPDATED", null,
                        "Grades updated for MASV: " + masv.toUpperCase() + ", MAMM: " + mamm.toUpperCase(), null, 400,
                        200);
                dialog.close();
            } catch (IllegalArgumentException e) {
                AlertDialog.showErrorAlert("INVALID INPUT", null, e.getMessage(), null, 400, 200);
            } catch (java.sql.SQLException e) {
                ClientAlertDialogs.displayGeneralSQLErrorDialog();
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("No enrollment")) {
                    AlertDialog.showErrorAlert("ERROR UPDATING GRADES",
                            null,
                            "Cannot update grades for student " + masv.toUpperCase() + " in course "
                                    + mamm.toUpperCase()
                                    + ".\nNo matching enrollment was found."
                                    + "\nPlease verify that the student is indeed enrolled in the course and try again!"
                                    + "\nIf the issue persists, contact your supervisor or authorized personnel for assistance.",
                            null, 500, 300);
                }
            }
        });

        cancelBtn.setOnAction(ev -> dialog.close());

        Scene scene = new Scene(layout, 350, 500);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }

    private static Double parseGrade(String text, String label) {
        if (text.isEmpty())
            return null;
        try {
            double value = Double.parseDouble(text);
            return value;
        } catch (NumberFormatException e) {
            AlertDialog.showErrorAlert("Invalid input", null, label + " must be a number between 0 and 10 or blank.",
                    null, 400, 200);
            return null;
        }
    }
}
