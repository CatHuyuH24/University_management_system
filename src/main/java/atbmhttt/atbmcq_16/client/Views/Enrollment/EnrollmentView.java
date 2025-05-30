package atbmhttt.atbmcq_16.client.Views.Enrollment;

import atbmhttt.atbmcq_16.client.ViewModels.EnrollmentViewModel;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class EnrollmentView {
    private final EnrollmentViewModel enrollmentViewModel;
    ScrollPane container;
    private ObservableList<String[]> enrollments;
    private final String[] columnNames = { "MASV", "MAMM", "DIEMTH", "DIEMQT", "DIEMCK", "DIEMTK" };

    public EnrollmentView() {
        this.enrollmentViewModel = new EnrollmentViewModel();
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
    }
}
