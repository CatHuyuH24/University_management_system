package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.DatabaseConnection;
import atbmhttt.atbmcq_16.client.Views.Students.StudentsView;
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

        Button studentsButton = new Button("Students");
        Button employeesButton = new Button("Employees");
        Button coursesButton = new Button("Courses");
        Button enrollmentButton = new Button("Enrollment & Grades");
        Button broadcastNotificationButton = new Button("Broadcast Notification");
        Button viewNotificationsButton = new Button("View Notifications");
        Button logoutButton = new Button("Log out");

        navigationPanel.add(studentsButton, 0, 1);
        navigationPanel.add(employeesButton, 0, 2);
        navigationPanel.add(coursesButton, 0, 3);
        navigationPanel.add(enrollmentButton, 0, 4);
        navigationPanel.add(broadcastNotificationButton, 0, 5);
        navigationPanel.add(viewNotificationsButton, 0, 6);

        // Add a spacer pane to fill the space between the last button and the bottom
        Pane spacer = new Pane();
        navigationPanel.add(spacer, 0, 7);
        GridPane.setVgrow(spacer, Priority.ALWAYS);

        // Move logout button to the bottom
        navigationPanel.add(logoutButton, 0, 8);

        BorderPane contentArea = new BorderPane();
        contentArea.setCenter(new Text("Welcome " + DatabaseConnection.getUsername()));

        // set up the buttons
        setUpDisplayStudents(studentsButton, contentArea);
        setUpDisplayRegistrationDetails(enrollmentButton, contentArea);

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
            atbmhttt.atbmcq_16.client.Views.Enrollment.EnrollmentView view = new atbmhttt.atbmcq_16.client.Views.Enrollment.EnrollmentView();
            view.displayEnrollments(contentArea);
        });
    }
}
