package atbmhttt.atbmcq_16.admin.Views;

import java.sql.SQLException;

import atbmhttt.atbmcq_16.admin.ViewModels.RolePrivilegesViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RolePrivilegesView {
    private RolePrivilegesViewModel viewModel = new RolePrivilegesViewModel();

    public void displayRolePrivileges() {
        Stage rolePrivilegesStage = new Stage();
        rolePrivilegesStage.setTitle("Role Privileges");

        TableView<String[]> tableView = new TableView<>();

        TableColumn<String[], Void> actionColumn = new TableColumn<>("Edit Privileges");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button("View and Revoke Privileges");

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
        try {
            tableView.setItems(FXCollections.observableArrayList(viewModel.getPdbRoles()));
        } catch (SQLException ex) {
            ex.printStackTrace();
            AlertDialog.showErrorAlert("ERROR FETCHING ROLE PRIVILEGES",
                    null,
                    "An error occurred while fetching role privileges. Please try again later.",
                    null);
        }

        VBox layout = new VBox(10, tableView);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 450);
        rolePrivilegesStage.setScene(scene);
        rolePrivilegesStage.show();
    }
}
