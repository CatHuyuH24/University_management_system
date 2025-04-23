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

            // Enable DBMS_OUTPUT
            try (CallableStatement enableDbmsOutput = connection.prepareCall("{CALL DBMS_OUTPUT.ENABLE()}")) {
                enableDbmsOutput.execute();
            }

            // Call the stored procedure
            String sql = "{CALL VIEW_PRIVILEGES_FOR_USER_OR_ROLE(?)}";
            try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                callableStatement.setString(1, userOrRoleName);
                callableStatement.execute();
            }

            // Read DBMS_OUTPUT
            try (CallableStatement getDbmsOutput = connection.prepareCall(
                    "DECLARE "
                            + "    line VARCHAR2(32767); "
                            + "    status INTEGER; "
                            + "BEGIN "
                            + "    LOOP "
                            + "        DBMS_OUTPUT.GET_LINE(line, status); "
                            + "        EXIT WHEN status != 0; "
                            + "        ? := line; "
                            + "    END LOOP; "
                            + "END;")) {

                getDbmsOutput.registerOutParameter(1, java.sql.Types.VARCHAR);

                while (true) {
                    getDbmsOutput.execute();
                    String line = getDbmsOutput.getString(1);
                    if (line == null) {
                        break;
                    }
                    result.append(line).append("\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            result.append("Error retrieving privileges: ").append(e.getMessage());
        }
        return result.toString();
    }
}