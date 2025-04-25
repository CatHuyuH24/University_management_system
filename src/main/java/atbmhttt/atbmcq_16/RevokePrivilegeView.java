package atbmhttt.atbmcq_16;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RevokePrivilegeView {
    private final PrivilegeManager privilegeManager;

    public RevokePrivilegeView(PrivilegeManager privilegeManager) {
        this.privilegeManager = privilegeManager;
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

        // Dropdown chọn quyền
        Label privilegeLabel = new Label("Privilege:");
        ComboBox<String> privilegeDropdown = new ComboBox<>();
        privilegeDropdown.setItems(FXCollections.observableArrayList("SELECT", "UPDATE", "EXECUTE", "INSERT", "DELETE"));

        // Dropdown chọn đối tượng
        Label objectLabel = new Label("Object:");
        ComboBox<String> objectDropdown = new ComboBox<>();
        objectDropdown.setItems(FXCollections.observableArrayList("SAMPLE_TABLE", "SAMPLE_VIEW", "SAMPLE_PROCEDURE", "SAMPLE_FUNCTION"));

        // Button Revoke
        Button revokeButton = new Button("Revoke");
        Label statusLabel = new Label();
        revokeButton.setOnAction(event -> {
            String userRole = userRoleDropdown.getValue();
            String privilege = privilegeDropdown.getValue();
            String object = objectDropdown.getValue();

            if (userRole == null || privilege == null || object == null) {
                statusLabel.setText("Please select all fields.");
                return;
            }

            try {
                privilegeManager.revokePrivilege(privilege, userRole, object, null);
                statusLabel.setText("Privilege " + privilege + " on " + object + " revoked from " + userRole);
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        });

        // Add components to the grid
        gridPane.add(userRoleLabel, 0, 0);
        gridPane.add(userRoleDropdown, 1, 0);
        gridPane.add(privilegeLabel, 0, 1);
        gridPane.add(privilegeDropdown, 1, 1);
        gridPane.add(objectLabel, 0, 2);
        gridPane.add(objectDropdown, 1, 2);
        gridPane.add(revokeButton, 1, 3);
        gridPane.add(statusLabel, 1, 4);

        // Create and set the scene
        Scene scene = new Scene(gridPane, 400, 300);
        stage.setTitle("Revoke Privilege");
        stage.setScene(scene);
        stage.show();
    }
}