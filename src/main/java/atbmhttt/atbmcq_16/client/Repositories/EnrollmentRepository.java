package atbmhttt.atbmcq_16.client.Repositories;

import atbmhttt.atbmcq_16.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentRepository {
    public List<String[]> getAllEnrollments() throws SQLException {
        String sql = "SELECT * FROM ATBMCQ_ADMIN.DANGKY";
        List<String[]> enrollments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.Statement statement = connection.createStatement();
                java.sql.ResultSet resultSet = statement.executeQuery(sql)) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getString(i + 1);
                }
                enrollments.add(row);
            }
        } catch (SQLException e) {
            throw e;
        }
        return enrollments;
    }

    public void addEnrollment(String masv, String mamm) throws SQLException {
        String sql = "INSERT INTO ATBMCQ_ADMIN.DANGKY (MASV, MAMM) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, masv != null ? masv.toUpperCase() : null);
            statement.setString(2, mamm != null ? mamm.toUpperCase() : null);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
