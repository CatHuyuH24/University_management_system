package atbmhttt.atbmcq_16.admin.Views;

import atbmhttt.atbmcq_16.admin.ViewModels.AdminViewModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class AdminView extends Application {

    private AdminViewModel adminViewModel;

    public AdminView(String username, String password) {
        adminViewModel = new AdminViewModel(username, password);
    }

    @Override
    public void start(Stage primaryStage) {
        // Left navigation panel
        VBox navigationPanel = new VBox(10);
        navigationPanel.setPadding(new Insets(10));

        Button usersButton = new Button("Users");
        Button rolesButton = new Button("Roles");
        Button privilegesButton = new Button("Privileges");
        Button logoutButton = new Button("Log out");

        navigationPanel.getChildren().addAll(usersButton, rolesButton, privilegesButton, logoutButton);

        BorderPane contentArea = new BorderPane();
        // Right content area
        Text text = new Text("Welcome administrator " + adminViewModel.getUsername());
        // Event handlers for navigation buttons
        ListView<String> usersListView = new ListView<>();
        usersButton.setOnAction(e -> {
            List<String> users = adminViewModel.getUsers();
            usersListView.setItems(FXCollections.observableArrayList(users));
            contentArea.setCenter(usersListView);
        });
        rolesButton.setOnAction(e -> {
            text.setText("Roles");
            contentArea.setCenter(text);
        });
        privilegesButton.setOnAction(e -> {
            text.setText("Priviledges");
            contentArea.setCenter(new Label("Priviledges"));
        });
        logoutButton.setOnAction(e -> showLogoutConfirmation());

        contentArea.setCenter(text);

        // SplitPane to divide navigation and content
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navigationPanel, contentArea);
        splitPane.setDividerPositions(0.25);

        // Scene setup
        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLogoutConfirmation() {
        // Placeholder for logout confirmation dialog
        System.out.println("Logout confirmation dialog");
    }
}