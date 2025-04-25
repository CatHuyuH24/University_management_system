package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.admin.ViewModels.RolesViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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

        BorderPaneHelper.setAllSections(contentArea,
                null, null,
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