package atbmhttt.atbmcq_16;

import atbmhttt.atbmcq_16.admin.Views.AdminView;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Router {

    private static Stage P_STAGE; // quick, simple, may use Singleton+ Service Factory instead

    public static void setPrimaryStage(Stage primaryStage) {
        P_STAGE = primaryStage;
    }

    public static void navigateToLogin() {
        LoginViewModel viewModel = new LoginViewModel();
        LoginView loginView = new LoginView(viewModel);
        loginView.start(P_STAGE);
    }

    public static void navigateToAdminDashboard(String username, String password) {
        AdminView view = new AdminView();
        view.start(P_STAGE);
    }

    public static void navigateToClientDashboard() {
        Label helloLabel = new Label("CLIENT_FIX");
        Scene helloScene = new Scene(new StackPane(helloLabel), 400, 300);
        P_STAGE.setTitle("Client dashboard");
        P_STAGE.setScene(helloScene);
    }
}