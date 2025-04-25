package atbmhttt.atbmcq_16.admin.ViewModels;

import atbmhttt.atbmcq_16.admin.Repositories.RolesRepository;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class RolesViewModel {
    private final RolesRepository rolesRepository;
    private final SimpleListProperty<String[]> roles;

    public RolesViewModel() {
        this.rolesRepository = new RolesRepository();
        this.roles = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            this.roles.setAll(rolesRepository.getPdbRoles());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String[]> getRoles() {
        return roles.get();
    }

    public void deleteRole(String roleName) throws SQLException {
        rolesRepository.deleteRole(roleName);
        roles.removeIf(role -> role[0].equals(roleName));
    }

    // private void handleEditRole(String roleName) {
    // // Logic to handle editing a role
    // System.out.println("Edit role: " + roleName);
    // }
}