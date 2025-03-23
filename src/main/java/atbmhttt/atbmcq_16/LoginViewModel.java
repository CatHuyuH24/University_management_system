package atbmhttt.atbmcq_16;

import javafx.scene.control.Label;

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

    public void login(Label usernameLabel, Label passwordLabel) {
        usernameLabel.textProperty().set(password.get());
        passwordLabel.textProperty().set(username.get());
        // Add login logic here (e.g., validate username and password)
        System.out.println("Logging in with username: " + getUsername() + " and password: " + getPassword());
    }
}