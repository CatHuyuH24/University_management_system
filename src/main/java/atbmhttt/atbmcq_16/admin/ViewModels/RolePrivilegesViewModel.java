package atbmhttt.atbmcq_16.admin.ViewModels;

import java.sql.SQLException;
import java.util.List;

import atbmhttt.atbmcq_16.admin.Repositories.RolePrivilegesRepository;

public class RolePrivilegesViewModel {
    private RolePrivilegesRepository repository = new RolePrivilegesRepository();

    public List<String[]> getPdbRoles() throws SQLException {
        return repository.getPdbRoles();
    }
}
