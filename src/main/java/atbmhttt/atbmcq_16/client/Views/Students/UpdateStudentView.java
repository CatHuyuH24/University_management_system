package atbmhttt.atbmcq_16.client.Views.Students;

import atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpdateStudentView {
    private static StudentsViewModel studentsViewModel;

    public static void setStudentsViewModel(atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel svm) {
        studentsViewModel = svm;
    }

    public static void show() {
        Stage dialog = new Stage();
        dialog.setTitle("Update Student Attribute");
        try {
            Image iconImage = new Image(UpdateStudentView.class.getResource("/images/app_icon.png").toExternalForm());
            dialog.getIcons().add(iconImage);
        } catch (Exception ex) {
            // ignore if icon not found
        }
        VBox layout = new VBox(12);
        layout.setPadding(new javafx.geometry.Insets(18));
        layout.setAlignment(Pos.CENTER);

        Label masvLabel = new Label("Student ID:");
        TextField masvField = new TextField();
        masvField.setPromptText("Enter student ID");

        Label colLabel = new Label("What to update:");
        ComboBox<String> colCombo = new ComboBox<>();
        colCombo.getItems().addAll("MASV", "HOTEN", "PHAI", "NGSINH", "DCHI", "DT", "KHOA", "TINHTRANG");
        colCombo.getSelectionModel().selectFirst();

        Label newValLabel = new Label("New value:");
        TextField newValField = new TextField();
        newValField.setPromptText("Enter new value");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        saveBtn.setOnAction(ev -> {
            String masv = masvField.getText().trim();
            String col = colCombo.getValue();
            String newVal = newValField.getText().trim();
            if (masv.isEmpty() || newVal.isEmpty()) {
                AlertDialog.showErrorAlert("Missing information", null, "Please fill in all required information.",
                        null, 400, 200);
                return;
            }
            try {
                if (studentsViewModel == null) {
                    studentsViewModel = new atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel();
                }
                studentsViewModel.updateStudentAttribute(masv, col, newVal);

                AlertDialog.showInformationAlert("Updated sucessfully",
                        null, col + " has been updated to " + newVal,
                        null, 400, 200);

            } catch (Exception ex) {
                AlertDialog.showErrorAlert("Error", null,
                        "You CANNOT perform this ACTION!\nIf you think this is a mistake or need help, please reach out to an authorized staff member or administrator.",
                        null, 400, 200);
            }
        });
        cancelBtn.setOnAction(ev -> dialog.close());

        layout.getChildren().addAll(masvLabel, masvField, colLabel, colCombo, newValLabel, newValField, buttonBox);
        Scene scene = new Scene(layout, 350, 320);
        dialog.setScene(scene);
        dialog.show();
    }
}
