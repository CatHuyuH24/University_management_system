package atbmhttt.atbmcq_16.admin.ViewModels;

import java.sql.SQLException;
import java.util.List;

import atbmhttt.atbmcq_16.admin.Repositories.UserPrivilegesRepository;

public class UserPrivilegesViewModel {
    private UserPrivilegesRepository repository = new UserPrivilegesRepository();

    public List<String[]> getUsersWithDetails() throws SQLException {
        return repository.getUsersWithDetails();
    }

    // private void handleUserPrivilegesDetails(String username) {
    // // Logic to handle user privileges details
    // System.out.println("Privileges for user: " + username);
    // }
}
