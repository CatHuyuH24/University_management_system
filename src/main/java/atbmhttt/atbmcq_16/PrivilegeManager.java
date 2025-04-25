package atbmhttt.atbmcq_16;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PrivilegeManager {

    public void revokePrivilege(String privilege, String grantee, String objectName, String columnName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL", "ATBMCQ_ADMIN", "123")) {

            String sql = "{CALL REVOKE_PRIVILEGE(?, ?, ?, ?, ?)}";
            try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                callableStatement.setString(1, grantee);
                callableStatement.setString(2, privilege);
                callableStatement.setString(3, objectName);
                callableStatement.setString(4, columnName);
                callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);

                callableStatement.execute();

                String result = callableStatement.getString(5);
                if (!result.startsWith("Privilege revoked successfully")) {
                    throw new SQLException(result);
                }
            }
        }
    }
}