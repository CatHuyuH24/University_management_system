package atbmhttt.atbmcq_16.admin.ViewModels;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
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

    public boolean addUser(String username, String password) {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_ADD_USER_ALLOW_CREATESESSION_ISADMINUSER(?, ?); END;";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete user " + username + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> response = confirmationAlert.showAndWait();

        if (response.isPresent() && response.get() == ButtonType.YES) {
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

    public List<String[]> getAllTables() {
        String sql = "SELECT table_name FROM user_tables ORDER BY table_name";
        List<String[]> tables = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             java.sql.Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                tables.add(new String[]{tableName});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public List<String> getColumnsOfTable(String tableName) {
        List<String> columns = new ArrayList<>();
        String sql = "SELECT column_name FROM user_tab_columns WHERE table_name = ? ORDER BY column_id";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.add(resultSet.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    // Lấy danh sách tên đối tượng (table, view, procedure, function)
    public List<String> getAllObjectNames(String type) {
        List<String> names = new ArrayList<>();
        String sql = null;
        switch (type) {
            case "TABLE":
                sql = "SELECT table_name FROM user_tables ORDER BY table_name";
                break;
            case "VIEW":
                sql = "SELECT view_name FROM user_views ORDER BY view_name";
                break;
            case "PROCEDURE":
                sql = "SELECT object_name FROM user_procedures WHERE object_type = 'PROCEDURE' ORDER BY object_name";
                break;
            case "FUNCTION":
                sql = "SELECT object_name FROM user_procedures WHERE object_type = 'FUNCTION' ORDER BY object_name";
                break;
        }
        if (sql == null) return names;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             java.sql.Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                names.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    // Gọi store procedure để cấp quyền cho đối tượng (trừ UPDATE TABLE/VIEW)
    public void grantGeneralPrivilege(String type, String objectName, String privilege, boolean withGrantOption) {
        String sql = "BEGIN grant_privilege( p_privilege => ?, p_object_name => ?, p_object_type => ?, p_grantee => ?, p_with_option => ? ); END;";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.setString(1, privilege);
            statement.setString(2, objectName.startsWith("ATBMCQ_ADMIN.") ? objectName : "ATBMCQ_ADMIN." + objectName);
            statement.setString(3, type);
            statement.setString(4, "ANNU");
            statement.setInt(5, withGrantOption ? 1:0);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gọi store procedure để cấp quyền UPDATE cho các cột đã chọn, có WITH GRANT OPTION
    public void grantUpdatePrivilegeWithOption(String tableName, List<String> columns, boolean withGrantOption) {
        String columnsStr = String.join(",", columns);
        String sql = "BEGIN grant_column_privilege(?,?,?,?,?); END;";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,"UPDATE");
            statement.setString(2, "ATBMCQ_ADMIN." + tableName);
            statement.setString(3, columnsStr);
            statement.setString(4, "ANNU");
            statement.setInt(5, withGrantOption ? 1:0);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}