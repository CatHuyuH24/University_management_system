package atbmhttt.atbmcq_16.admin.Repositories;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import atbmhttt.atbmcq_16.DatabaseConnection;

public class RolesRepository {

    public List<String[]> getPdbRoles() throws SQLException {
        String sql = "BEGIN ? := ATBMCQ_ADMIN.FN_GET_PDB_ROLES; END;";
        List<String[]> pdbRoles = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                while (resultSet.next()) {
                    String roleName = resultSet.getString(1);
                    pdbRoles.add(new String[] { roleName });
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return pdbRoles;
    }

    public List<String[]> getUserRoles(String username) throws SQLException {
        String sql = "BEGIN ? := ATBMCQ_ADMIN.FN_GET_USER_ROLES(?); END;";
        List<String[]> userRoles = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            statement.setString(2, username);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                while (resultSet.next()) {
                    String roleName = resultSet.getString(1);
                    userRoles.add(new String[] { roleName });
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return userRoles;
    }

    public void deleteRole(String roleName) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_DROP_ROLE(?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, roleName);
            statement.execute();

            System.out.println("Role " + roleName + " has been deleted successfully.");
        } catch (SQLException e) {
            throw e;
        }
    }

    public void addRole(String roleName) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_ADD_ROLE(?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, roleName);
            statement.execute();

            System.out.println("Role " + roleName + " has been added successfully.");
        } catch (SQLException e) {
            throw e;
        }
    }

    public void addRoleToUser(String username, String roleName) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_ADD_ROLE_TO_USER(?, ?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, roleName);
            statement.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void removeRoleFromUser(String username, String roleName) throws SQLException {
        String sql = "BEGIN ATBMCQ_ADMIN.SP_REMOVE_ROLE_FROM_USER(?, ?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, username);
            statement.setString(2, roleName);
            statement.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<String> getGrantedRoles(String roleName) throws SQLException {
        String sql = "BEGIN SP_LIST_GRANTED_ROLES(?, ?); END;";
        List<String> grantedRoles = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, roleName);
            statement.registerOutParameter(2, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(2)) {
                while (resultSet.next()) {
                    grantedRoles.add(resultSet.getString(1));
                }
            }
        }

        return grantedRoles;
    }

    public List<String> getAvailableRoles(String excludeRoleName) throws SQLException {
        String sql = "BEGIN SP_LIST_ROLES_EXCLUDE(?, ?); END;";
        List<String> availableRoles = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, excludeRoleName);
            statement.registerOutParameter(2, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(2)) {
                while (resultSet.next()) {
                    availableRoles.add(resultSet.getString(1));
                }
            }
        }

        return availableRoles;
    }

    public void grantRoleToRole(String grantRole, String targetRole) throws SQLException {
        String sql = "BEGIN SP_GRANT_ROLE_TO_ROLE(?, ?); END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.setString(1, grantRole);
            statement.setString(2, targetRole);
            statement.execute();
        }
    }
}