package atbmhttt.atbmcq_16;

import atbmhttt.atbmcq_16.admin.Views.AdminView;
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
        AdminView view = new AdminView();
        view.start(primaryStage);
    }

    public void navigateToClientDashboard() {
        Label helloLabel = new Label("CLIENT_FIX");
        Scene helloScene = new Scene(new StackPane(helloLabel), 400, 300);
        primaryStage.setTitle("Client dashboard");
        primaryStage.setScene(helloScene);
    }
}