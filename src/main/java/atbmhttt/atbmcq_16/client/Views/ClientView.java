package atbmhttt.atbmcq_16.client.Views;

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
        Button dummyButton2 = new Button("Dummy 2");
        Button dummyButton3 = new Button("Dummy 3");
        Button logoutButton = new Button("Log out");

        navigationPanel.add(studentsButton, 0, 1);
        navigationPanel.add(dummyButton2, 0, 2);
        navigationPanel.add(dummyButton3, 0, 3);

        // Add a spacer pane to fill the space between the last button and the bottom
        Pane spacer = new Pane();
        navigationPanel.add(spacer, 0, 4);
        GridPane.setVgrow(spacer, Priority.ALWAYS);

        navigationPanel.add(logoutButton, 0, 5);

        BorderPane contentArea = new BorderPane();
        contentArea.setCenter(new Text("Welcome CLIENT"));

        // set up the buttons
        setUpDisplayStudents(studentsButton, contentArea);

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
}
