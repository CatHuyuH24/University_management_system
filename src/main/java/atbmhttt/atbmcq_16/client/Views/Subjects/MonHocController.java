package atbmhttt.atbmcq_16.client.Views.Subjects;

import atbmhttt.atbmcq_16.DatabaseConnection;

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
        // Thay VBox bằng ListView
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
                            setText("Mã MM: " + item.getMamm() +
                                    " | Mã HP: " + item.getMahp() +
                                    " | Mã GV: " + (item.getMagv() != null ? item.getMagv() : "N/A") +
                                    " | Học kỳ: " + item.getHk() +
                                    " | Năm: " + item.getNam());
                        }
                    }
                };
            }
        });
        monHocListView.setPrefHeight(300);

        // Tạo TextField
        mammField = new TextField();
        mahpField = new TextField();
        magvField = new TextField();
        hkField = new TextField();
        namField = new TextField();

        // Tạo Label và TextField layout
        HBox inputLayout = new HBox(10);
        inputLayout.getChildren().addAll(
            createInputVBox("Mã MM", mammField),
            createInputVBox("Mã HP", mahpField),
            createInputVBox("Mã GV", magvField),
            createInputVBox("Học kỳ", hkField),
            createInputVBox("Năm", namField)
        );

        // Tạo Button
        Button addButton = new Button("Thêm");
        addButton.setOnAction(e -> handleAdd());

        Button updateButton = new Button("Sửa");
        updateButton.setOnAction(e -> handleUpdate());

        Button deleteButton = new Button("Xóa");
        deleteButton.setOnAction(e -> handleDelete());

        Button refreshButton = new Button("Làm mới");
        refreshButton.setOnAction(e -> handleRefresh());

        HBox buttonLayout = new HBox(10, addButton, updateButton, deleteButton, refreshButton);

        // Tạo Label thông báo
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        // Tạo tiêu đề
        Label titleLabel = new Label("Quản lý Môn học");
        titleLabel.setFont(new Font("System Bold", 16));

        // Sắp xếp giao diện
        VBox layout = new VBox(10, titleLabel, monHocListView, inputLayout, buttonLayout, messageLabel);
        layout.setPadding(new Insets(10));

        // Load dữ liệu và xử lý chọn dòng
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
                if (currentRole.equals("ROLE_NVPDT")) return "ROLE_NVPDT";
                else if (currentRole.equals("ROLE_GV")) role = "ROLE_GV";
                else if (currentRole.equals("ROLE_TRGDV")) role = "ROLE_TRGDV";
                else if (currentRole.equals("ROLE_SINHVIEN")) role = "ROLE_SINHVIEN";
            }
        }
        return role;
    }

    private void loadData() {
        monHocList.clear();
        // Không cần clearChildren cho ListView
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            messageLabel.setText("Vai trò của user: " + userRole);
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
                messageLabel.setText("Vai trò không hợp lệ, dùng MOMON mặc định!");
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
                        rs.getInt("NAM")
                    );
                    monHocList.add(monHoc);
                    count++;
                }
                messageLabel.setText("Dữ liệu môn học đã được tải! Số dòng: " + count);
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi tải dữ liệu môn học: " + ex.getMessage());
        }
    }

    private void handleAdd() {
        try (Connection conn = DatabaseConnection.getConnection()) { // SỬA: bỏ username, password
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
                    loadData(); // Làm mới giao diện
                    clearFields();
                    messageLabel.setText("Thêm môn học thành công!");
                }
            } else {
                messageLabel.setText("Bạn không có quyền thêm môn học!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi khi thêm môn học");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Học kỳ và Năm phải là số!");
        }
    }

    private void handleUpdate() {
        if (selectedMonHoc == null) {
            messageLabel.setText("Vui lòng chọn một môn học để sửa!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) { // SỬA: bỏ username, password
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
                    messageLabel.setText("Cập nhật môn học thành công!");
                }
            } else {
                messageLabel.setText("Bạn không có quyền sửa môn học!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi khi sửa môn học ");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Học kỳ và Năm phải là số!");
        }
    }

    private void handleDelete() {
        if (selectedMonHoc == null) {
            messageLabel.setText("Vui lòng chọn một môn học để xóa!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) { // SỬA: bỏ username, password
            String userRole = getUserRole(conn);
            String sql = "";
            if (userRole.equals("ROLE_NVPDT")) {
                sql = "DELETE FROM ATBMCQ_ADMIN.MOMON_NVPDT WHERE MAMM = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, mammField.getText().trim());
                    pstmt.executeUpdate();
                    loadData();
                    clearFields();
                    messageLabel.setText("Xóa môn học thành công!");
                }
            } else {
                messageLabel.setText("Bạn không có quyền xóa môn học!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi khi xóa môn học ");
        }
    }

    private void handleRefresh() {
        loadData();
        clearFields();
        messageLabel.setText("Dữ liệu môn học đã được làm mới!");
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
