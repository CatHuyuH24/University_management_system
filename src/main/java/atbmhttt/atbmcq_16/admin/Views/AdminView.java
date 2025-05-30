package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.Router;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AdminView extends Application {

    private PrivilegesView privilegesView = new PrivilegesView();// doesn't need dynamic fetching -> no need repeating
                                                                 // initialization

    private Text text = new Text("Welcome DATABASE ADMINISTRATOR");

    @Override
    public void start(Stage primaryStage) {
        // Left navigation panel using GridPane
        GridPane navigationPanel = new GridPane();
        navigationPanel.setPadding(new Insets(10));
        navigationPanel.setVgap(10);

        Button usersButton = new Button("USERS");
        Button rolesButton = new Button("ROLES");
        Button privilegesButton = new Button("PRIVILEGES");
        Button logoutButton = new Button("LOG OUT");
        // Button grantPrivilegeButton = new Button("Grant Privilege");

        navigationPanel.add(usersButton, 0, 1);
        navigationPanel.add(rolesButton, 0, 2);
        navigationPanel.add(privilegesButton, 0, 3);
        // navigationPanel.add(grantPrivilegeButton, 0, 4);
        // setUpGrantPrivilegeButton(grantPrivilegeButton);
        // Add a spacer pane to fill the space between the privileges button and the
        // logout button
        Pane spacer = new Pane();
        navigationPanel.add(spacer, 0, 5);
        GridPane.setVgrow(spacer, Priority.ALWAYS);

        navigationPanel.add(logoutButton, 0, 6);

        BorderPane contentArea = new BorderPane();
        // Right content area
        // Event handlers for navigation buttons
        setUpDisplayUsersViaButton(usersButton, contentArea);
        setUpDisplayPriviledgesViaButton(privilegesButton, contentArea);
        setUpDisplayRolesButton(rolesButton, contentArea);
        setUpLogoutButton(logoutButton);

        contentArea.setCenter(text);

        // SplitPane to divide navigation and content
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navigationPanel, contentArea);
        splitPane.setDividerPositions(0.25);

        // Scene setup
        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setUpDisplayRolesButton(final Button rolesButton, final BorderPane contentArea) {
        rolesButton.setOnAction(e -> {
            RolesView rolesView = new RolesView();
            rolesView.displayRoles(contentArea);
        });
    }

    private void setUpDisplayUsersViaButton(final Button usersButton, final BorderPane contentArea) {
        usersButton.setOnAction(e -> {
            UsersView usersView = new UsersView();
            usersView.displayUsers(contentArea);
        });
    }

    private void setUpDisplayPriviledgesViaButton(final Button privilegesButton, final BorderPane contentArea) {
        privilegesButton.setOnAction(e -> {
            privilegesView.display(contentArea);
        });
    }

    private void setUpLogoutButton(final Button logoutButton) {
        logoutButton.setOnAction(e -> {
            // Show confirmation alert before logging out
            ButtonType response = AlertDialog.showAndGetResultConfirmationAlert("LOGGING OUT",
                    null,
                    "Are you sure you want to log out?",
                    null, 400, 200);
            if (ButtonType.OK == response) {
                Router.navigateToLogin();
            }
        });
    }

}