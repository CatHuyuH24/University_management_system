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
        LoginViewModel viewModel = new LoginViewModel();
        LoginView loginView = new LoginView(viewModel, this);
        loginView.start(primaryStage);
    }

    public void navigateToAdminDashboard() {
        Label helloLabel = new Label("ADMIN");
        Scene helloScene = new Scene(new StackPane(helloLabel), 400, 300);
        primaryStage.setScene(helloScene);
    }

    public void navigateToClientDashboard() {
        Label helloLabel = new Label("CLIENT_FIX");
        Scene helloScene = new Scene(new StackPane(helloLabel), 400, 300);
        primaryStage.setScene(helloScene);
    }
}