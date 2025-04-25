package atbmhttt.atbmcq_16.admin.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PrivilegesView {
    private UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
    private RolePrivilegesView rolePrivilegesView = new RolePrivilegesView();

    public void display(BorderPane contentArea) {
        Stage privilegesStage = new Stage();
        privilegesStage.setTitle("Privileges Management");

        // Set the icon in the title bar
        privilegesStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app_icon.png")));

        privilegesStage.setOnCloseRequest(event -> privilegesStage.close());

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(
                new Image(getClass().getResourceAsStream("/images/app_icon.png")));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Label titleLabel = new Label("Privileges Management");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        header.getChildren().addAll(imageView, titleLabel);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().add(header); // Add the header with the logo and title
        layout.setAlignment(Pos.CENTER);

        Button userPrivilegesButton = new Button("User Privileges");
        Button rolePrivilegesButton = new Button("Role Privileges");

        userPrivilegesButton.setOnAction(event -> {
            userPrivilegesView.displayUserPrivileges();
            privilegesStage.close();
        });

        rolePrivilegesButton.setOnAction(event -> {
            rolePrivilegesView.displayRolePrivileges();
            privilegesStage.close();
        });

        layout.getChildren().addAll(userPrivilegesButton, rolePrivilegesButton);

        Scene scene = new Scene(layout, 300, 200);
        privilegesStage.setScene(scene);
        privilegesStage.show();
    }
}
