package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.admin.Models.Privilege;
import atbmhttt.atbmcq_16.admin.ViewModels.DetailPrivilegeViewModel;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DetailPrivilegesView {

    public void displayUserPrivs(String username) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        Stage stage = new Stage();

        // TableView hiển thị quyền
        TableView<Privilege> tableView = new TableView<>();
        TableColumn<Privilege, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(data -> data.getValue().typeProperty());

        TableColumn<Privilege, String> privilegeColumn = new TableColumn<>("Privilege");
        privilegeColumn.setCellValueFactory(data -> data.getValue().privilegeProperty());

        TableColumn<Privilege, String> objectColumn = new TableColumn<>("Object");
        objectColumn.setCellValueFactory(data -> data.getValue().objectProperty());

        TableColumn<Privilege, String> columnColumn = new TableColumn<>("Column");
        columnColumn.setCellValueFactory(data -> data.getValue().columnProperty());

        TableColumn<Privilege, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button revokeButton = new Button("Revoke");

            {
                revokeButton.setOnAction(event -> {
                    Privilege privilege = getTableView().getItems().get(getIndex());
                    DetailPrivilegeViewModel viewModel = new DetailPrivilegeViewModel();

                    String title = "REVOKING PRIVILEGE";
                    String content = "";
                    if ("UPDATE".equals(privilege.getPrivilege())) {
                        content = "Deleting a privilege with UPDATE will REVOKE ON THE WHOLE TABLE\n";
                    }
                    content += "Are you sure you want to REVOKE?";
                    ButtonType response = AlertDialog.showAndGetResultConfirmationAlert(
                            title,
                            null, content, null, 400, 200);
                    if (ButtonType.OK == response) {
                        viewModel.revokePrivilege(
                                username,
                                privilege.getPrivilege(),
                                privilege.getObject());

                        stage.close();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(revokeButton);
                }
            }
        });

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().add(typeColumn);
        tableView.getColumns().add(privilegeColumn);
        tableView.getColumns().add(objectColumn);
        tableView.getColumns().add(columnColumn);
        tableView.getColumns().add(actionColumn);

        // Bind the TableView width to the GridPane width
        tableView.prefWidthProperty().bind(gridPane.widthProperty());

        // Fetch privileges and set items to the TableView
        DetailPrivilegeViewModel viewModel = new DetailPrivilegeViewModel();
        ObservableList<Privilege> privileges = viewModel.getPrivileges(username);
        tableView.setItems(privileges);

        // Add components to the grid
        gridPane.add(tableView, 0, 2, 2, 1);

        // Add the application icon to the stage
        Image iconImage = new Image(getClass().getResource("/images/app_icon.png").toExternalForm());
        stage.getIcons().add(iconImage);
        // Create and set the scene
        Scene scene = new Scene(gridPane, 600, 400);
        stage.setTitle(username + " PRIVILEGES");
        stage.setScene(scene);
        stage.show();
    }

    public void initializeAndDisplay(String username) {
        displayUserPrivs(username);
    }
}