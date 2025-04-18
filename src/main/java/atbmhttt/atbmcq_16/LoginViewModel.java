package atbmhttt.atbmcq_16;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginViewModel {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty("One way binding");

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
        String url = "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL"; // Replace with your Oracle DB URL
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

    public boolean loginSuccessfully(String inputUsername, String inputPassword) {
        try (Connection connection = connectToDatabase(inputUsername, inputPassword)) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Login successful for user: " + inputUsername);
                return true;
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database connection error: " + e.getMessage());
        }

        return false;
    }
}