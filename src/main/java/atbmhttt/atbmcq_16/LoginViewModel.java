package atbmhttt.atbmcq_16;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.CallableStatement;

public class LoginViewModel {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final Router router;

    public LoginViewModel(Router router) {
        this.router = router;
    }

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

    // public boolean loginSuccessfully(String inputUsername, String inputPassword)
    // {
    // Connection connection = null;
    // try {
    // connection = connectToDatabase(inputUsername, inputPassword);
    // if (connection != null && !connection.isClosed()) {
    // System.out.println("Login successful for user: " + inputUsername);
    // return true;
    // } else {
    // System.out.println("Invalid username or password.");
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // System.err.println("Database connection error: " + e.getMessage());
    // }

    // return false;
    // }

    public void login() throws SQLException, Exception {
        try {
            if (isAdminUser()) {
                router.navigateToAdminDashboard(getUsername(), getPassword());
            } else {
                router.navigateToClientDashboard();
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("LoginViewModel: Exception when logging in");
            throw e;
        }
    }

    public boolean isAdminUser() throws SQLException {
        String sql = "{ call ATBMCQ_ADMIN.IS_ADMIN_USER(?) }"; // Stored procedure call
        try (Connection connection = connectToDatabase(getUsername(), getPassword());
                CallableStatement callableStatement = connection.prepareCall(sql)) {

            // Register the OUT parameter to capture the result
            callableStatement.registerOutParameter(1, java.sql.Types.NUMERIC);

            // Execute the stored procedure
            callableStatement.execute();

            // Retrieve the result
            int result = callableStatement.getInt(1);
            return result == 1; // Return true if the user is an admin

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }
}