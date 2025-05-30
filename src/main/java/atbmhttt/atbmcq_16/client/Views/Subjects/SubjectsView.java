package atbmhttt.atbmcq_16.client.Views.Subjects;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SubjectsView {
    public void displaySubjects(BorderPane contentArea) {
        MonHocController controller = new MonHocController();
        Scene scene = controller.createScene();
        contentArea.setCenter(scene.getRoot());
    }
}
