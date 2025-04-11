package atbmhttt.atbmcq_16;

import javafx.scene.control.Label;
import java.sql.PreparedStatement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

    private Connection connectToDatabase() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver"); // Ensure the Oracle JDBC driver is loaded
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found. Please include it in your library path.", e);
        }
        String url = "jdbc:oracle:thin:@//localhost:1521/ATBMCQ_16_CSDL"; // Replace with your Oracle DB URL
        String user = "ATBMCQ_ADMIN"; // Replace with your Oracle DB username
        String password = "123"; // Replace with your Oracle DB password
        Connection conn = DriverManager.getConnection(url, user, password);
        if (conn != null) {
            System.out.println("OKOK");
            return conn;
        } else {
            return null;
        }
    }

    public boolean isConnectedToDatabase() {
        try (Connection connection = connectToDatabase()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void login(Label usernameLabel) {
        usernameLabel.textProperty().set(username.get());
        try (Connection connection = connectToDatabase()) {
            String query = "SELECT * FROM dba_users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, getUsername());
                statement.setString(2, getPassword());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Login successful for user: " + getUsername());
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database connection error: " + e.getMessage());
        }
    }
}