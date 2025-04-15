package atbmhttt.atbmcq_16;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Router router = new Router(primaryStage);
        router.navigateToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}