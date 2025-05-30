package atbmhttt.atbmcq_16.client.Views.Employees;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import atbmhttt.atbmcq_16.DatabaseConnection;
import javafx.animation.KeyFrame;
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

public class NhanvienController {
    private final String username;
    private final String password;
    private ListView<Nhanvien> nhanvienListView;
    private TextField manvField, hotenField, phaiField, ngsinhField, luongField, phucapField, dtField, vaitroField, madvField;
    private Label messageLabel;
    private final ObservableList<Nhanvien> nhanvienList = FXCollections.observableArrayList();

    public NhanvienController(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Scene createScene() {
        // Thay TableView bằng ListView
        nhanvienListView = new ListView<>();
        nhanvienListView.setItems(nhanvienList);
        nhanvienListView.setCellFactory(new Callback<ListView<Nhanvien>, javafx.scene.control.ListCell<Nhanvien>>() {
            @Override
            public javafx.scene.control.ListCell<Nhanvien> call(ListView<Nhanvien> param) {
                return new javafx.scene.control.ListCell<Nhanvien>() {
                    @Override
                    protected void updateItem(Nhanvien item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getManv() + " - " + item.getHoten() + " - " + item.getVaitro());
                        }
                    }
                };
            }
        });

        // Tạo TextField
        manvField = new TextField();
        hotenField = new TextField();
        phaiField = new TextField();
        ngsinhField = new TextField();
        ngsinhField.setPromptText("YYYY-MM-DD");
        luongField = new TextField();
        phucapField = new TextField();
        dtField = new TextField();
        vaitroField = new TextField();
        madvField = new TextField();

        // Tạo Label và TextField layout
        HBox inputLayout = new HBox(10);
        inputLayout.getChildren().addAll(
            createInputVBox("Mã NV", manvField),
            createInputVBox("Họ tên", hotenField),
            createInputVBox("Phái", phaiField),
            createInputVBox("Ngày sinh", ngsinhField),
            createInputVBox("Lương", luongField),
            createInputVBox("Phụ cấp", phucapField),
            createInputVBox("Điện thoại", dtField),
            createInputVBox("Vai trò", vaitroField),
            createInputVBox("Mã DV", madvField)
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
        Label titleLabel = new Label("Quản lý Nhân viên");
        titleLabel.setFont(new Font("System Bold", 16));

        // Sắp xếp giao diện
        VBox layout = new VBox(10, titleLabel, nhanvienListView, inputLayout, buttonLayout, messageLabel);
        layout.setPadding(new Insets(10));

        // Load dữ liệu và xử lý chọn dòng
        loadData();
        nhanvienListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                manvField.setText(newSelection.getManv());
                hotenField.setText(newSelection.getHoten());
                phaiField.setText(newSelection.getPhai());
                ngsinhField.setText(newSelection.getNgsinh());
                luongField.setText(newSelection.getLuong() == 0.0 ? "" : String.valueOf(newSelection.getLuong()));
                phucapField.setText(newSelection.getPhucap() == 0.0 ? "" : String.valueOf(newSelection.getPhucap()));
                dtField.setText(newSelection.getDt());
                vaitroField.setText(newSelection.getVaitro());
                madvField.setText(newSelection.getMadv());
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
        String sql = "SELECT ROLE FROM SESSION_ROLES WHERE ROLE IN ('ROLE_NVCB', 'ROLE_TRGDV', 'ROLE_TCHC')";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String currentRole = rs.getString("ROLE");
                if (currentRole.equals("ROLE_TCHC")) {
                    return "ROLE_TCHC";
                } else if (currentRole.equals("ROLE_TRGDV")) {
                    role = "ROLE_TRGDV";
                } else if (currentRole.equals("ROLE_NVCB") && !role.equals("ROLE_TRGDV")) {
                    role = "ROLE_NVCB";
                }
            }
        }
        return role;
    }

    private void loadData() {
        nhanvienList.clear();
        String sql = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            messageLabel.setText("Vai trò của user: " + userRole);

            if (userRole.equals("ROLE_NVCB")) {
                sql = "SELECT MANV, HOTEN, PHAI, TO_CHAR(NGSINH, 'YYYY-MM-DD') AS NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV " +
                      "FROM ATBMCQ_ADMIN.NHANVIEN_NVCB WHERE MANV = ?";
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            nhanvienList.add(new Nhanvien(
                                rs.getString("MANV"),
                                rs.getString("HOTEN"),
                                rs.getString("PHAI"),
                                rs.getString("NGSINH"),
                                rs.getDouble("LUONG"),
                                rs.getDouble("PHUCAP"),
                                rs.getString("DT"),
                                rs.getString("VAITRO"),
                                rs.getString("MADV")
                            ));
                        }
                    }
                }
            } else if (userRole.equals("ROLE_TRGDV")) {
                sql = "SELECT MANV, HOTEN, PHAI, TO_CHAR(NGSINH, 'YYYY-MM-DD') AS NGSINH, DT, VAITRO, MADV " +
                      "FROM ATBMCQ_ADMIN.NHANVIEN_TRGDV";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String manv = rs.getString("MANV");
                        if (manv.equals(username)) {
                            String sqlSelf = "SELECT MANV, HOTEN, PHAI, TO_CHAR(NGSINH, 'YYYY-MM-DD') AS NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV " +
                                             "FROM ATBMCQ_ADMIN.NHANVIEN_NVCB WHERE MANV = ?";
                            try (java.sql.PreparedStatement pstmtSelf = conn.prepareStatement(sqlSelf)) {
                                pstmtSelf.setString(1, username);
                                try (ResultSet rsSelf = pstmtSelf.executeQuery()) {
                                    if (rsSelf.next()) {
                                        nhanvienList.add(new Nhanvien(
                                            rsSelf.getString("MANV"),
                                            rsSelf.getString("HOTEN"),
                                            rsSelf.getString("PHAI"),
                                            rsSelf.getString("NGSINH"),
                                            rsSelf.getDouble("LUONG"),
                                            rsSelf.getDouble("PHUCAP"),
                                            rsSelf.getString("DT"),
                                            rsSelf.getString("VAITRO"),
                                            rsSelf.getString("MADV")
                                        ));
                                    }
                                }
                            }
                        } else {
                            nhanvienList.add(new Nhanvien(
                                rs.getString("MANV"),
                                rs.getString("HOTEN"),
                                rs.getString("PHAI"),
                                rs.getString("NGSINH"),
                                0.0,
                                0.0,
                                rs.getString("DT"),
                                rs.getString("VAITRO"),
                                rs.getString("MADV")
                            ));
                        }
                    }
                }
            } else if (userRole.equals("ROLE_TCHC")) {
                sql = "SELECT MANV, HOTEN, PHAI, TO_CHAR(NGSINH, 'YYYY-MM-DD') AS NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV " +
                      "FROM ATBMCQ_ADMIN.NHANVIEN";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        nhanvienList.add(new Nhanvien(
                            rs.getString("MANV"),
                            rs.getString("HOTEN"),
                            rs.getString("PHAI"),
                            rs.getString("NGSINH"),
                            rs.getDouble("LUONG"),
                            rs.getDouble("PHUCAP"),
                            rs.getString("DT"),
                            rs.getString("VAITRO"),
                            rs.getString("MADV")
                        ));
                    }
                }
            } else {
                messageLabel.setText("Không xác định được vai trò của user!");
                return;
            }
            messageLabel.setText("Dữ liệu đã được tải! Số dòng: " + nhanvienList.size());
            nhanvienListView.setItems(nhanvienList); // Đảm bảo ListView cập nhật
            nhanvienListView.refresh();
            // Bỏ tự động selectFirst để không làm mất selection của người dùng
        } catch (SQLException ex) {
            messageLabel.setText("Không có quyền xem dữ liệu: " + ex.getMessage());
        }
    }

    private void handleAdd() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            if (!userRole.equals("ROLE_TCHC")) {
                messageLabel.setText("Bạn không có quyền thêm nhân viên!");
                return;
            }
            String sql = "INSERT INTO ATBMCQ_ADMIN.NHANVIEN (MANV, HOTEN, PHAI, NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV) " +
                         "VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?)";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, manvField.getText().trim());
                pstmt.setString(2, hotenField.getText().trim());
                pstmt.setString(3, phaiField.getText().trim());
                pstmt.setString(4, ngsinhField.getText().trim());
                pstmt.setDouble(5, Double.parseDouble(luongField.getText().trim()));
                pstmt.setDouble(6, Double.parseDouble(phucapField.getText().trim()));
                pstmt.setString(7, dtField.getText().trim());
                pstmt.setString(8, vaitroField.getText().trim());
                pstmt.setString(9, madvField.getText().trim());
                pstmt.executeUpdate();
                loadData();
                clearFields();
                messageLabel.setText("Thêm nhân viên thành công!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi khi thêm nhân viên: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            messageLabel.setText("Lương/Phụ cấp phải là số!");
        }
    }

    private void handleUpdate() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            String selectedManv = manvField.getText().trim();

            if (userRole.equals("ROLE_TCHC")) {
                String sql = "UPDATE ATBMCQ_ADMIN.NHANVIEN SET HOTEN = ?, PHAI = ?, NGSINH = TO_DATE(?, 'YYYY-MM-DD'), " +
                             "LUONG = ?, PHUCAP = ?, DT = ?, VAITRO = ?, MADV = ? WHERE MANV = ?";
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, hotenField.getText().trim());
                    pstmt.setString(2, phaiField.getText().trim());
                    pstmt.setString(3, ngsinhField.getText().trim());
                    pstmt.setDouble(4, Double.parseDouble(luongField.getText().trim()));
                    pstmt.setDouble(5, Double.parseDouble(phucapField.getText().trim()));
                    pstmt.setString(6, dtField.getText().trim());
                    pstmt.setString(7, vaitroField.getText().trim());
                    pstmt.setString(8, madvField.getText().trim());
                    pstmt.setString(9, selectedManv);
                    pstmt.executeUpdate();
                    loadData();
                    clearFields();
                    messageLabel.setText("Cập nhật nhân viên thành công!");
                }
            } else if (userRole.equals("ROLE_NVCB") || userRole.equals("ROLE_TRGDV")) {
                if (!selectedManv.equals(username)) {
                    messageLabel.setText("Bạn chỉ có thể cập nhật số điện thoại của chính mình!");
                    return;
                }

                String sqlCurrent = "SELECT HOTEN, PHAI, TO_CHAR(NGSINH, 'YYYY-MM-DD') AS NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV " +
                                    "FROM ATBMCQ_ADMIN.NHANVIEN_NVCB WHERE MANV = ?";
                String currentHoten = "", currentPhai = "", currentNgsinh = "", currentVaitro = "", currentMadv = "";
                double currentLuong = 0.0, currentPhucap = 0.0;
                String currentDt = "";
                try (java.sql.PreparedStatement pstmtCurrent = conn.prepareStatement(sqlCurrent)) {
                    pstmtCurrent.setString(1, username);
                    try (ResultSet rs = pstmtCurrent.executeQuery()) {
                        if (rs.next()) {
                            currentHoten = rs.getString("HOTEN");
                            currentPhai = rs.getString("PHAI");
                            currentNgsinh = rs.getString("NGSINH");
                            currentLuong = rs.getDouble("LUONG");
                            currentPhucap = rs.getDouble("PHUCAP");
                            currentDt = rs.getString("DT");
                            currentVaitro = rs.getString("VAITRO");
                            currentMadv = rs.getString("MADV");
                        }
                    }
                }

                boolean hasOtherChanges = false;
                if (!hotenField.getText().trim().equals(currentHoten)) hasOtherChanges = true;
                if (!phaiField.getText().trim().equals(currentPhai)) hasOtherChanges = true;
                if (!ngsinhField.getText().trim().equals(currentNgsinh)) hasOtherChanges = true;
                if (!luongField.getText().trim().isEmpty() && Double.parseDouble(luongField.getText().trim()) != currentLuong) hasOtherChanges = true;
                if (!phucapField.getText().trim().isEmpty() && Double.parseDouble(phucapField.getText().trim()) != currentPhucap) hasOtherChanges = true;
                if (!vaitroField.getText().trim().equals(currentVaitro)) hasOtherChanges = true;
                if (!madvField.getText().trim().equals(currentMadv)) hasOtherChanges = true;

                if (hasOtherChanges) {
                    messageLabel.setText("Bạn không có quyền thay đổi các trường khác ngoài số điện thoại!");
                    return;
                }

                String newDt = dtField.getText().trim();
                if (!newDt.equals(currentDt)) {
                    String sql = "UPDATE ATBMCQ_ADMIN.NHANVIEN_NVCB SET DT = ? WHERE MANV = ?";
                    try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, newDt);
                        pstmt.setString(2, selectedManv);
                        pstmt.executeUpdate();
                        loadData();
                        clearFields();
                        messageLabel.setText("Cập nhật số điện thoại thành công!");
                    }
                } else {
                    messageLabel.setText("Không có thay đổi về số điện thoại!");
                }
            } else {
                messageLabel.setText("Bạn không có quyền cập nhật nhân viên!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi khi cập nhật nhân viên: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            messageLabel.setText("Lương/Phụ cấp phải là số!");
        }
    }

    private void handleDelete() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String userRole = getUserRole(conn);
            if (!userRole.equals("ROLE_TCHC")) {
                messageLabel.setText("Bạn không có quyền xóa nhân viên!");
                return;
            }
            String sql = "DELETE FROM ATBMCQ_ADMIN.NHANVIEN WHERE MANV = ?";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, manvField.getText().trim());
                pstmt.executeUpdate();
                loadData();
                clearFields();
                messageLabel.setText("Xóa nhân viên thành công!");
            }
        } catch (SQLException ex) {
            messageLabel.setText("Lỗi khi xóa nhân viên: " + ex.getMessage());
        }
    }

    private void clearFields() {
        manvField.setText("");
        hotenField.setText("");
        phaiField.setText("");
        ngsinhField.setText("");
        luongField.setText("");
        phucapField.setText("");
        dtField.setText("");
        vaitroField.setText("");
        madvField.setText("");
    }

    private void handleRefresh() {
        loadData();
        clearFields();
        messageLabel.setText("Dữ liệu đã được làm mới!");
    }
}
