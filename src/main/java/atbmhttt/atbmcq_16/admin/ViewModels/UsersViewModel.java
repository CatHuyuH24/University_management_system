package atbmhttt.atbmcq_16.admin.ViewModels;

import atbmhttt.atbmcq_16.admin.Repositories.UsersRepository;
import java.sql.SQLException;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UsersViewModel {
    private final UsersRepository usersRepository;
    private final SimpleListProperty<String[]> users;

    public UsersViewModel() {
        this.usersRepository = new UsersRepository();
        this.users = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            this.users.setAll(usersRepository.getUsersWithDetails());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String[]> getUsers() {
        return users.get();
    }

    public void addUser(String username, String password) throws SQLException {
        usersRepository.addUser(username, password);
        String currentDateTime = java.time.LocalDateTime
                .now().format(
                        java.time.format.DateTimeFormatter
                                .ofPattern("yyyy-MM-dd HH:mm:ss"));
        users.add(new String[] { username, currentDateTime });
    }

    public void deleteUser(String username) throws SQLException {
        usersRepository.deleteUser(username);
        users.removeIf(user -> user[0].equals(username));
    }

    public void updateUserPassword(String username, String newPassword) throws SQLException {
        usersRepository.changeUserPassword(username, newPassword);
    }
}