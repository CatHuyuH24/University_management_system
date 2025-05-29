package atbmhttt.atbmcq_16.client.Views.Students;

import atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentsView {
    private final StudentsViewModel studentsViewModel;

    public StudentsView() {
        this.studentsViewModel = new StudentsViewModel();
    }

    public void displayStudents(BorderPane contentArea) {
        ObservableList<String[]> students = studentsViewModel.getStudents();
        VBox studentsContainer = new VBox(20);
        studentsContainer.setStyle("-fx-padding: 16;");
        studentsContainer.setAlignment(Pos.CENTER);

        if (students.isEmpty()) {
            javafx.scene.control.Label emptyLabel = new javafx.scene.control.Label(
                    "No students information to be displayed");
            emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #888;");
            studentsContainer.getChildren().add(emptyLabel);
        } else {
            String[] labels = { "Student ID", "Full Name", "Gender", "Date of Birth", "Address", "Phone", "Department",
                    "Status" };
            for (String[] student : students) {
                VBox studentBox = new VBox(5);
                studentBox.setStyle(
                        "-fx-border-color: #888; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-background-color: #f9f9f9; -fx-margin: 16");
                studentBox.setAlignment(Pos.TOP_LEFT);
                for (int i = 0; i < labels.length; i++) {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    javafx.scene.control.Label label = new javafx.scene.control.Label(labels[i] + ": ");
                    label.setMinWidth(90);
                    javafx.scene.control.Label value = new javafx.scene.control.Label(student[i]);
                    row.getChildren().addAll(label, value);
                    studentBox.getChildren().add(row);
                }
                studentsContainer.getChildren().add(studentBox);
            }
        }

        // --- Scrollable students list ---
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(studentsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // --- Sticky box at the bottom ---
        HBox bottomSection = new HBox();
        bottomSection.setAlignment(Pos.CENTER_RIGHT);
        bottomSection.setStyle(
                "-fx-background-color: #f0f0f0; -fx-padding: 12 24 12 24; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");
        javafx.scene.control.Button updateAllBtn = new javafx.scene.control.Button("Update");
        updateAllBtn.setOnAction(e -> UpdateStudentView.show());
        bottomSection.getChildren().add(updateAllBtn);

        BorderPaneHelper.setAllSections(contentArea, null, scrollPane, null, bottomSection, null);
    }
}
