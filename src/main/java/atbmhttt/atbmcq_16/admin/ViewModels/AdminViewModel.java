package atbmhttt.atbmcq_16.admin.ViewModels;

import atbmhttt.atbmcq_16.admin.DatabaseConnection;
import atbmhttt.atbmcq_16.admin.Repositories.UsersRepository;
import atbmhttt.atbmcq_16.admin.Repositories.RolesRepository;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
import javafx.scene.control.ButtonType;

import java.sql.SQLException;
import java.util.List;

public class AdminViewModel {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    public AdminViewModel(String username, String password) {
        DatabaseConnection.initialize(username, password);
        this.usersRepository = new UsersRepository();
        this.rolesRepository = new RolesRepository();
    }

    public List<String[]> getUsersWithDetails() throws SQLException {
        return usersRepository.getUsersWithDetails();
    }

    public List<String[]> getPdbRoles() throws SQLException {
        return rolesRepository.getPdbRoles();
    }

    public void addUser(String username, String password) throws SQLException {
        usersRepository.addUser(username, password);
    }

    public void changeUserPassword(String username, String newPassword) throws SQLException {
        usersRepository.changeUserPassword(username, newPassword);
    }

    public void deleteUser(String username) throws SQLException {
        usersRepository.deleteUser(username);
    }

    public boolean confirmAndDeleteUser(String username) throws SQLException {
        ButtonType response = AlertDialog.showAndGetResultConfirmationAlert(
                "DELETE USER " + username, null,
                "Are you sure you want to delete user " + username + "?",
                null);

        if (ButtonType.OK == response) {
            deleteUser(username);
            return true;
        }
        return false;
    }

    public void deleteRole(String roleName) throws SQLException {
        rolesRepository.deleteRole(roleName);
    }
}