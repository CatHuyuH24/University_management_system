package atbmhttt.atbmcq_16.client.Repositories;

import atbmhttt.atbmcq_16.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentAndGradesRepository {
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
            statement.setString(1, masv);
            statement.setString(2, mamm);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public int deleteEnrollment(String masv, String mamm) throws SQLException {
        String sql = "DELETE FROM ATBMCQ_ADMIN.DANGKY WHERE MASV = ? AND MAMM = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, masv);
            statement.setString(2, mamm);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public int updateGrades(String masv, String mamm, Double diemth, Double diemqt, Double diemck, Double diemtk)
            throws SQLException {
        String sql = "UPDATE ATBMCQ_ADMIN.DANGKY SET DIEMTH = ?, DIEMQT = ?, DIEMCK = ?, DIEMTK = ? WHERE MASV = ? AND MAMM = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableDouble(statement, 1, diemth);
            setNullableDouble(statement, 2, diemqt);
            setNullableDouble(statement, 3, diemck);
            setNullableDouble(statement, 4, diemtk);
            statement.setString(5, masv);
            statement.setString(6, mamm);
            return statement.executeUpdate();
        }
    }

    private void setNullableDouble(java.sql.PreparedStatement stmt, int idx, Double value) throws SQLException {
        if (value == null) {
            stmt.setNull(idx, java.sql.Types.NUMERIC);
        } else {
            stmt.setDouble(idx, value);
        }
    }
}
