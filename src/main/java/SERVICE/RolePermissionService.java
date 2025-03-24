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

    public boolean createRoleWithPermissions(RoleDTO role, int employee_roleId) {

        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (roleBus.isLocalEmpty()) roleBus.loadLocal();
        if (!roleBus.insert(role, employee_roleId)) {
            return false;
        }

        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();
        if (!rolePermissionBus.createDefaultPermissionsForRole(role.getId(), employee_roleId)) {

            // Rollback nếu lỗi
            roleBus.delete(role.getId(), employee_roleId);
            return false;
        }
        return true;
    }

    public boolean deleteRoleWithPermissions(int roleId, int employee_roleId) {
        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (roleBus.isLocalEmpty()) roleBus.loadLocal();
        RoleDTO temp = roleBus.getByIdLocal(roleId);
        // Xóa role trước
        if (!roleBus.delete(roleId, employee_roleId)) {
            return false;
        }

        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();
        ArrayList<RolePermissionDTO> tempList = rolePermissionBus.getAllRolePermissionByRoleIdLocal(roleId);

        if (!rolePermissionBus.delete(roleId, employee_roleId)) {
            System.err.println("Failed to delete role permissions. Attempting to roll back role.");
            // Thêm lại role
            boolean rollbackRoleSuccess = roleBus.insert(temp, 1);
            if (rollbackRoleSuccess) {
                tempList.get(0).setRoleId(temp.getId());
                return rolePermissionBus.insertRollbackPermission(tempList, 1);

            } else {
                System.err.println("Failed to roll back the role.");
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
