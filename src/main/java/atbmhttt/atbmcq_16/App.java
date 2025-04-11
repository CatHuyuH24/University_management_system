package atbmhttt.atbmcq_16;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginViewModel viewModel = new LoginViewModel();
        if (viewModel.isConnectedToDatabase()) {
            StackPane root = new StackPane();
            Label helloLabel = new Label("HELLO");
            root.getChildren().add(helloLabel);
            Scene scene = new Scene(root, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Welcome");
            primaryStage.show();
        } else {
            LoginView view = new LoginView(viewModel);
            view.start(primaryStage);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}