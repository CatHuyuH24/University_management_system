package atbmhttt.atbmcq_16;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

        // Input fields
        Label privilegeLabel = new Label("Privilege:");
        TextField privilegeField = new TextField();

        Label granteeLabel = new Label("User/Role:");
        TextField granteeField = new TextField();

        // Revoke button
        Button revokeButton = new Button("Revoke");
        revokeButton.setOnAction(event -> {
            String privilege = privilegeField.getText();
            String grantee = granteeField.getText();
            privilegeManager.revokePrivilege(privilege, grantee);
        });

        // Add components to the grid
        gridPane.add(privilegeLabel, 0, 0);
        gridPane.add(privilegeField, 1, 0);
        gridPane.add(granteeLabel, 0, 1);
        gridPane.add(granteeField, 1, 1);
        gridPane.add(revokeButton, 1, 2);

        // Create and set the scene
        Scene scene = new Scene(gridPane, 400, 300);
        stage.setTitle("Revoke Privilege");
        stage.setScene(scene);
        stage.show();
    }
}