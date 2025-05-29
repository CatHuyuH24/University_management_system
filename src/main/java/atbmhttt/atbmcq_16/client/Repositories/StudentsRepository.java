package atbmhttt.atbmcq_16.client.Repositories;

import atbmhttt.atbmcq_16.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentsRepository {
    public List<String[]> getStudentsWithDetails() throws SQLException {
        String sql = "SELECT MASV, HOTEN, PHAI, NGSINH, DCHI, DT, KHOA, TINHTRANG FROM ATBMCQ_ADMIN.SINHVIEN";
        List<String[]> studentDetails = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.Statement statement = connection.createStatement();
                java.sql.ResultSet resultSet = statement.executeQuery(sql)) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while (resultSet.next()) {
                String masv = resultSet.getString("MASV");
                String hoten = resultSet.getString("HOTEN");
                String phai = resultSet.getString("PHAI");
                java.sql.Date ngsinhDate = resultSet.getDate("NGSINH");
                String ngsinh = ngsinhDate != null ? ngsinhDate.toLocalDate().format(formatter) : "";
                String dchi = resultSet.getString("DCHI");
                if (dchi == null)
                    dchi = "";
                String dt = resultSet.getString("DT");
                if (dt == null)
                    dt = "";
                String khoa = resultSet.getString("KHOA");
                if (khoa == null)
                    khoa = "";
                String tinhtrang = resultSet.getString("TINHTRANG");
                if (tinhtrang == null)
                    tinhtrang = "";
                studentDetails.add(new String[] { masv, hoten, phai, ngsinh, dchi, dt, khoa, tinhtrang });
            }
        } catch (SQLException e) {
            throw e;
        }
        return studentDetails;
    }

    public int updateStudentAttribute(String masv, String cleanedColumnName, String newValue) throws SQLException {
        String sql = "UPDATE ATBMCQ_ADMIN.SINHVIEN SET " + cleanedColumnName + " = ? WHERE MASV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newValue);
            statement.setString(2, masv);
            int rowCount = statement.executeUpdate();
            return rowCount;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            // Any other error (should be rare)
            throw new RuntimeException("Unexpected error during update: " + e.getMessage(), e);
        }
    }
}
