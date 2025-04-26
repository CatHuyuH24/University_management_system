package atbmhttt.atbmcq_16.admin.Repositories;

import java.sql.SQLException;
import java.util.List;

public class RolePrivilegesRepository {
    private RolesRepository rolesRepository = new RolesRepository();

    public List<String[]> getPdbRoles() throws SQLException {
        return rolesRepository.getPdbRoles();
    }

}
