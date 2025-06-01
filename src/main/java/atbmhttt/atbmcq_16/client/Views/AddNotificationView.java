package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.client.Repositories.LabelRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.util.List;

public class AddNotificationView {
    public static void show(Connection conn) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Notification");
        // Set app icon like App.java
        try {
            Image iconImage = new Image(AddNotificationView.class.getResource("/images/app_icon.png").toExternalForm());
            stage.getIcons().add(iconImage);
        } catch (Exception e) {
            System.err.println("Image not found: app_icon.png");
        }

        VBox root = new VBox(16);
        root.setPadding(new Insets(18));
        root.setAlignment(Pos.TOP_CENTER);

        Label labelTitle = new Label("Select label for notification:");
        TableView<LabelRepository.LabelInfo> table = new TableView<>();
        TableColumn<LabelRepository.LabelInfo, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().id));
        TableColumn<LabelRepository.LabelInfo, String> dinhCol = new TableColumn<>("Identifier");
        dinhCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().dinhdanh));
        TableColumn<LabelRepository.LabelInfo, String> nameCol = new TableColumn<>("Label Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().nameLabel));
        table.getColumns().add(idCol);
        table.getColumns().add(dinhCol);
        table.getColumns().add(nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);

        ComboBox<String> dinhdanhCombo = new ComboBox<>();
        dinhdanhCombo.setPromptText("Select label identifier");

        try {
            List<LabelRepository.LabelInfo> labels = LabelRepository.getAllLabels(conn);
            table.getItems().addAll(labels);
            for (LabelRepository.LabelInfo l : labels) {
                dinhdanhCombo.getItems().add(l.dinhdanh);
            }
        } catch (Exception e) {
            root.getChildren().add(new Label("Error loading labels: " + e.getMessage()));
        }

        Label contentLabel = new Label("Enter notification content:");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter content...");
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(4);

        Button submitBtn = new Button("Add Notification");
        Label statusLabel = new Label();
        submitBtn.setOnAction(ev -> {
            String noidung = contentArea.getText().trim();
            String dinhdanh = dinhdanhCombo.getValue();
            if (noidung.isEmpty() || dinhdanh == null) {
                statusLabel.setText("Please enter content and select a label!");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            try {
                LabelRepository.insertNotification(conn, noidung, dinhdanh);
                statusLabel.setText("Notification added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
                contentArea.clear();
                dinhdanhCombo.getSelectionModel().clearSelection();
            } catch (Exception ex) {
                statusLabel.setText("Error adding notification: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        root.getChildren().addAll(labelTitle, table, dinhdanhCombo, contentLabel, contentArea, submitBtn, statusLabel);
        Scene scene = new Scene(root, 600, 450);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
