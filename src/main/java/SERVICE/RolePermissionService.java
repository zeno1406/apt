package SERVICE;

import BUS.ModuleBUS;
import BUS.PermissionBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DTO.*;

import java.util.ArrayList;

public class RolePermissionService {
    private static final RolePermissionService INSTANCE = new RolePermissionService();

    public static RolePermissionService getInstance() {
        return INSTANCE;
    }

    public boolean createRoleWithPermissions(RoleDTO role) {
        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (roleBus.getAllLocal().isEmpty()) roleBus.loadLocal();
        if (!roleBus.insert(role)) {
            return false;
        }

        if (rolePermissionBus.getAllLocal().isEmpty()) rolePermissionBus.loadLocal();
        if (!rolePermissionBus.createDefaultPermissionsForRole(role.getId())) {
            // Rollback nếu lỗi
            roleBus.delete(role.getId());
            return false;
        }
        return true;
    }

    public boolean deleteRoleWithPermissions(int roleId) {
        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (rolePermissionBus.getAllLocal().isEmpty()) rolePermissionBus.loadLocal();
        ArrayList<RolePermissionDTO> temp = rolePermissionBus.getAllRolePermissionByRoleIdLocal(roleId);

        if (!rolePermissionBus.delete(roleId)) {
            return false;
        }

        if (roleBus.getAllLocal().isEmpty()) roleBus.loadLocal();
        if (!roleBus.delete(roleId)) {
            // Nếu xóa Role thất bại, khôi phục lại RolePermission đã xóa trước đó
            boolean rollbackSuccess = rolePermissionBus.insertRollbackPermission(temp);
            if (!rollbackSuccess) {
                System.err.println("Rollback failed! Some role permissions might be lost.");
            }
            return false;
        }
        return true;
    }

    public void printPermissionsGroupedByModule() {
        ModuleBUS moduleBus = ModuleBUS.getInstance();
        PermissionBUS permissionBus = PermissionBUS.getInstance();

        if (moduleBus.getAllLocal().isEmpty()) moduleBus.loadLocal();
        if (permissionBus.getAllLocal().isEmpty()) permissionBus.loadLocal();

        ArrayList<ModuleDTO> arrModule = moduleBus.getAllLocal();
        ArrayList<PermissionDTO> arrPermission = permissionBus.getAllLocal();

        for (ModuleDTO module : arrModule) {
            System.out.println("Module: " + module.getName());
            for (PermissionDTO permission : arrPermission) {
                if (permission.getModule_id() == module.getId()) {
                    System.out.println("    - " + permission.getName() + " | id: " + permission.getId());
                }
            }
        }
    }
}
