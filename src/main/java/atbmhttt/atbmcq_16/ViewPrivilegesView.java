package atbmhttt.atbmcq_16;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ViewPrivilegesView {
    private final PrivilegeViewer privilegeViewer;

    public ViewPrivilegesView(PrivilegeViewer privilegeViewer) {
        this.privilegeViewer = privilegeViewer;
    }

    public void start(Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // Dropdown chọn user/role
        Label userRoleLabel = new Label("User/Role:");
        ComboBox<String> userRoleDropdown = new ComboBox<>();
        userRoleDropdown.setItems(FXCollections.observableArrayList("USER1", "USER2", "ROLE1", "ROLE2"));

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

        TableColumn<Privilege, String> grantOptionColumn = new TableColumn<>("Grant Option");
        grantOptionColumn.setCellValueFactory(data -> data.getValue().grantOptionProperty());

        tableView.getColumns().addAll(typeColumn, privilegeColumn, objectColumn, columnColumn, grantOptionColumn);

        // Button View
        Button viewButton = new Button("View Privileges");
        viewButton.setOnAction(event -> {
            String userRole = userRoleDropdown.getValue();
            if (userRole == null) {
                tableView.setItems(FXCollections.observableArrayList());
                return;
            }

            ObservableList<Privilege> privileges = privilegeViewer.viewPrivileges(userRole);
            tableView.setItems(privileges);
        });

        // Add components to the grid
        gridPane.add(userRoleLabel, 0, 0);
        gridPane.add(userRoleDropdown, 1, 0);
        gridPane.add(viewButton, 1, 1);
        gridPane.add(tableView, 0, 2, 2, 1);

        // Create and set the scene
        Scene scene = new Scene(gridPane, 600, 400);
        stage.setTitle("View Privileges");
        stage.setScene(scene);
        stage.show();
    }
}