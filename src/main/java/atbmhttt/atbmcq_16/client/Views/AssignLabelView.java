package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.client.Repositories.UserLabelRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.util.List;

public class AssignLabelView {
    public static void show(Connection conn) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Gán nhãn cho người dùng");

        VBox root = new VBox(16);
        root.setPadding(new Insets(18));
        root.setAlignment(Pos.TOP_CENTER);

        Label labelTitle = new Label("Chọn định danh nhãn:");
        ComboBox<String> dinhdanhCombo = new ComboBox<>();
        TableView<UserLabelRepository.UserLabelInfo> table = new TableView<>();
        TableColumn<UserLabelRepository.UserLabelInfo, String> dinhCol = new TableColumn<>("Định danh");
        dinhCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().dinhdanh));
        TableColumn<UserLabelRepository.UserLabelInfo, String> nameCol = new TableColumn<>("Tên nhãn");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().name));
        table.getColumns().addAll(dinhCol, nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(180);

        try {
            List<UserLabelRepository.UserLabelInfo> labels = UserLabelRepository.getAllUserLabels(conn);
            table.getItems().addAll(labels);
            for (UserLabelRepository.UserLabelInfo l : labels) {
                dinhdanhCombo.getItems().add(l.dinhdanh);
            }
        } catch (Exception e) {
            root.getChildren().add(new Label("Lỗi tải label: " + e.getMessage()));
        }

        Label userLabel = new Label("Chọn user để gán nhãn:");
        ComboBox<String> userCombo = new ComboBox<>();
        userCombo.setPromptText("Chọn user");
        try {
            List<String> users = UserLabelRepository.getUserList(conn);
            userCombo.getItems().addAll(users);
        } catch (Exception e) {
            root.getChildren().add(new Label("Lỗi tải user: " + e.getMessage()));
        }

        Button submitBtn = new Button("Gán nhãn");
        Label statusLabel = new Label();
        submitBtn.setOnAction(ev -> {
            String dinhdanh = dinhdanhCombo.getValue();
            String user = userCombo.getValue();
            if (dinhdanh == null || user == null) {
                statusLabel.setText("Vui lòng chọn định danh và user!");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            try {
                UserLabelRepository.setOlsLabelForUser(conn, user, dinhdanh);
                statusLabel.setText("Đã gán nhãn " + dinhdanh + " cho user " + user + " thành công!");
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception ex) {
                statusLabel.setText("Lỗi khi gán nhãn: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        root.getChildren().addAll(labelTitle, table, dinhdanhCombo, userLabel, userCombo, submitBtn, statusLabel);
        Scene scene = new Scene(root, 500, 420);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
