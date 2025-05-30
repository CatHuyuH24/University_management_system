package atbmhttt.atbmcq_16.client.Views.Subjects;

import atbmhttt.atbmcq_16.MonHocController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SubjectsView {
    public void displaySubjects(BorderPane contentArea, String username, String password) {
        MonHocController controller = new MonHocController(username, password);
        Scene scene = controller.createScene();
        contentArea.setCenter(scene.getRoot());
    }
}
