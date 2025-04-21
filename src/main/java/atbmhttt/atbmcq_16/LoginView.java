package atbmhttt.atbmcq_16;

import java.sql.SQLException;

import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; // Import PasswordField
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginView {
    private final LoginViewModel viewModel;

    public LoginView(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void start(Stage stage) {
        // Create a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Set horizontal gap between columns
        gridPane.setVgap(10); // Set vertical gap between rows
        gridPane.setAlignment(Pos.CENTER); // Center the grid in the scene

        Label title = new Label("UNIVERSITY MANAGEMENT SYSTEM");
        Label subtitle = new Label("ATBMCQ-16");

        // Add title and subtitle to the GridPane
        // Update title color to purple
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: purple;");
        gridPane.add(title, 0, 0, 2, 1); // Span across 2 columns

        subtitle.setStyle("-fx-font-size: 18px;");
        gridPane.add(subtitle, 0, 1, 2, 1); // Span across 2 columns

        // Username field
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());

        // Password field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        // Login button
        Button loginButton = new Button("Login");
        // Update login button action to use the router for navigation
        loginButton.setOnAction(event -> {
            try {
                viewModel.login();
            } catch (SQLException e) {
                AlertDialog.showErrorAlert(
                        "INVALID LOGIN CREDENTIAL",
                        null,
                        "Invalid username or password.\nPlease try again.",
                        null);
                e.printStackTrace();
            } catch (Exception e) {
                AlertDialog.showErrorAlert(
                        "ERROR",
                        null,
                        "An error has occurred.\nPlease try again.",
                        null);
                e.printStackTrace();
            }
        });

        // Adjust other components' positions
        gridPane.add(usernameLabel, 0, 2); // Column 0, Row 2
        gridPane.add(usernameField, 1, 2); // Column 1, Row 2
        gridPane.add(passwordLabel, 0, 3); // Column 0, Row 3
        gridPane.add(passwordField, 1, 3); // Column 1, Row 3
        gridPane.add(loginButton, 1, 4); // Column 1, Row 4
        loginButton.setPrefWidth(350);

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(gridPane, 600, 450);
        stage.setTitle("Login");

        stage.setScene(scene);
        stage.show();
    }
}