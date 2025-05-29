package atbmhttt.atbmcq_16.admin.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PrivilegesView {
    private UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
    private RolePrivilegesView rolePrivilegesView = new RolePrivilegesView();

    public void display(BorderPane contentArea) {
        // Clear previous content
        contentArea.setCenter(null);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);

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
        });

        rolePrivilegesButton.setOnAction(event -> {
            rolePrivilegesView.displayRolePrivileges();
        });

        layout.getChildren().addAll(userPrivilegesButton, rolePrivilegesButton);

        contentArea.setCenter(layout);
    }
}
