package atbmhttt.atbmcq_16;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

        // Input field
        Label userOrRoleLabel = new Label("User/Role:");
        TextField userOrRoleField = new TextField();

        // Output area
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        // View button
        Button viewButton = new Button("View Privileges");
        viewButton.setOnAction(event -> {
            String userOrRole = userOrRoleField.getText();
            String result = privilegeViewer.viewPrivileges(userOrRole);
            outputArea.setText(result);
        });

        // Add components to the grid
        gridPane.add(userOrRoleLabel, 0, 0);
        gridPane.add(userOrRoleField, 1, 0);
        gridPane.add(viewButton, 1, 1);
        gridPane.add(outputArea, 0, 2, 2, 1);

        // Create and set the scene
        Scene scene = new Scene(gridPane, 600, 400);
        stage.setTitle("View Privileges");
        stage.setScene(scene);
        stage.show();
    }
}