package SERVICE;

import BUS.RoleBUS;
import BUS.RolePermissionBUS;

public class AuthorizationService {
    private static final AuthorizationService INSTANCE = new AuthorizationService();

    public static AuthorizationService getInstance() {
        return INSTANCE;
    }

    public boolean hasPermission(int roleId, int permissionId) {
        if (RolePermissionBUS.getInstance().isLocalEmpty()) RolePermissionBUS.getInstance().loadLocal();
        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();
        return RolePermissionBUS.getInstance().hasPermission(roleId, permissionId) && RoleBUS.getInstance().getByIdLocal(roleId) != null;
    }
}
