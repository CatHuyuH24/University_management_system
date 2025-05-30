package atbmhttt.atbmcq_16;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    public StringProperty usernameProperty() {
        return username;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    private Connection connectToDatabase(String dbUsername, String dbPassword) throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver"); // Ensure the Oracle JDBC driver is loaded
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found. Please include it in your library path.", e);
        }
        String url = "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL"; // Oracle DB URL
        Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
        if (conn != null) {
            System.out.println("Database connection successful.");
            return conn;
        } else {
            return null;
        }
    }

    public boolean isConnectedToDatabase() {
        try (Connection connection = connectToDatabase("ATBMCQ_ADMIN", "123")) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void login() throws SQLException, Exception {
        try {
            if (checkIfAdminAndInitializeDBConnection()) {
                Router.navigateToAdminDashboard(getUsername(), getPassword());
            } else {
                Router.navigateToClientDashboard();
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("LoginViewModel: Exception when logging in");
            throw e;
        }
    }

    public boolean checkIfAdminAndInitializeDBConnection() throws SQLException {
        String sql = "{ call ATBMCQ_ADMIN.IS_ADMIN_USER(?) }"; // Stored procedure call
        try (Connection connection = connectToDatabase(getUsername(), getPassword());
                CallableStatement callableStatement = connection.prepareCall(sql)) {

            // Register the OUT parameter to capture the result
            callableStatement.registerOutParameter(1, java.sql.Types.NUMERIC);

            // Execute the stored procedure
            callableStatement.execute();

            // Retrieve the result
            int result = callableStatement.getInt(1);

            DatabaseConnection.initialize(getUsername(), getPassword());// register username, password for the current
                                                                        // user
            return result == 1; // Return true if the user is an admin

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }
}