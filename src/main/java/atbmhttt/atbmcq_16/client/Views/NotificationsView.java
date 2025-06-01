package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.client.Repositories.NotificationRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.util.List;

public class NotificationsView {
    public static void showNotifications(Connection conn, BorderPane contentArea) {
        VBox notificationsContainer = new VBox(20);
        notificationsContainer.setStyle("-fx-padding: 16;");
        notificationsContainer.setAlignment(Pos.CENTER);

        try {
            List<NotificationRepository.Notification> notifications = NotificationRepository.getAllNotifications(conn);
            if (notifications.isEmpty()) {
                Label emptyLabel = new Label("No notifications available.");
                emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #888;");
                notificationsContainer.getChildren().add(emptyLabel);
            } else {
                int stt = 1;
                for (NotificationRepository.Notification n : notifications) {
                    GridPane notificationGrid = new GridPane();
                    notificationGrid.setStyle(
                            "-fx-border-color: #888; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-background-color: #f9f9f9; -fx-margin: 16");
                    notificationGrid.setAlignment(Pos.TOP_LEFT);
                    notificationGrid.setHgap(10);
                    notificationGrid.setVgap(5);

                    Label sttLabel = new Label(stt + ". ");
                    sttLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
                    Label contentLabel = new Label(n.noidung);
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-size: 15;");

                    notificationGrid.add(sttLabel, 0, 0);
                    notificationGrid.add(contentLabel, 1, 0);

                    notificationsContainer.getChildren().add(notificationGrid);
                    stt++;
                }
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Error loading notifications: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            notificationsContainer.getChildren().add(errorLabel);
        }

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(notificationsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setTop(new Label("Notification List:"));
        pane.setCenter(scrollPane);

        // Display directly on contentArea instead of opening a new window
        contentArea.setCenter(pane);
    }
}
