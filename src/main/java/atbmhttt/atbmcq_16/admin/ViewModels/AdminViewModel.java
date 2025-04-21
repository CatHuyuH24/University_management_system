package atbmhttt.atbmcq_16.admin.ViewModels;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminViewModel {

    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL";
    private static String DB_USER;
    private static String DB_PASSWORD;

    public AdminViewModel(String username, String password) {
        DB_USER = username;
        DB_PASSWORD = password;
    }

    public List<String> getUsers() {
        List<String> users = new ArrayList<>();
        String sql = "BEGIN ? := ATBMCQ_ADMIN.FN_GET_USERS; END;";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                CallableStatement statement = connection.prepareCall(sql)) {

            statement.registerOutParameter(1, java.sql.Types.REF_CURSOR);
            statement.execute();

            try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                while (resultSet.next()) {
                    users.add(resultSet.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public String getUsername() {
        return DB_USER;
    }
}