package atbmhttt.atbmcq_16.admin.ViewModels;

import atbmhttt.atbmcq_16.admin.Models.Privilege;
import atbmhttt.atbmcq_16.admin.Repositories.UserPrivilegesRepository;
import javafx.collections.ObservableList;

public class SingleUserPrivViewModel {
    UserPrivilegesRepository repository = new UserPrivilegesRepository();

    public ObservableList<Privilege> getPrivileges(String username) {
        return repository.getUserPrivileges(username);
    }
    public String revokePrivilege(String grantee, String privilege, String objectName, String columnName) {
        return repository.revokePrivilege(grantee, privilege, objectName, columnName);
    }
}
