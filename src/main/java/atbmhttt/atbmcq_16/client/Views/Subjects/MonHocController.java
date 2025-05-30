package atbmhttt.atbmcq_16.client.Views.Subjects;

import atbmhttt.atbmcq_16.DatabaseConnection;
import atbmhttt.atbmcq_16.client.Models.MonHoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class MonHocController {
    private ListView<MonHoc> monHocListView;
    private TextField mammField, mahpField, magvField, hkField, namField;
    private Label messageLabel;
    private ObservableList<MonHoc> monHocList = FXCollections.observableArrayList();
    private MonHoc selectedMonHoc; // Để lưu môn học được chọn

    public MonHocController() {
    }

    public Scene createScene() {
        // Replace VBox with ListView
        monHocListView = new ListView<>();
        monHocListView.setItems(monHocList);
        monHocListView.setCellFactory(new Callback<ListView<MonHoc>, javafx.scene.control.ListCell<MonHoc>>() {
            @Override
            public javafx.scene.control.ListCell<MonHoc> call(ListView<MonHoc> param) {
                return new javafx.scene.control.ListCell<MonHoc>() {
                    @Override
                    protected void updateItem(MonHoc item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText("Subject ID: " + item.getMamm() +
                                    " | Course ID: " + item.getMahp() +
                                    " | Lecturer ID: " + (item.getMagv() != null ? item.getMagv() : "N/A") +
                                    " | Semester: " + item.getHk() +
                                    " | Year: " + item.getNam());
                        }
                    }
                };
            }
        });
        monHocListView.setPrefHeight(300);

        // Create TextFields
        mammField = new TextField();
        mahpField = new TextField();
        magvField = new TextField();
        hkField = new TextField();
        namField = new TextField();

        // Create Label and TextField layout
        HBox inputLayout = new HBox(10);
        inputLayout.getChildren().addAll(
                createInputVBox("Subject ID", mammField),
                createInputVBox("Course ID", mahpField),
                createInputVBox("Lecturer ID", magvField),
                createInputVBox("Semester", hkField),
                createInputVBox("Year", namField));

        // Create Buttons
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> handleAdd());

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> handleUpdate());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDelete());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> handleRefresh());

        HBox buttonLayout = new HBox(10, addButton, updateButton, deleteButton, refreshButton);

        // Create message label
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        // Create title
        Label titleLabel = new Label("Subject Management");
        titleLabel.setFont(new Font("System Bold", 16));

        // Arrange layout
        VBox layout = new VBox(10, titleLabel, monHocListView, inputLayout, buttonLayout, messageLabel);
        layout.setPadding(new Insets(10));

        // Load data and handle row selection
        loadData();
        monHocListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMonHoc = newSelection;
                mammField.setText(newSelection.getMamm());
                mahpField.setText(newSelection.getMahp());
                magvField.setText(newSelection.getMagv() != null ? newSelection.getMagv() : "");
                hkField.setText(String.valueOf(newSelection.getHk()));
                namField.setText(String.valueOf(newSelection.getNam()));
            }
        });

        return new Scene(layout, 900, 600);
    }

    private VBox createInputVBox(String labelText, TextField textField) {
        Label label = new Label(labelText);
        VBox vBox = new VBox(5, label, textField);
        return vBox;
    }

    private String getUserRole(Connection conn) throws SQLException {
        String role = "UNKNOWN";
        String sql = "SELECT ROLE FROM SESSION_ROLES WHERE ROLE IN ('ROLE_GV', 'ROLE_NVPDT', 'ROLE_TRGDV', 'ROLE_SINHVIEN')";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String currentRole = rs.getString("ROLE");
                System.out.println("Detected role: " + currentRole);
                if (currentRole.equals("ROLE_NVPDT"))
                    return "ROLE_NVPDT";
                else if (currentRole.equals("ROLE_GV"))
                    role = "ROLE_GV";
                else if (currentRole.equals("ROLE_TRGDV"))
                    role = "ROLE_TRGDV";
                else if (currentRole.equals("ROLE_SINHVIEN"))
                    role = "ROLE_SINHVIEN";
            }
        }
        return role;
    }

    private void loadData() {
        monHocList.clear();
        // No need to clearChildren for ListView
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            messageLabel.setText("Your role: " + userRole);
            String sql = "";

            if (userRole.equals("ROLE_GV")) {
                sql = "SELECT MAMM, MAHP, MAGV, HK, NAM FROM ATBMCQ_ADMIN.MOMON_GV";
            } else if (userRole.equals("ROLE_NVPDT")) {
                sql = "SELECT MAMM, MAHP, MAGV, HK, NAM FROM ATBMCQ_ADMIN.MOMON_NVPDT";
            } else if (userRole.equals("ROLE_TRGDV")) {
                sql = "SELECT MAMM, MAHP, MAGV, HK, NAM FROM ATBMCQ_ADMIN.MOMON_TRGDV";
            } else if (userRole.equals("ROLE_SINHVIEN")) {
                sql = "SELECT MAMM, MAHP, MAGV, HK, NAM FROM ATBMCQ_ADMIN.MOMON_SINHVIEN";
            } else {
                messageLabel.setText("Invalid role, using default MOMON!");
                sql = "SELECT MAMM, MAHP, MAGV, HK, NAM FROM ATBMCQ_ADMIN.MOMON";
            }

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                int count = 0;
                while (rs.next()) {
                    MonHoc monHoc = new MonHoc(
                            rs.getString("MAMM"),
                            rs.getString("MAHP"),
                            rs.getString("MAGV"),
                            rs.getInt("HK"),
                            rs.getInt("NAM"));
                    monHocList.add(monHoc);
                    count++;
                }
                messageLabel.setText("Subject data loaded! Rows: " + count);
            }
        } catch (SQLException ex) {
            messageLabel.setText("Failed to load subject data");
        }
    }

    private void handleAdd() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            String sql = "";
            if (userRole.equals("ROLE_NVPDT")) {
                sql = "INSERT INTO ATBMCQ_ADMIN.MOMON_NVPDT (MAMM, MAHP, MAGV, HK, NAM) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, mammField.getText().trim());
                    pstmt.setString(2, mahpField.getText().trim());
                    pstmt.setString(3, magvField.getText().trim());
                    pstmt.setInt(4, Integer.parseInt(hkField.getText().trim()));
                    pstmt.setInt(5, Integer.parseInt(namField.getText().trim()));
                    pstmt.executeUpdate();
                    loadData();
                    clearFields();
                    messageLabel.setText("Subject added successfully!");
                }
            } else {
                messageLabel.setText("You do not have permission to add subjects!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Error adding subject");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Semester and Year must be numbers!");
        }
    }

    private void handleUpdate() {
        if (selectedMonHoc == null) {
            messageLabel.setText("Please select a subject to update!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            String sql = "";
            if (userRole.equals("ROLE_NVPDT")) {
                sql = "UPDATE ATBMCQ_ADMIN.MOMON_NVPDT SET MAHP = ?, MAGV = ?, HK = ?, NAM = ? WHERE MAMM = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, mahpField.getText().trim());
                    pstmt.setString(2, magvField.getText().trim());
                    pstmt.setInt(3, Integer.parseInt(hkField.getText().trim()));
                    pstmt.setInt(4, Integer.parseInt(namField.getText().trim()));
                    pstmt.setString(5, mammField.getText().trim());
                    pstmt.executeUpdate();
                    loadData();
                    clearFields();
                    messageLabel.setText("Subject updated successfully!");
                }
            } else {
                messageLabel.setText("You do not have permission to update subjects!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Error updating subject");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Semester and Year must be numbers!");
        }
    }

    private void handleDelete() {
        if (selectedMonHoc == null) {
            messageLabel.setText("Please select a subject to delete!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            String sql = "";
            if (userRole.equals("ROLE_NVPDT")) {
                sql = "DELETE FROM ATBMCQ_ADMIN.MOMON_NVPDT WHERE MAMM = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, mammField.getText().trim());
                    pstmt.executeUpdate();
                    loadData();
                    clearFields();
                    messageLabel.setText("Subject deleted successfully!");
                }
            } else {
                messageLabel.setText("You do not have permission to delete subjects!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Error deleting subject");
        }
    }

    private void handleRefresh() {
        loadData();
        clearFields();
        messageLabel.setText("Subject data refreshed!");
    }

    private void clearFields() {
        mammField.setText("");
        mahpField.setText("");
        magvField.setText("");
        hkField.setText("");
        namField.setText("");
        selectedMonHoc = null;
    }
}
