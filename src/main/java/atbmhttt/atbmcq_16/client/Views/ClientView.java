package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.DatabaseConnection;
import atbmhttt.atbmcq_16.client.Views.Students.StudentsView;
import java.sql.Connection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientView extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Left navigation panel using GridPane
        GridPane navigationPanel = new GridPane();
        navigationPanel.setPadding(new Insets(10));
        navigationPanel.setVgap(10);

        Button studentsButton = new Button("STUDENTS");
        Button employeesButton = new Button("EMPLOYEES");
        Button coursesButton = new Button("COURSES");
        Button enrollmentButton = new Button("ENROLLMENT & GRADES");
        Button addNotificationButton = new Button("ADD NOTIFICATION");
        Button viewNotificationsButton = new Button("VIEW NOTIFICATIONS");
        Button assignLabelButton = new Button("ASSIGN LABEL");
        Button logoutButton = new Button("LOG OUT");

        navigationPanel.add(studentsButton, 0, 1);
        navigationPanel.add(employeesButton, 0, 2);
        navigationPanel.add(coursesButton, 0, 3);
        navigationPanel.add(enrollmentButton, 0, 4);
        navigationPanel.add(addNotificationButton, 0, 5);
        navigationPanel.add(viewNotificationsButton, 0, 6);
        navigationPanel.add(assignLabelButton, 0, 7);

        // Add a spacer pane to fill the space between the last button and the bottom
        Pane spacer = new Pane();
        navigationPanel.add(spacer, 0, 8);
        GridPane.setVgrow(spacer, Priority.ALWAYS);

        // Move logout button to the bottom
        navigationPanel.add(logoutButton, 0, 9);

        BorderPane contentArea = new BorderPane();
        contentArea.setCenter(new Text("Welcome " + DatabaseConnection.getUsername()));

        // set up the buttons
        setUpDisplayStudents(studentsButton, contentArea);
        setUpDisplayEmployees(employeesButton, contentArea);
        setUpDisplaySubjects(coursesButton, contentArea);
        setUpDisplayRegistrationDetails(enrollmentButton, contentArea);

        // Gán sự kiện cho nút VIEW NOTIFICATIONS
        viewNotificationsButton.setOnAction(e -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                atbmhttt.atbmcq_16.client.Views.NotificationsView.showNotifications(conn, contentArea);
            } catch (Exception ex) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể tải thông báo");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        // Gán sự kiện cho nút ADD NOTIFICATION để mở giao diện thêm thông báo
        addNotificationButton.setOnAction(e -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                atbmhttt.atbmcq_16.client.Views.AddNotificationView.show(conn);
            } catch (Exception ex) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể mở giao diện thêm thông báo");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        // Gán sự kiện cho nút ASSIGN LABEL để mở giao diện gán nhãn cho user
        assignLabelButton.setOnAction(e -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                atbmhttt.atbmcq_16.client.Views.AssignLabelView.show(conn);
            } catch (Exception ex) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể mở giao diện gán nhãn");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        // Log out button functionality (similar to AdminView)
        logoutButton.setOnAction(e -> {
            // Show confirmation alert before logging out
            javafx.scene.control.ButtonType response = atbmhttt.atbmcq_16.dialogs.AlertDialog
                    .showAndGetResultConfirmationAlert(
                            "LOGGING OUT",
                            null,
                            "Are you sure you want to log out?",
                            null, 400, 200);
            if (javafx.scene.control.ButtonType.OK == response) {
                atbmhttt.atbmcq_16.Router.navigateToLogin();
            }
        });

        // SplitPane to divide navigation and content
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navigationPanel, contentArea);
        splitPane.setDividerPositions(0.25);

        // Scene setup
        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setTitle("Client Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setUpDisplayStudents(final Button studentsButton, final BorderPane contentArea) {
        studentsButton.setOnAction(e -> {
            StudentsView view = new StudentsView();
            view.displayStudents(contentArea);
        });
    }

    private void setUpDisplayRegistrationDetails(final Button enrollmentButton, final BorderPane contentArea) {
        enrollmentButton.setOnAction(e -> {
            atbmhttt.atbmcq_16.client.Views.Enrollment.EnrollmentAndGradesView view = new atbmhttt.atbmcq_16.client.Views.Enrollment.EnrollmentAndGradesView();
            view.displayEnrollments(contentArea);
        });
    }

    private void setUpDisplayEmployees(final Button employeesButton, final BorderPane contentArea) {
        employeesButton.setOnAction(e -> {
            String username = DatabaseConnection.getUsername();
            atbmhttt.atbmcq_16.client.Views.Employees.EmployeesView view = new atbmhttt.atbmcq_16.client.Views.Employees.EmployeesView();
            view.displayEmployees(contentArea, username);
        });
    }

    private void setUpDisplaySubjects(final Button coursesButton, final BorderPane contentArea) {
        coursesButton.setOnAction(e -> {
            atbmhttt.atbmcq_16.client.Views.Subjects.SubjectsView view = new atbmhttt.atbmcq_16.client.Views.Subjects.SubjectsView();
            view.displaySubjects(contentArea);
        });
    }
}
