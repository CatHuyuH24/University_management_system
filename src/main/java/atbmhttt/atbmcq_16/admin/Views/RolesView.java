package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.admin.ViewModels.RolesViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class RolesView {

    private final RolesViewModel rolesViewModel;
    private TableView<String[]> rolesTableView;

    public RolesView() {
        System.out.println("new roles view");
        this.rolesViewModel = new RolesViewModel();
    }

    public void displayRoles(BorderPane contentArea) {
        ObservableList<String[]> roles = rolesViewModel.getRoles();

        rolesTableView = new TableView<>();

        TableColumn<String[], String> roleNameColumn = new TableColumn<>("Role Name");
        roleNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

        TableColumn<String[], Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Roles");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    String roleName = getTableView().getItems().get(getIndex())[0];
                    openEditRoleWindow(roleName);

                });

                deleteButton.setOnAction(event -> {
                    String roleName = getTableView().getItems().get(getIndex())[0];
                    confirmAndDeleteRole(roleName);
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

        rolesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roleNameColumn.setStyle("-fx-alignment: CENTER;");

        rolesTableView.getColumns().add(roleNameColumn);
        rolesTableView.getColumns().add(actionsColumn);
        rolesTableView.setItems(roles);

        roles.addListener((ListChangeListener<String[]>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    rolesTableView.refresh();
                }
            }
        });

        Button addRoleButton = new Button("Add Role");
        addRoleButton.setOnAction(e -> {
            Stage addRoleStage = new Stage();
            addRoleStage.setTitle("ADDING NEW ROLE");
            addRoleStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));

            Label roleNameLabel = new Label("Enter role name:");
            TextField roleNameField = new TextField();

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(submitEvent -> {
                String roleName = roleNameField.getText();
                if (roleName.isEmpty()) {
                    AlertDialog.showErrorAlert(
                            "EMPTY ROLE NAME",
                            null,
                            "Role name cannot be empty. Please try again.",
                            null, 400, 200);
                } else {
                    try {
                        rolesViewModel.addRole(roleName);
                        AlertDialog.showInformationAlert(
                                "ROLE ADDED SUCCESSFULLY",
                                null,
                                "Role " + roleName + " has been added successfully.",
                                null, 400, 200);
                        addRoleStage.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        if (20010 == ex.getErrorCode()) {
                            AlertDialog.showErrorAlert(
                                    "ROLE ALREADY EXISTED", null,
                                    "Role " + roleName
                                            + " already existed!\nPlease delete it first, or use another role name",
                                    null, 400, 200);
                        } else {

                            AlertDialog.showErrorAlert(
                                    "FAILED TO ADD ROLE",
                                    null,
                                    "An error occurred while adding role " + roleName + ". Please try again later.",
                                    null, 400, 200);
                        }
                    }
                }
            });

            layout.getChildren().addAll(roleNameLabel, roleNameField, submitButton);
            layout.setAlignment(Pos.CENTER);

            addRoleStage.setScene(new Scene(layout, 300, 200));
            addRoleStage.show();
        });

        // Add the button to the UI
        VBox actionButtons = new VBox(10, addRoleButton, new Pane());

        BorderPaneHelper.setAllSections(contentArea,
                null, actionButtons,
                null, null, rolesTableView);
    }

    private void confirmAndDeleteRole(String roleName) {
        ButtonType response = AlertDialog.showAndGetResultConfirmationAlert(
                "DELETE ROLE " + roleName, null,
                "Are you sure you want to delete role " + roleName + "?",
                null, 400, 200);

        if (ButtonType.OK == response) {
            try {
                rolesViewModel.deleteRole(roleName);
                AlertDialog.showInformationAlert("ROLE DELETED",
                        null,
                        "Role " + roleName + " has been deleted successfully.",
                        null, 400, 200);
            } catch (SQLException e) {
                e.printStackTrace();
                AlertDialog.showErrorAlert("FAILED TO DELETE ROLE",
                        null,
                        "An error occurred while deleting role " + roleName + ". Please try again later.",
                        null, 400, 200);
            }
        }
    }

    private void openEditRoleWindow(String roleName) {
        Stage editRoleStage = new Stage();
        editRoleStage.setTitle("ADD NEW ROLE FOR ROLE " + roleName);
        editRoleStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label grantedRolesLabel = new Label("ROLES GRANTED TO " + roleName + "LIST");
        TableView<String> grantedRolesTable = new TableView<>();

        grantedRolesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        grantedRolesTable.setPlaceholder(new Label("No roles granted yet."));

        TableColumn<String, String> grantedRoleColumn = new TableColumn<>("Granted Role");
        grantedRoleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
        grantedRolesTable.getColumns().add(grantedRoleColumn);

        try {
            grantedRolesTable.setItems(rolesViewModel.getGrantedRoles(roleName));
        } catch (Exception e) {
            AlertDialog.showErrorAlert("Error Fetching Granted Roles", null,
                    "An error occurred while fetching granted roles for " + roleName + ".\n" + e.getMessage(), null,
                    400, 200);
        }

        Button grantRoleButton = new Button("GRANT ROLE");
        grantRoleButton.setOnAction(e -> openGrantRoleWindow(roleName, editRoleStage));

        layout.getChildren().addAll(grantedRolesLabel, grantedRolesTable, grantRoleButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        editRoleStage.setScene(scene);
        editRoleStage.show();
    }

    private void openGrantRoleWindow(String roleName, final Stage editRoleStage) {
        Stage grantRoleStage = new Stage();
        grantRoleStage.setTitle("GRANT ROLE TO " + roleName);
        grantRoleStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label availableRolesLabel = new Label("Available Roles:");
        TableView<String> availableRolesTable = new TableView<>();

        availableRolesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        availableRolesTable.setPlaceholder(new Label("No roles available."));

        TableColumn<String, String> availableRoleColumn = new TableColumn<>("Role");
        availableRoleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
        availableRolesTable.getColumns().add(availableRoleColumn);

        try {
            availableRolesTable.setItems(rolesViewModel.getAvailableRoles(roleName));
        } catch (Exception e) {
            AlertDialog.showErrorAlert("Error Fetching Available Roles", null,
                    "An error occurred while fetching available roles for " + roleName + ".\n" + e.getMessage(), null,
                    400, 200);
        }

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            ObservableList<String> selectedRoles = availableRolesTable.getSelectionModel().getSelectedItems();
            for (String selectedRole : selectedRoles) {
                try {
                    rolesViewModel.grantRoleToRole(selectedRole, roleName);
                    editRoleStage.close();
                } catch (Exception ex) {
                    AlertDialog.showErrorAlert("Error Granting Role", null, "An error occurred while granting role "
                            + selectedRole + " to " + roleName + ".\n" + ex.getMessage(), null, 400, 200);
                }
            }
            grantRoleStage.close();
        });

        layout.getChildren().addAll(availableRolesLabel, availableRolesTable, submitButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        grantRoleStage.setScene(scene);
        grantRoleStage.show();
    }
}