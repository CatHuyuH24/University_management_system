package atbmhttt.atbmcq_16.admin.ViewModels;

import atbmhttt.atbmcq_16.admin.Models.Privilege;
import atbmhttt.atbmcq_16.admin.Repositories.UserPrivilegesRepository;
import javafx.collections.ObservableList;

public class DetailPrivilegeViewModel {
    UserPrivilegesRepository repository = new UserPrivilegesRepository();

    public ObservableList<Privilege> getPrivileges(String name) {
        return repository.getUserPrivileges(name);
    }

    public String revokePrivilege(String grantee, String privilege, String objectName) {
        return repository.revokePrivilege(grantee, privilege, objectName);
    }
}
