package atbmhttt.atbmcq_16.client.Views.Employees;

import javafx.scene.layout.BorderPane;

public class EmployeesView {
    public void displayEmployees(BorderPane contentArea, String username, String password) {
        NhanvienController controller = new NhanvienController(username, password);
        contentArea.setCenter(controller.createScene().getRoot());
    }
}
