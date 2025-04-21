package atbmhttt.atbmcq_16.admin.Views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminView extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Left navigation panel
        VBox navigationPanel = new VBox(10);
        navigationPanel.setPadding(new Insets(10));

        Button usersButton = new Button("Users");
        Button rolesButton = new Button("Roles");
        Button privilegesButton = new Button("Privileges");
        Button logoutButton = new Button("Log out");

        navigationPanel.getChildren().addAll(usersButton, rolesButton, privilegesButton, logoutButton);

        // Right content area
        Label contentLabel = new Label("Users");

        // Event handlers for navigation buttons
        usersButton.setOnAction(e -> contentLabel.setText("Users"));
        rolesButton.setOnAction(e -> contentLabel.setText("Roles"));
        privilegesButton.setOnAction(e -> contentLabel.setText("Privileges"));
        logoutButton.setOnAction(e -> showLogoutConfirmation());

        BorderPane contentArea = new BorderPane();
        contentArea.setCenter(contentLabel);

        // SplitPane to divide navigation and content
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navigationPanel, contentArea);
        splitPane.setDividerPositions(0.25);

        // Scene setup
        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setTitle("Admin Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLogoutConfirmation() {
        // Placeholder for logout confirmation dialog
        System.out.println("Logout confirmation dialog");
    }

    public static void main(String[] args) {
        launch(args);
    }
}