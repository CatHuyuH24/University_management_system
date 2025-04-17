package atbmhttt.atbmcq_16;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Router {

    private final Stage primaryStage;

    public Router(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void navigateToLogin() {
        LoginViewModel viewModel = new LoginViewModel(this);
        LoginView loginView = new LoginView(viewModel);
        loginView.start(primaryStage);
    }

    public void navigateToAdminDashboard() {
        Label helloLabel = new Label("ADMIN");
        Scene helloScene = new Scene(new StackPane(helloLabel), 400, 300);
        primaryStage.setTitle("Admin dashboard");
        primaryStage.setScene(helloScene);
    }

    public void navigateToClientDashboard() {
        Label helloLabel = new Label("CLIENT");
        Scene helloScene = new Scene(new StackPane(helloLabel), 400, 300);
        primaryStage.setTitle("Client dashboard");
        primaryStage.setScene(helloScene);
    }
}