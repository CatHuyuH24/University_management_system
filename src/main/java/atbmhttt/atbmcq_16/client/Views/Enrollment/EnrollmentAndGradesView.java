package atbmhttt.atbmcq_16.client.Views.Enrollment;

import atbmhttt.atbmcq_16.client.ViewModels.EnrollmentAndGradesViewModel;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class EnrollmentAndGradesView {
    private final EnrollmentAndGradesViewModel enrollmentViewModel;
    ScrollPane container;
    private ObservableList<String[]> enrollments;
    private final String[] columnNames = { "MASV", "MAMM", "DIEMTH", "DIEMQT", "DIEMCK", "DIEMTK" };

    public EnrollmentAndGradesView() {
        this.enrollmentViewModel = new EnrollmentAndGradesViewModel();
    }

    public void displayEnrollments(BorderPane contentArea) {
        enrollments = enrollmentViewModel.getEnrollments();
        if (enrollments.isEmpty()) {
            Label emptyLabel = new Label("No enrollment information to be displayed");
            emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #888;");
            container = new ScrollPane(emptyLabel);

        } else {
            TableView<String[]> tableView = new TableView<>();
            for (int i = 0; i < columnNames.length; i++) {
                final int colIndex = i;
                TableColumn<String[], String> column = new TableColumn<>(columnNames[i]);
                column.setCellValueFactory(
                        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[colIndex]));
                tableView.getColumns().add(column);
            }
            tableView.setItems(enrollments);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableView.setPrefHeight(400); // Set a preferred height for the table
            container = new ScrollPane(tableView);

            container.setFitToWidth(true);
            container.setFitToHeight(true);
        }

        BorderPaneHelper.setAllSections(contentArea,
                null, null, null, null, container);

        // --- Sticky box at the bottom ---
        javafx.scene.layout.HBox bottomSection = new javafx.scene.layout.HBox(12); // Add spacing between buttons
        bottomSection.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        bottomSection.setStyle(
                "-fx-background-color: #f0f0f0; -fx-padding: 12 24 12 24; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");
        javafx.scene.control.Button updateButton = new javafx.scene.control.Button("Update information");
        javafx.scene.control.Button addButton = new javafx.scene.control.Button("Add enrollment");
        javafx.scene.control.Button unenrollButton = new javafx.scene.control.Button("Unenroll");

        updateButton.setOnAction(e -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Update Enrollment");
            alert.setHeaderText(null);
            alert.setContentText("Update enrollment confirmation.");
            alert.showAndWait();
        });
        addButton.setOnAction(e -> {
            AddEnrollmentView.show(enrollmentViewModel);
        });
        unenrollButton.setOnAction(e -> {
            DeleteEnrollmentView.show(enrollmentViewModel);
        });
        bottomSection.getChildren().addAll(addButton, updateButton, unenrollButton);

        BorderPaneHelper.setAllSections(contentArea, null, null, null, bottomSection, container);
    }
}
