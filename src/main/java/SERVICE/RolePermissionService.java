package SERVICE;

import BUS.ModuleBUS;
import BUS.PermissionBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DTO.*;
import INTERFACE.ServiceAccessCode;

import java.util.ArrayList;


public class RolePermissionService {
    private static final RolePermissionService INSTANCE = new RolePermissionService();

    public static RolePermissionService getInstance() {
        return INSTANCE;
    }

    public boolean createRoleWithPermissions(RoleDTO role, int employee_roleId, int employeeLoginId) {

        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (roleBus.isLocalEmpty()) roleBus.loadLocal();
        if (!roleBus.insert(role, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId)) {
            System.err.println("Tạo role thất bại");
            return false;
        }

        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();
        if (!rolePermissionBus.createDefaultPermissionsForRole(role.getId(), employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId)) {

            // Rollback nếu lỗi
            roleBus.delete(role.getId(), employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId);
            return false;
        }
        return true;
    }

    public boolean deleteRoleWithPermissions(int roleId, int employee_roleId, int employeeLoginId) {
        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();
        ArrayList<RolePermissionDTO> tempList = rolePermissionBus.getAllRolePermissionByRoleIdLocal(roleId);

        // Xóa role_permission trước
        if (!rolePermissionBus.delete(roleId, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId)) return false;

        if (roleBus.isLocalEmpty()) roleBus.loadLocal();
        RoleDTO temp = roleBus.getByIdLocal(roleId);

        // Xóa role sau
        if (!roleBus.delete(roleId, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId)) {
            System.err.println("Failed to delete role. Attempting to roll back role permissions.");

            // Khôi phục role_permission nếu role không xóa được
            return rolePermissionBus.insertRollbackPermission(tempList, 1, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId);
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
