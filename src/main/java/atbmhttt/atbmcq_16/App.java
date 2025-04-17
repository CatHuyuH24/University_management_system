package atbmhttt.atbmcq_16;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // set up the icon for the whole application
        try {
            Image iconImage = new Image(getClass().getResource("/images/university_icon.png").toExternalForm());
            primaryStage.getIcons().add(iconImage);
        } catch (NullPointerException e) {
            System.err.println("Image not found: university_icon.png");
        }

        Router router = new Router(primaryStage);
        router.navigateToLogin();

    }

    public static void main(String[] args) {
        launch(args);
    }
}