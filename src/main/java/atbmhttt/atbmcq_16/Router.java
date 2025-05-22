package atbmhttt.atbmcq_16;

import atbmhttt.atbmcq_16.admin.Views.AdminView;
import atbmhttt.atbmcq_16.client.Views.ClientView;
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
        ClientView view = new ClientView();
        view.start(P_STAGE);
    }
}