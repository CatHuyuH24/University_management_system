package atbmhttt.atbmcq_16.admin.ViewModels;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.scene.control.ButtonType;

public class AdminViewModel {

    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL";
    private static String DB_USER;
    private static String DB_PASSWORD;

    public AdminViewModel(String username, String password) {
        DB_USER = username;
        DB_PASSWORD = password;
    }

    public List<String[]> getUsersWithDetails() {
        String sql = "BEGIN ? := ATBMCQ_ADMIN.FN_GET_USERS; END;";
        List<String[]> userDetails = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                while (resultSet.next()) {
                    // assuming only 2 attributes for each records, username and created_date
                    String username = resultSet.getString(1);
                    String created = resultSet.getString(2);
                    userDetails.add(new String[] { username, created });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userDetails;
    }

    public List<String[]> getPdbRoles() {
        String sql = "BEGIN ? := ATBMCQ_ADMIN.FN_GET_PDB_ROLES; END;";
        List<String[]> pdbRoles = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                while (resultSet.next()) {
                    // assuming only 2 attributes for each record, role name and description
                    String roleName = resultSet.getString(1);
                    pdbRoles.add(new String[] { roleName });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pdbRoles;
    }

    public void addUser(String username, String password) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_ADD_USER_ALLOW_CREATESESSION_ISADMINUSER(?, ?); END;";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding user: " + e.getMessage());
            throw e;
        }
    }

    public String getUsername() {
        return DB_USER;
    }

    public void changeUserPassword(String username, String newPassword) {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_CHANGE_USER_PASSWORD(?, ?); END;";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, newPassword);
            statement.execute();

            System.out.println("Password for user " + username + " has been changed successfully.");
        } catch (SQLException e) {
            System.err.println("Error changing password for user " + username + ": " + e.getMessage());
        }
    }

    public void deleteUser(String username) {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_DROP_USER(?); END;";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.execute();

            System.out.println("User " + username + " has been deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting user " + username + ": " + e.getMessage());
        }
    }

    public boolean confirmAndDeleteUser(String username) {
        // Confirm deletion

        ButtonType response = AlertDialog.showAndGetResultConfirmationAlert(
                "DELETE USER " + username, null,
                "Are you sure you want to delete user " + username + "?",
                null);

        if (ButtonType.OK == response) {
            deleteUser(username);
            return true;
        }
        return false;
    }

    public void deleteRole(String roleName) {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_DROP_ROLE(?); END;";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, roleName);
            statement.execute();

            System.out.println("Role " + roleName + " has been deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting role " + roleName + ": " + e.getMessage());
        }
    }
}