package atbmhttt.atbmcq_16.admin.Repositories;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import atbmhttt.atbmcq_16.DatabaseConnection;

public class UsersRepository {

    public List<String[]> getUsersWithDetails() throws SQLException {
        String sql = "BEGIN ? := ATBMCQ_ADMIN.FN_GET_USERS; END;";
        List<String[]> userDetails = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                while (resultSet.next()) {
                    String username = resultSet.getString(1);
                    String created = resultSet.getString(2);
                    userDetails.add(new String[] { username, created });
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return userDetails;
    }

    public void addUser(String username, String password) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_ADD_USER_ALLOW_CREATESESSION_ISADMINUSER(?, ?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void changeUserPassword(String username, String newPassword) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_CHANGE_USER_PASSWORD(?, ?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, newPassword);
            statement.execute();

            System.out.println("Password for user " + username + " has been changed successfully.");
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_DROP_USER(?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.execute();

            System.out.println("User " + username + " has been deleted successfully.");
        } catch (SQLException e) {
            throw e;
        }
    }
}