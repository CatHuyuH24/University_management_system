package atbmhttt.atbmcq_16.admin.ViewModels;

import atbmhttt.atbmcq_16.admin.Repositories.RolesRepository;
import atbmhttt.atbmcq_16.dialogs.AlertDialog;
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

    public void addRole(String roleName) throws SQLException {
        rolesRepository.addRole(roleName);
        roles.add(new String[] { roleName });
    }

    public ObservableList<String> getGrantedRoles(String roleName) {
        try {
            return FXCollections.observableArrayList(rolesRepository.getGrantedRoles(roleName));
        } catch (SQLException e) {
            AlertDialog.showErrorAlert("Error Fetching Granted Roles", null,
                    "An error occurred while fetching granted roles for " + roleName + ".\n" + e.getMessage(), null,
                    400, 200);
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<String> getAvailableRoles(String excludeRoleName) {
        try {
            return FXCollections.observableArrayList(rolesRepository.getAvailableRoles(excludeRoleName));
        } catch (SQLException e) {
            AlertDialog.showErrorAlert("Error Fetching Available Roles", null,
                    "An error occurred while fetching available roles excluding " + excludeRoleName + ".\n"
                            + e.getMessage(),
                    null, 400, 200);
            return FXCollections.observableArrayList();
        }
    }

    public void grantRoleToRole(String grantRole, String targetRole) {
        try {
            rolesRepository.grantRoleToRole(grantRole, targetRole);
            AlertDialog.showInformationAlert("Role Granted Successfully", null,
                    "Role " + grantRole + " has been granted to " + targetRole + ".", null, 400, 200);
        } catch (SQLException e) {
            AlertDialog.showErrorAlert("Error Granting Role", null,
                    "An error occurred while granting role " + grantRole + " to " + targetRole + ".\n" + e.getMessage(),
                    null, 400, 200);
        }
    }
}