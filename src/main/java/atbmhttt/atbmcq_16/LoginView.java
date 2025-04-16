package atbmhttt.atbmcq_16;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; // Import PasswordField
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginView {
    private final LoginViewModel viewModel;
    private final Router router;

    public LoginView(LoginViewModel viewModel, Router router) {
        this.viewModel = viewModel;
        this.router = router;
    }

    public void start(Stage stage) {
        // Create a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Set horizontal gap between columns
        gridPane.setVgap(10); // Set vertical gap between rows
        gridPane.setAlignment(Pos.CENTER); // Center the grid in the scene

        // Username field
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());

        // Password field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField(); // Replace the TextField for password with PasswordField
        // 2-way changing, the content of the text field and the
        // actual underlying state of the linked data is consistent
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        // THESE ARE FOR TESTING PURPOSES
        /*
         * 1-way changing, change the field affect the
         * label's text, not the other way around
         * passwordLabel.textProperty().bind(viewModel.passwordProperty());
         */

        // Login button
        Button loginButton = new Button("Login");
        // Update login button action to use the router for navigation
        loginButton.setOnAction(event -> {
            try {
                if (viewModel.isAdminUser()) {
                    router.navigateToAdminDashboard();
                } else {
                    router.navigateToClientDashboard();
                }
            } catch (SQLException e) {
                // Display a pop-up to the user if login fails
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password. Please try again.");
                alert.showAndWait();
            }
        });

        // Add components to the GridPane
        gridPane.add(usernameLabel, 0, 0); // Column 0, Row 0
        gridPane.add(usernameField, 1, 0); // Column 1, Row 0
        gridPane.add(passwordLabel, 0, 1); // Column 0, Row 1
        gridPane.add(passwordField, 1, 1); // Column 1, Row 1
        gridPane.add(loginButton, 1, 2); // Start at column 0, row 2
        loginButton.setMaxWidth(150);

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(gridPane, 600, 450);
        stage.setTitle("Login");

        try {
            Image iconImage = new Image(getClass().getResource("/images/university_icon.png").toExternalForm());
            stage.getIcons().add(iconImage);
        } catch (NullPointerException e) {
            System.err.println("Image not found: university_icon.png");
        }

        stage.setScene(scene);
        stage.show();
    }
}