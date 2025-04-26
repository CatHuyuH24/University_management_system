package atbmhttt.atbmcq_16.admin.ViewModels;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import atbmhttt.atbmcq_16.DatabaseConnection;

public class GrantPrivilegesViewModel {
    private String username;

    public GrantPrivilegesViewModel(String username) {
        this.username = username;
    }

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
        if (sql == null)
            return names;
        try (Connection connection = DatabaseConnection.getConnection();
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

    public void grantGeneralPrivilege(String type, String objectName, String privilege, boolean withGrantOption) {
        String sql = "BEGIN grant_privilege( p_privilege => ?, p_object_name => ?, p_object_type => ?, p_grantee => ?, p_with_option => ? ); END;";
        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {
            statement.setString(1, privilege);
            statement.setString(2, objectName.startsWith("ATBMCQ_ADMIN.") ? objectName : "ATBMCQ_ADMIN." + objectName);
            statement.setString(3, type);
            statement.setString(4, username);
            statement.setInt(5, withGrantOption ? 1 : 0);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getColumnsOfTable(String tableName) {
        List<String> columns = new ArrayList<>();
        String sql = "SELECT column_name FROM user_tab_columns WHERE table_name = ? ORDER BY column_id";
        try (Connection connection = DatabaseConnection.getConnection();
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

    public void grantUpdatePrivilegeWithOption(String tableName, List<String> columns, boolean withGrantOption) {
        String columnsStr = String.join(",", columns);
        String sql = "BEGIN grant_column_privilege(?,?,?,?,?); END;";
        try (Connection connection = DatabaseConnection.getConnection();
                java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "UPDATE");
            statement.setString(2, "ATBMCQ_ADMIN." + tableName);
            statement.setString(3, columnsStr);
            statement.setString(4, username);
            statement.setInt(5, withGrantOption ? 1 : 0);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
