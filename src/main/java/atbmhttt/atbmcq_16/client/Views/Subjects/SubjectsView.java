package atbmhttt.atbmcq_16.client.Views.Subjects;

import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.scene.layout.BorderPane;

public class SubjectsView {
    public void displaySubjects(BorderPane contentArea) {
        MonHocController controller = new MonHocController();
        BorderPaneHelper.setAllSections(contentArea,
                null, null, null, null,
                controller.createView());
    }
}
