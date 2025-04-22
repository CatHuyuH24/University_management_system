package atbmhttt.atbmcq_16;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PrivilegeViewer {

    public String viewPrivileges(String userOrRoleName) {
        StringBuilder result = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL", "ATBMCQ_ADMIN", "123")) {

            String sql = "{CALL VIEW_PRIVILEGES_FOR_USER_OR_ROLE(?)}";
            try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                callableStatement.setString(1, userOrRoleName);
                callableStatement.execute();

                // Append output to result (if needed, fetch from DBMS_OUTPUT)
                result.append("Privileges for ").append(userOrRoleName).append(" retrieved successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.append("Error retrieving privileges: ").append(e.getMessage());
        }
        return result.toString();
    }
}