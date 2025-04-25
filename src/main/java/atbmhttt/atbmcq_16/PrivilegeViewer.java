package atbmhttt.atbmcq_16;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PrivilegeViewer {

    public ObservableList<Privilege> viewPrivileges(String userOrRoleName) {
        ObservableList<Privilege> privileges = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL", "ATBMCQ_ADMIN", "123")) {

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