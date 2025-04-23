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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class AdminView extends Application {

    private AdminViewModel adminViewModel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

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

        Button usersButton = new Button("Users");
        Button rolesButton = new Button("Roles");
        Button privilegesButton = new Button("Privileges");
        Button logoutButton = new Button("Log out");

        // Add buttons to the grid
        navigationPanel.add(usersButton, 0, 0);
        navigationPanel.add(rolesButton, 0, 1);
        navigationPanel.add(privilegesButton, 0, 2);

        // Add a spacer pane to fill the space between the privileges button and the
        // logout button
        Pane spacer = new Pane();
        navigationPanel.add(spacer, 0, 3);
        GridPane.setVgrow(spacer, Priority.ALWAYS);

        navigationPanel.add(logoutButton, 0, 4);

        BorderPane contentArea = new BorderPane();
        // Right content area
        // Event handlers for navigation buttons
        setUpDisplayUsersViaButton(usersButton, contentArea);
        setUpDisplayRolesViaButton(rolesButton, contentArea);
        setUpDisplayPriviledgesViaButton(privilegesButton, contentArea);
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
                        handleEditUser(username);
                    });

                    deleteButton.setOnAction(event -> {
                        String username = getTableView().getItems().get(getIndex())[0];
                        handleDeleteUser(username, getTableView(), getIndex());
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
            List<String[]> roles = adminViewModel.getPdbRoles();

            TableView<String[]> tableView = new TableView<>();

            TableColumn<String[], String> roleNameColumn = new TableColumn<>("Role Name");
            roleNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

            TableColumn<String[], String> roleIdColumn = new TableColumn<>("Role ID");
            roleIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[1]));

            TableColumn<String[], Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(col -> new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox actionButtons = new HBox(5, editButton, deleteButton);

                {
                    editButton.setOnAction(event -> {
                        String roleName = getTableView().getItems().get(getIndex())[0];
                        handleEditRole(roleName);
                    });

                    deleteButton.setOnAction(event -> {
                        String roleName = getTableView().getItems().get(getIndex())[0];
                        handleDeleteRole(roleName, getTableView(), getIndex());
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
            roleNameColumn.setStyle("-fx-alignment: CENTER;");
            roleIdColumn.setStyle("-fx-alignment: CENTER;");

            tableView.getColumns().addAll(roleNameColumn, roleIdColumn, actionsColumn);
            tableView.setItems(FXCollections.observableArrayList(roles));

            contentArea.setCenter(tableView);
        });
    }

    private void setUpDisplayPriviledgesViaButton(final Button privilegesButton, final BorderPane contentArea) {
        privilegesButton.setOnAction(e -> {
            Stage privilegesStage = new Stage();
            privilegesStage.setTitle("Privileges Management");

            // Set the icon in the title bar
            privilegesStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/university_icon.png")));

            privilegesStage.setOnCloseRequest(event -> privilegesStage.close());

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);

            ImageView imageView = new ImageView(
                    new Image(getClass().getResourceAsStream("/images/university_icon.png")));
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            Label titleLabel = new Label("Privileges Management");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            header.getChildren().addAll(imageView, titleLabel);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.getChildren().add(header); // Add the header with the logo and title
            layout.setAlignment(Pos.CENTER);

            Button userPrivilegesButton = new Button("User Privileges");
            Button rolePrivilegesButton = new Button("Role Privileges");

            userPrivilegesButton.setOnAction(event -> {
                privilegesStage.close();
                handleUserPrivileges();
            });

            rolePrivilegesButton.setOnAction(event -> {
                privilegesStage.close();
                handleRolePrivileges();
            });

            layout.getChildren().addAll(userPrivilegesButton, rolePrivilegesButton);

            Scene scene = new Scene(layout, 300, 200);
            privilegesStage.setScene(scene);
            privilegesStage.show();
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

    private void handleUserPrivileges() {
        Stage userPrivilegesStage = new Stage();
        userPrivilegesStage.setTitle("User Privileges");

        TableView<String[]> tableView = new TableView<>();

        TableColumn<String[], Void> actionColumn = new TableColumn<>("Edit Privileges");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button("P");

            {
                actionButton.setOnAction(event -> {
                    String username = getTableView().getItems().get(getIndex())[0];
                    System.out.println("Button P clicked for user: " + username);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        });

        TableColumn<String[], String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

        tableView.getColumns().addAll(actionColumn, userColumn);
        tableView.setItems(FXCollections.observableArrayList(adminViewModel.getUsersWithDetails()));

        VBox layout = new VBox(10, tableView);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 450);
        userPrivilegesStage.setScene(scene);
        userPrivilegesStage.show();
    }

    private void handleUserPrivilegesDetails(String username) {
        // Logic to handle user privileges details
        System.out.println("Privileges for user: " + username);
    }

    private void handleRolePrivileges() {
        Stage rolePrivilegesStage = new Stage();
        rolePrivilegesStage.setTitle("Role Privileges");

        TableView<String[]> tableView = new TableView<>();

        TableColumn<String[], Void> actionColumn = new TableColumn<>("Edit Privileges");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button("P");

            {
                actionButton.setOnAction(event -> {
                    String roleName = getTableView().getItems().get(getIndex())[0];
                    System.out.println("Button P clicked for role: " + roleName);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        });

        TableColumn<String[], String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

        tableView.getColumns().addAll(actionColumn, roleColumn);
        tableView.setItems(FXCollections.observableArrayList(adminViewModel.getPdbRoles()));

        VBox layout = new VBox(10, tableView);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 450);
        rolePrivilegesStage.setScene(scene);
        rolePrivilegesStage.show();
    }

    private void handleEditUser(String username) {
        // Create a new stage for changing the password
        Stage changePasswordStage = new Stage();
        changePasswordStage.setTitle("Change Password for " + username);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label instructionLabel = new Label("Enter new password for " + username + ":");
        PasswordField newPasswordField = new PasswordField();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(submitEvent -> {
            String newPassword = newPasswordField.getText();
            if (newPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Password cannot be empty!", ButtonType.OK);
                alert.showAndWait();
            } else {
                adminViewModel.changeUserPassword(username, newPassword);
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Password changed successfully!",
                        ButtonType.OK);
                successAlert.showAndWait();
                changePasswordStage.close();
            }
        });

        layout.getChildren().addAll(instructionLabel, newPasswordField, submitButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        changePasswordStage.setScene(scene);
        changePasswordStage.show();
    }

    private void handleDeleteUser(String username, TableView<String[]> tableView, int index) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete user " + username + "?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                adminViewModel.deleteUser(username);
                tableView.getItems().remove(index); // Update the UI to reflect the deletion
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "User deleted successfully!",
                        ButtonType.OK);
                successAlert.showAndWait();
            }
        });
    }

    private void handleEditRole(String roleName) {
        // Logic to handle editing a role
        System.out.println("Edit role: " + roleName);
    }

    private void handleDeleteRole(String roleName, TableView<String[]> tableView, int index) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete role " + roleName + "?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                adminViewModel.deleteRole(roleName);
                tableView.getItems().remove(index); // Update the UI to reflect the deletion
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Role deleted successfully!",
                        ButtonType.OK);
                successAlert.showAndWait();
            }
        });
    }
}