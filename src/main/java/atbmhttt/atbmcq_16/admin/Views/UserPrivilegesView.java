package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.admin.ViewModels.UserPrivilegesViewModel;
import java.sql.SQLException;

import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserPrivilegesView {
    private UserPrivilegesViewModel viewModel = new UserPrivilegesViewModel();

    public void displayUserPrivileges() {
        Stage userPrivilegesStage = new Stage();
        Image iconImage = new Image(getClass().getResource("/images/app_icon.png").toExternalForm());
        userPrivilegesStage.getIcons().add(iconImage);
        userPrivilegesStage.setTitle("USERS PRIVILEGES");

        TableView<String[]> tableView = new TableView<>();

        TableColumn<String[], Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button("View and Revoke Privileges");
            private final Button grantButton = new Button("Grant Privileges");
            private final VBox buttonContainer = new VBox(5, actionButton, grantButton);

            {
                actionButton.setOnAction(event -> {
                    String username = getTableView().getItems().get(getIndex())[0];
                    SingleUserPrivView singleView = new SingleUserPrivView();
                    singleView.displayUserPrivs(username);
                });

                grantButton.setOnAction(event -> {
                    GrantPrivilegesView view = new GrantPrivilegesView();
                    view.displayPrivileges(getTableView().getItems().get(getIndex())[0]);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });

        TableColumn<String[], String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));

        tableView.getColumns().add(actionColumn);
        tableView.getColumns().add(userColumn);
        try {
            tableView.setItems(FXCollections.observableArrayList(viewModel.getUsersWithDetails()));
        } catch (SQLException ex) {
            ex.printStackTrace();
            AlertDialog.showErrorAlert("ERROR FETCHING USER PRIVILEGES",
                    null,
                    "An error occurred while fetching user privileges. Please try again later.",
                    null);
        }

        VBox layout = new VBox(10, tableView);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 450);
        userPrivilegesStage.setScene(scene);
        userPrivilegesStage.show();
    }
}
