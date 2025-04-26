package atbmhttt.atbmcq_16.admin.Views;

import java.sql.SQLException;
import java.util.List;

import atbmhttt.atbmcq_16.PrivilegeManager;
import atbmhttt.atbmcq_16.PrivilegeViewer;
import atbmhttt.atbmcq_16.RevokePrivilegeView;
import atbmhttt.atbmcq_16.Router;
import atbmhttt.atbmcq_16.ViewPrivilegesView;
import atbmhttt.atbmcq_16.admin.ViewModels.AdminViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AdminView extends Application {

    private TableView<String[]> rolesTableView;
    private TableView<String[]> usersTableView;
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

        Button usersButton = new Button("Users");
        Button rolesButton = new Button("Roles");
        Button privilegesButton = new Button("Privileges");
        Button logoutButton = new Button("Log out");
        Button revokePrivilegeButton = new Button("Revoke Privilege");
        Button viewPrivilegesButton = new Button("View Privileges");

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
        navigationPanel.add(revokePrivilegeButton, 0, 6);
        navigationPanel.add(viewPrivilegesButton, 0, 7);

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

        // Set up event handlers for the buttons
        revokePrivilegeButton.setOnAction(e -> {
            RevokePrivilegeView revokePrivilegeView = new RevokePrivilegeView(new PrivilegeManager());
            revokePrivilegeView.start(new Stage());
        });

        viewPrivilegesButton.setOnAction(e -> {
            ViewPrivilegesView viewPrivilegesView = new ViewPrivilegesView(new PrivilegeViewer());
            viewPrivilegesView.start(new Stage());
        });
    }

    private void setUpDisplayUsersViaButton(final Button usersButton, final BorderPane contentArea) {
        usersButton.setOnAction(e -> {
            List<String[]> users = adminViewModel.getUsersWithDetails(); // Assuming this returns a list of [username,
                                                                         // created]
            usersTableView = new TableView<>();

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

            usersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Ensure columns fill the table
                                                                                       // width
            usernameColumn.setStyle("-fx-alignment: CENTER;");
            createdDateColumn.setStyle("-fx-alignment: CENTER;");

            usersTableView.getColumns().add(usernameColumn);
            usersTableView.getColumns().add(createdDateColumn);
            usersTableView.getColumns().add(actionsColumn);
            usersTableView.setItems(FXCollections.observableArrayList(users));

            Button addUserButton = new Button("Add User");
            setUpAddUserButton(addUserButton, contentArea); // set up event-listener

            VBox vBox = new VBox(10, addUserButton, new Pane());

            BorderPaneHelper.setAllSections(contentArea,
                    null, vBox,
                    null, null, usersTableView);
        });
    }

    private void setUpDisplayRolesViaButton(final Button rolesButton, final BorderPane contentArea) {
        rolesButton.setOnAction(e -> {
            List<String[]> roles = adminViewModel.getPdbRoles();

            rolesTableView = new TableView<>();

            TableColumn<String[], String> roleNameColumn = new TableColumn<>("Role Name");
            roleNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

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

            rolesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Ensure columns fill the table
                                                                                       // width
            roleNameColumn.setStyle("-fx-alignment: CENTER;");
            rolesTableView.getColumns().add(roleNameColumn);
            rolesTableView.getColumns().add(actionsColumn);

            rolesTableView.setItems(FXCollections.observableArrayList(roles));

            BorderPaneHelper.setAllSections(contentArea,
                    null, null, null, null,
                    rolesTableView);
        });
    }

    private void setUpDisplayPriviledgesViaButton(final Button privilegesButton, final BorderPane contentArea) {
        privilegesButton.setOnAction(e -> {
            Stage privilegesStage = new Stage();
            privilegesStage.setTitle("Privileges Management");

            // Set the icon in the title bar
            privilegesStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

            privilegesStage.setOnCloseRequest(event -> privilegesStage.close());

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);

            ImageView imageView = new ImageView(
                    new Image(getClass().getResourceAsStream("/images/app_icon.png")));
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

    private void setUpAddUserButton(final Button addUserButton, final BorderPane contentArea) {
        addUserButton.setOnAction(e -> {
            // Create a new stage for adding a user
            Stage addUserStage = new Stage();
            addUserStage.setTitle("ADDING NEW USER");

            // Set the icon in the title bar
            addUserStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));

            Label usernameLabel = new Label("Enter username:");
            TextField usernameField = new TextField();

            Label passwordLabel = new Label("Enter password:");
            PasswordField passwordField = new PasswordField();

            Button submitButton = new Button("Submit");

            submitButton.setOnAction(submitEvent -> {
                String username = usernameField.getText();
                String password = passwordField.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.showErrorAlert(
                            "EMPTY USERNAME OR PASSWORD",
                            null,
                            "Username and/or password can not be empty\nPlease try again",
                            null);
                } else {
                    try {
                        adminViewModel.addUser(username, password);
                        String currentDateTime = java.time.LocalDateTime
                                .now().format(
                                        java.time.format.DateTimeFormatter
                                                .ofPattern("yyyy-MM-dd HH:mm:ss")); // Get the current
                                                                                    // date and time
                                                                                    // as a string
                        usersTableView.getItems().add(new String[] { username, currentDateTime }); // add the newly
                                                                                                   // created user
                                                                                                   // on UI
                        AlertDialog.showInformationAlert("USER ADDED SUCCESSFULLY",
                                null,
                                "User " + username + "has been added successfully",
                                null);

                        addUserStage.close();
                    } catch (Exception ex) {
                        AlertDialog.showErrorAlert("FAILED TO ADD A NEW USER",
                                null,
                                "User " + username + " couldn't be created.\nPlease try again later", null);
                    }

                }
            });

            layout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, submitButton);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout, 300, 200);
            addUserStage.setScene(scene);
            addUserStage.show();
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

        tableView.getColumns().add(actionColumn);
        tableView.getColumns().add(userColumn);

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

        tableView.getColumns().add(actionColumn);
        tableView.getColumns().add(roleColumn);

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
        changePasswordStage.setTitle("CHANGING PASSWORD");

        // Set the icon in the title bar
        changePasswordStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label instructionLabel = new Label("Enter new password for " + username + ":");
        PasswordField newPasswordField = new PasswordField();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(submitEvent -> {
            String newPassword = newPasswordField.getText();
            if (newPassword.isEmpty()) {
                AlertDialog.showErrorAlert("EMPTY NEW PASSWORD", null,
                        "The new password CANNOT be empty!\nPlease provide a new password", null);
            } else {
                adminViewModel.changeUserPassword(username, newPassword);
                AlertDialog.showInformationAlert("UPDATED PASSWORD SUCCESSFULLY",
                        null,
                        "User " + username + " has had their password updated!",
                        null);
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
        ButtonType response = AlertDialog.showAndGetResultConfirmationAlert("DELETING USER",
                null,
                "Are you sure you want to delete user " + username,
                null);
        if (ButtonType.OK == response) {
            adminViewModel.deleteUser(username);
            tableView.getItems().remove(index); // Update the UI to reflect the deletion
            AlertDialog.showInformationAlert("DELETED USER " + username,
                    null,
                    "User " + username + " has been deleted successfully!",
                    null);
        }
    }

    private void handleEditRole(String roleName) {
        // Logic to handle editing a role
        System.out.println("Edit role: " + roleName);
    }

    private void handleDeleteRole(String roleName, TableView<String[]> tableView, int index) {
        ButtonType response = AlertDialog.showAndGetResultConfirmationAlert("DELETING ROLE",
                null,
                "Are you sure you want to delete role " + roleName,
                null);
        if (ButtonType.OK == response) {
            adminViewModel.deleteUser(roleName);
            tableView.getItems().remove(index); // Update the UI to reflect the deletion
            AlertDialog.showInformationAlert("DELETED ROLE " + roleName,
                    null,
                    "Role " + roleName + " has been deleted successfully!",
                    null);
        }
    }
}