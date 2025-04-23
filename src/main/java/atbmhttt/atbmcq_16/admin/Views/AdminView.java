package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.Router;
import atbmhttt.atbmcq_16.admin.ViewModels.AdminViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class AdminView extends Application {

    private AdminViewModel adminViewModel;

    public AdminView(String username, String password) {
        adminViewModel = new AdminViewModel(username, password);
    }

    private Text text = new Text("Welcome administrator");

    @Override
    public void start(Stage primaryStage) {
        // Left navigation panel using GridPane
        GridPane navigationPanel = new GridPane();
        navigationPanel.setPadding(new Insets(10));
        navigationPanel.setVgap(10);

        Button addUserButton = new Button("Add User");
        addUserButton.setStyle(
                "-fx-background-radius: 15; -fx-padding: 5 10 5 10; -fx-background-color: #0078D7; -fx-text-fill: white;");
        navigationPanel.add(addUserButton, 0, 0); // Add the button to the top of the navigation panel

        Button usersButton = new Button("Users");
        Button rolesButton = new Button("Roles");
        Button privilegesButton = new Button("Privileges");
        Button logoutButton = new Button("Log out");

        // Add buttons to the grid
        navigationPanel.add(usersButton, 0, 1);
        navigationPanel.add(rolesButton, 0, 2);
        navigationPanel.add(privilegesButton, 0, 3);

        // Add a spacer pane to fill the space between the privileges button and the
        // logout button
        Pane spacer = new Pane();
        navigationPanel.add(spacer, 0, 4);
        GridPane.setVgrow(spacer, Priority.ALWAYS);

        navigationPanel.add(logoutButton, 0, 5);

        BorderPane contentArea = new BorderPane();
        // Right content area
        // Event handlers for navigation buttons
        setUpDisplayUsersViaButton(usersButton, contentArea);
        setUpDisplayRolesViaButton(rolesButton, contentArea);
        setUpDisplayPriviledgesViaButton(privilegesButton, contentArea);
        setUpAddUserButton(addUserButton, contentArea); // Set up event handler for the button
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

    private void setUpDisplayUsersViaButton(final Button usersButton, final BorderPane contentArea) {
        usersButton.setOnAction(e -> {
            List<String[]> users = adminViewModel.getUsersWithDetails(); // Assuming this returns a list of [username,
                                                                         // created]

            TableView<String[]> tableView = new TableView<>();

            TableColumn<String[], String> usernameColumn = new TableColumn<>("Username");
            usernameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

            TableColumn<String[], String> createdDateColumn = new TableColumn<>("Created date");
            createdDateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[1]));

            TableColumn<String[], Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(col -> new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox actionButtons = new HBox(5, editButton, deleteButton);

                {

                    editButton.setOnAction(event -> {
                        String username = getTableView().getItems().get(getIndex())[0];
                        System.out.println("Edit action for: " + username);
                    });

                    deleteButton.setOnAction(event -> {
                        String username = getTableView().getItems().get(getIndex())[0];
                        System.out.println("Delete action for: " + username);
                    });

                    actionButtons.setAlignment(Pos.CENTER);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionButtons);
                    }
                }
            });

            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Ensure columns fill the table width
            usernameColumn.setStyle("-fx-alignment: CENTER;");
            createdDateColumn.setStyle("-fx-alignment: CENTER;");

            tableView.getColumns().add(usernameColumn);
            tableView.getColumns().add(createdDateColumn);
            tableView.getColumns().add(actionsColumn);
            tableView.setItems(FXCollections.observableArrayList(users));

            contentArea.setCenter(tableView);
        });
    }

    private void setUpDisplayRolesViaButton(final Button rolesButton, final BorderPane contentArea) {
        rolesButton.setOnAction(e -> {
            List<String[]> roles = adminViewModel.getPdbRoles(); // Assuming this returns a list of [roleName,
                                                                 // description]

            TableView<String[]> tableView = new TableView<>();

            TableColumn<String[], String> roleNameColumn = new TableColumn<>("Role Name");
            roleNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

            TableColumn<String[], String> descriptionColumn = new TableColumn<>("Role ID");
            descriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[1]));

            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Ensure columns fill the table width
            roleNameColumn.setStyle("-fx-alignment: CENTER;");
            descriptionColumn.setStyle("-fx-alignment: CENTER;");

            tableView.getColumns().add(roleNameColumn);
            tableView.getColumns().add(descriptionColumn);
            tableView.setItems(FXCollections.observableArrayList(roles));

            contentArea.setCenter(tableView);
        });
    }

    private void setUpDisplayPriviledgesViaButton(final Button privilegesButton, final BorderPane contentArea) {
        privilegesButton.setOnAction(e -> {
            text.setText("Priviledges");
            contentArea.setCenter(new Label("Priviledges"));
        });
    }

    private void setUpAddUserButton(final Button addUserButton, final BorderPane contentArea) {
        addUserButton.setOnAction(e -> {
            // Placeholder for adding a new user
            contentArea.setCenter(new Label("Add User Form"));
        });
    }

    private void setUpLogoutButton(final Button logoutButton) {
        logoutButton.setOnAction(e -> {
            // Show confirmation alert before logging out
            ButtonType response = AlertDialog.showAndGetResultConfirmationAlert("LOGGING OUT",
                    null,
                    "Are you sure you want to log out?",
                    null);
            if (ButtonType.OK == response) {
                Router.navigateToLogin();
            }
        });
    }
}