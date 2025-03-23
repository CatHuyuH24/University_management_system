package atbmhttt.atbmcq_16;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginViewModel viewModel = new LoginViewModel();
        LoginView view = new LoginView(viewModel);
        view.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}