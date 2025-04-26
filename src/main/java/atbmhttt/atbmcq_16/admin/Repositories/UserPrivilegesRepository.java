package atbmhttt.atbmcq_16.admin.Repositories;

import java.sql.SQLException;
import java.util.List;

public class UserPrivilegesRepository {
    private UsersRepository usersRepository = new UsersRepository();

    public List<String[]> getUsersWithDetails() throws SQLException {
        return usersRepository.getUsersWithDetails();
    }
}
