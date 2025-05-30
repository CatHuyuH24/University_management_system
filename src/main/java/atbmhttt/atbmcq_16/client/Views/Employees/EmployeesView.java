package atbmhttt.atbmcq_16.client.Views.Employees;

import atbmhttt.atbmcq_16.helpers.BorderPaneHelper;
import javafx.scene.layout.BorderPane;

public class EmployeesView {
    public void displayEmployees(BorderPane contentArea, String username) {
        NhanvienController controller = new NhanvienController(username);
        BorderPaneHelper.setAllSections(contentArea,
                null, null, null, null,
                controller.createScene().getRoot());
    }
}
