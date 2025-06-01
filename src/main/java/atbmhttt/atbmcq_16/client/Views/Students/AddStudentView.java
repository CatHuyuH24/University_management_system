package atbmhttt.atbmcq_16.client.Views.Students;

import java.sql.SQLException;

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

public class AddStudentView {
    public static void show(StudentsView studentsView, StudentsViewModel studentsViewModel) {
        Stage dialog = new Stage();
        dialog.setTitle("ADD NEW STUDENT");
        try {
            Image iconImage = new Image(AddStudentView.class.getResource("/images/app_icon.png").toExternalForm());
            dialog.getIcons().add(iconImage);
        } catch (Exception ex) {
            // ignore if icon not found
        }
        VBox layout = new VBox(12);
        layout.setPadding(new javafx.geometry.Insets(18));
        layout.setAlignment(Pos.CENTER);

        String[] labels = { "MASV", "HOTEN", "PHAI", "NGSINH", "DCHI", "DT", "KHOA" };
        TextField[] fields = new TextField[labels.length];
        VBox formBox = new VBox(8);
        formBox.setAlignment(Pos.TOP_LEFT);
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i] + ":");
            label.setAlignment(Pos.CENTER_LEFT);
            label.setStyle("-fx-alignment: center-left;");
            TextField field = new TextField();
            field.setPromptText("Enter " + labels[i].toLowerCase());
            formBox.getChildren().addAll(label, field);
            fields[i] = field;
        }
        // Make the form scrollable
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        saveBtn.setOnAction(ev -> {
            String[] values = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                values[i] = fields[i].getText().trim();
                if (values[i].isEmpty()) {
                    AlertDialog.showErrorAlert("MISSING INFORMATION", null, "Please fill in all required information.",
                            null, 400, 200);
                    return;
                }
            }

            try {
                studentsViewModel.addStudent(values);
                AlertDialog.showInformationAlert("STUDENT ADDED", null, "Student has been added successfully.", null,
                        400, 200);
                dialog.close();
            } catch (IllegalArgumentException e) {
                AlertDialog.showErrorAlert("DISALLOWED INPUT", null, e.getMessage(), null, 400, 200);
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    AlertDialog.showErrorAlert("ERROR ADDING NEW STUDENT",
                            null,
                            "MASV you enter already exists.\nPlease re-check and try with another MASV or contact your supervisor.",
                            null, 400, 200);
                } else if (e.getErrorCode() == 2291) { // ORA-02291: integrity constraint violation - parent key not
                                                       // found
                    AlertDialog.showErrorAlert("ERROR ADDING NEW STUDENT",
                            null,
                            "KHOA you enter could not be found.\nPlease re-check and try again. If you're unsure, contact your department staff or authorized personnel.",
                            null, 400, 200);
                } else {
                    ClientAlertDialogs.displayGeneralSQLErrorDialog();
                }
            } catch (Exception e) {
                ClientAlertDialogs.displayUnexpectedErrorDialog();
            }
        });
        cancelBtn.setOnAction(ev -> dialog.close());

        layout.getChildren().addAll(scrollPane, buttonBox);
        Scene scene = new Scene(layout, 350, 420);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }
}
