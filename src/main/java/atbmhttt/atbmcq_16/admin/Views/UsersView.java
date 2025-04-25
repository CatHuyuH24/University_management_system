package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.admin.ViewModels.UsersViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UsersView {

    private final UsersViewModel usersViewModel;
    private TableView<String[]> usersTableView;

    public UsersView() {
        this.usersViewModel = new UsersViewModel();
    }

    public void displayUsers(BorderPane contentArea) {
        ObservableList<String[]> users = usersViewModel.getUsers();

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
                    confirmAndDeleteUser(username);
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

        usersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usernameColumn.setStyle("-fx-alignment: CENTER;");
        createdDateColumn.setStyle("-fx-alignment: CENTER;");

        usersTableView.getColumns().add(usernameColumn);
        usersTableView.getColumns().add(createdDateColumn);
        usersTableView.getColumns().add(actionsColumn);
        usersTableView.setItems(users);

        users.addListener((ListChangeListener<String[]>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    usersTableView.refresh();
                }
            }
        });

        Button addUserButton = new Button("Add User");
        setUpAddUserButton(addUserButton);

        VBox vBox = new VBox(10, addUserButton, new VBox());

        BorderPaneHelper.setAllSections(contentArea,
                null, vBox,
                null, null, usersTableView);
    }

    private void setUpAddUserButton(final Button addUserButton) {
        addUserButton.setOnAction(e -> {
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
                            "Username and/or password cannot be empty. Please try again.",
                            null);
                } else {
                    handleAddUser(username, password);
                    addUserStage.close();
                }
            });

            layout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, submitButton);
            layout.setAlignment(Pos.CENTER);

            addUserStage.setScene(new javafx.scene.Scene(layout, 300, 200));
            addUserStage.show();
        });
    }

    private void handleAddUser(String username, String password) {
        try {
            usersViewModel.addUser(username, password);
            AlertDialog.showInformationAlert("USER ADDED SUCCESSFULLY",
                    null,
                    "User " + username + " has been added successfully.",
                    null);
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 20001) {

                AlertDialog.showErrorAlert("USER ALREADY EXISTED",
                        null,
                        "User already " + username
                                + "!\nPlease delete that user first, or choose a different username and try again.",
                        null);
            } else {

                AlertDialog.showErrorAlert("FAILED TO ADD USER",
                        null,
                        "An error occurred while adding user " + username + ". Please try again later.",
                        null);
            }
        }
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
                handleChangeUserPassword(username, newPassword);
                changePasswordStage.close();
            }
        });

        layout.getChildren().addAll(instructionLabel, newPasswordField, submitButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        changePasswordStage.setScene(scene);
        changePasswordStage.show();
    }

    private void handleChangeUserPassword(String username, String newPassword) {
        try {
            usersViewModel.updateUserPassword(username, newPassword);
            AlertDialog.showInformationAlert("PASSWORD CHANGED",
                    null,
                    "Password for user " + username + " has been changed successfully!",
                    null);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertDialog.showErrorAlert("FAILED TO CHANGE PASSWORD",
                    null,
                    "An error occurred while changing the password for user " + username + ". Please try again later.",
                    null);
        }
    }

    public boolean confirmAndDeleteUser(String username) {
        ButtonType response = AlertDialog.showAndGetResultConfirmationAlert(
                "DELETE USER " + username, null,
                "Are you sure you want to delete user " + username + "?",
                null);

        if (ButtonType.OK == response) {
            try {
                usersViewModel.deleteUser(username);
                AlertDialog.showInformationAlert("USER DELETED",
                        null,
                        "User " + username + " has been deleted successfully.",
                        null);
            } catch (SQLException e) {
                e.printStackTrace();
                AlertDialog.showErrorAlert("FAILED TO DELETE USER",
                        null,
                        "An error occurred while deleting user " + username + ". Please try again later.",
                        null);
            }
        }
        return false;
    }
}