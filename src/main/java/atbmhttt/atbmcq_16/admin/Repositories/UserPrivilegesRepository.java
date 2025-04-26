package atbmhttt.atbmcq_16.admin.Repositories;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import atbmhttt.atbmcq_16.DatabaseConnection;
import atbmhttt.atbmcq_16.admin.Models.Privilege;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserPrivilegesRepository {
    private UsersRepository usersRepository = new UsersRepository();

    public List<String[]> getUsersWithDetails() throws SQLException {
        return usersRepository.getUsersWithDetails();
    }

    public ObservableList<Privilege> getUserPrivileges(String username) {
        return new PrivilegeViewer().viewPrivileges(username);
    }

    private class PrivilegeViewer {

        private ObservableList<Privilege> viewPrivileges(String userOrRoleName) {
            ObservableList<Privilege> privileges = FXCollections.observableArrayList();

            try (Connection connection = DatabaseConnection.getConnection()) {

                String sql = "{CALL VIEW_PRIVILEGES(?, ?)}";
                try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                    callableStatement.setString(1, userOrRoleName);
                    callableStatement.registerOutParameter(2, java.sql.Types.REF_CURSOR);

                    callableStatement.execute();

                    try (ResultSet resultSet = (ResultSet) callableStatement.getObject(2)) {
                        while (resultSet.next()) {
                            String type = resultSet.getString("PRIVILEGE_TYPE");
                            String privilege = resultSet.getString("PRIVILEGE");
                            String object = resultSet.getString("OBJECT_NAME");
                            String column = resultSet.getString("COLUMN_NAME");
                            String grantOption = resultSet.getString("GRANT_OPTION");

                            privileges.add(new Privilege(type, privilege, object, column, grantOption));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return privileges;
        }
    }

    public String revokePrivilege(String grantee, String privilege, String objectName, String columnName) {
        String result = "";
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "{CALL REVOKE_PRIVILEGE(?, ?, ?, ?, ?)}";
            try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                callableStatement.setString(1, grantee);
                callableStatement.setString(2, privilege);
                callableStatement.setString(3, objectName);
                callableStatement.setString(4, columnName);
                callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);

                callableStatement.execute();
                result = callableStatement.getString(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error: " + e.getMessage();
        }
        return result;
    }
}
