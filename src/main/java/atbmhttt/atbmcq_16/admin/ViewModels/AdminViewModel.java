package atbmhttt.atbmcq_16.admin.ViewModels;

import java.util.List;

public class AdminViewModel {
    private String username;
    private String password;

    public AdminViewModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<String[]> getUsersWithDetails() {
        // Mock data for demonstration purposes
        return List.of(
            new String[]{"user1", "2025-04-01"},
            new String[]{"user2", "2025-04-15"}
        );
    }

    public List<String[]> getPdbRoles() {
        // Mock data for demonstration purposes
        return List.of(
            new String[]{"role1"},
            new String[]{"role2"}
        );
    }

    public void addUser(String username, String password) {
        // Logic to add a user to the database
        System.out.println("User added: " + username);
    }

    public void changeUserPassword(String username, String newPassword) {
        // Logic to change a user's password in the database
        System.out.println("Password changed for user: " + username);
    }

    public void deleteUser(String username) {
        // Logic to delete a user from the database
        System.out.println("User deleted: " + username);
    }
}