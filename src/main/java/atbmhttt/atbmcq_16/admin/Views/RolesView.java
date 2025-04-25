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
        this.rolesViewModel = new RolesViewModel();
    }

    public void displayRoles(BorderPane contentArea) {
        ObservableList<String[]> roles = rolesViewModel.getRoles();

        rolesTableView = new TableView<>();

        TableColumn<String[], String> roleNameColumn = new TableColumn<>("Role Name");
        roleNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

        TableColumn<String[], Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(5, deleteButton);

            {
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
                            null);
                } else {
                    try {
                        rolesViewModel.addRole(roleName);
                        AlertDialog.showInformationAlert(
                                "ROLE ADDED SUCCESSFULLY",
                                null,
                                "Role " + roleName + " has been added successfully.",
                                null);
                        addRoleStage.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        if (20010 == ex.getErrorCode()) {
                            AlertDialog.showErrorAlert(
                                    "ROLE ALREADY EXISTED", null,
                                    "Role " + roleName
                                            + " already existed!\nPlease delete it first, or use another role name",
                                    null);
                        } else {

                            AlertDialog.showErrorAlert(
                                    "FAILED TO ADD ROLE",
                                    null,
                                    "An error occurred while adding role " + roleName + ". Please try again later.",
                                    null);
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
                null);

        if (ButtonType.OK == response) {
            try {
                rolesViewModel.deleteRole(roleName);
                AlertDialog.showInformationAlert("ROLE DELETED",
                        null,
                        "Role " + roleName + " has been deleted successfully.",
                        null);
            } catch (SQLException e) {
                e.printStackTrace();
                AlertDialog.showErrorAlert("FAILED TO DELETE ROLE",
                        null,
                        "An error occurred while deleting role " + roleName + ". Please try again later.",
                        null);
            }
        }
    }
}