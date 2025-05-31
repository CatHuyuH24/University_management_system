package atbmhttt.atbmcq_16.client.Views.Students;

import atbmhttt.atbmcq_16.client.ViewModels.StudentsViewModel;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentsView {
    private final StudentsViewModel studentsViewModel;
    private final String[] labels = { "Student ID",
            "Full Name",
            "Gender",
            "Date of Birth",
            "Address",
            "Phone",
            "Department",
            "Status" };
    private VBox studentsContainer;
    private ObservableList<String[]> students;

    public StudentsView() {
        this.studentsViewModel = new StudentsViewModel();
    }

    public void displayStudents(BorderPane contentArea) {
        students = studentsViewModel.getStudents();

        studentsContainer = new VBox(20);
        studentsContainer.setStyle("-fx-padding: 16;");
        studentsContainer.setAlignment(Pos.CENTER);
        if (students.isEmpty()) {
            javafx.scene.control.Label emptyLabel = new javafx.scene.control.Label(
                    "No students information to be displayed");
            emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #888;");
            studentsContainer.getChildren().add(emptyLabel);
        } else {
            for (String[] student : students) {
                GridPane studentGrid = new GridPane();
                studentGrid.setStyle(
                        "-fx-border-color: #888; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-background-color: #f9f9f9; -fx-margin: 16");
                studentGrid.setAlignment(Pos.TOP_LEFT);
                studentGrid.setHgap(10);
                studentGrid.setVgap(5);
                for (int i = 0; i < labels.length; i++) {
                    javafx.scene.control.Label label = new javafx.scene.control.Label(labels[i] + ": ");
                    label.setMinWidth(90);
                    javafx.scene.control.Label value = new javafx.scene.control.Label(student[i]);
                    studentGrid.add(label, 0, i);
                    studentGrid.add(value, 1, i);
                }
                studentsContainer.getChildren().add(studentGrid);
            }
        }

        // --- Scrollable students list ---
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(studentsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // --- Sticky box at the bottom ---
        HBox bottomSection = new HBox(12); // Add spacing between buttons
        bottomSection.setAlignment(Pos.CENTER_RIGHT);
        bottomSection.setStyle(
                "-fx-background-color: #f0f0f0; -fx-padding: 12 24 12 24; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");
        javafx.scene.control.Button updateButton = new javafx.scene.control.Button("Update Information");
        javafx.scene.control.Button addButton = new javafx.scene.control.Button("Add Student");
        javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Delete Student");

        UpdateStudentView.setStudentsViewModel(this, studentsViewModel);
        updateButton.setOnAction(e -> UpdateStudentView.show());
        addButton.setOnAction(e -> AddStudentView.show(this, studentsViewModel));
        deleteButton.setOnAction(e -> DeleteStudentView.show(studentsViewModel));
        bottomSection.getChildren().addAll(addButton, updateButton, deleteButton);

        BorderPaneHelper.setAllSections(contentArea, null, null, null, bottomSection, scrollPane);
    }

    public void renderNewStudent(String[] updatedStudent) {
        GridPane studentGrid = new GridPane();
        studentGrid.setStyle(
                "-fx-border-color: #888; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-background-color: #f9f9f9; -fx-margin: 16");
        studentGrid.setAlignment(Pos.TOP_LEFT);
        studentGrid.setHgap(10);
        studentGrid.setVgap(5);
        for (int i = 0; i < labels.length; i++) {
            javafx.scene.control.Label label = new javafx.scene.control.Label(labels[i] + ": ");
            label.setMinWidth(90);
            javafx.scene.control.Label value = new javafx.scene.control.Label(updatedStudent[i]);
            studentGrid.add(label, 0, i);
            studentGrid.add(value, 1, i);
        }

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i)[0].equals(updatedStudent[0])) {
                studentsContainer.getChildren().set(i, studentGrid);
                return;
            }
        }
        System.out.println("Updated student does not exist in the Observable list");
        // do nothing
    }
}
