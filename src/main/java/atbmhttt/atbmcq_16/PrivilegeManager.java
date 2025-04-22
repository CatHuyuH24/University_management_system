package atbmhttt.atbmcq_16;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PrivilegeManager {

    public void revokePrivilege(String privilegeName, String granteeName) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL", "ATBMCQ_ADMIN", "123")) {

            String sql = "{CALL REVOKE_PRIVILEGE_FROM_USER_OR_ROLE(?, ?)}";
            try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                callableStatement.setString(1, privilegeName);
                callableStatement.setString(2, granteeName);
                callableStatement.execute();
                System.out.println("Privilege " + privilegeName + " revoked from " + granteeName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error revoking privilege: " + e.getMessage());
        }
    }
}