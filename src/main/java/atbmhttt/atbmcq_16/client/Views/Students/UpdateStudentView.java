package atbmhttt.atbmcq_16.client.Views.Students;

import atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel;
import atbmhttt.atbmcq_16.client.Views.ClientAlertDialogs;
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
    private static StudentsViewModel viewModel;
    private static StudentsView view;

    public static void setStudentsViewModel(StudentsView studentsView, StudentsViewModel studentsViewModel) {
        viewModel = studentsViewModel;
        view = studentsView;
    }

    public static void show() {
        Stage dialog = new Stage();
        dialog.setTitle("Update Student information");
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
        masvLabel.setAlignment(Pos.CENTER_LEFT);
        masvLabel.setStyle("-fx-alignment: center-left;");
        TextField masvField = new TextField();
        masvField.setPromptText("Enter student ID");

        Label colLabel = new Label("What to update:");
        colLabel.setAlignment(Pos.CENTER_LEFT);
        colLabel.setStyle("-fx-alignment: center-left;");
        ComboBox<String> colCombo = new ComboBox<>();
        colCombo.getItems().addAll("MASV", "HOTEN", "PHAI", "NGSINH", "DCHI", "DT", "KHOA", "TINHTRANG");
        colCombo.getSelectionModel().selectFirst();

        Label newValLabel = new Label("New value:");
        newValLabel.setAlignment(Pos.CENTER_LEFT);
        newValLabel.setStyle("-fx-alignment: center-left;");
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
                if (viewModel == null) {
                    viewModel = new atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel();
                }
                var newStudentInfoToBeRendered = viewModel.updateStudentAttributeAndReturnStudentToBeRendered(masv, col,
                        newVal);

                if (null != newStudentInfoToBeRendered) {
                    view.renderNewStudent(newStudentInfoToBeRendered);
                }
                // being null just means there is no need to re-render

                AlertDialog.showInformationAlert("Updated sucessfully",
                        null, col + " has been updated to " + newVal,
                        null, 400, 200);
                dialog.close();

            } catch (IllegalArgumentException e) {
                AlertDialog.showErrorAlert("Disallowed input", null,
                        e.getMessage(), null, 400, 200);
            } catch (Exception ex) {
                ClientAlertDialogs.displayGeneralErrorDialog();
            }
        });
        cancelBtn.setOnAction(ev -> dialog.close());

        layout.getChildren().addAll(masvLabel, masvField, colLabel, colCombo, newValLabel, newValField, buttonBox);
        Scene scene = new Scene(layout, 350, 320);
        dialog.setScene(scene);
        dialog.show();
    }
}
