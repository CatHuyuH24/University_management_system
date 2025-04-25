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
}