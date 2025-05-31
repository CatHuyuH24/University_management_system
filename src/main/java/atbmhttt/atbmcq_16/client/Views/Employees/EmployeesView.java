package atbmhttt.atbmcq_16.client.Views.Employees;

import javafx.scene.layout.BorderPane;

public class EmployeesView {
    public void displayEmployees(BorderPane contentArea, String username) {
        NhanvienController controller = new NhanvienController(username);
        contentArea.setCenter(controller.createView());
    }
}
