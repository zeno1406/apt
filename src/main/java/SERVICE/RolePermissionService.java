package SERVICE;

import BUS.ModuleBUS;
import BUS.PermissionBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DAL.RoleDAL;
import DTO.*;
import INTERFACE.ServiceAccessCode;
import UTILS.AvailableUtils;

import java.util.ArrayList;


public class RolePermissionService {
    private static final RolePermissionService INSTANCE = new RolePermissionService();

    public static RolePermissionService getInstance() {
        return INSTANCE;
    }

    public int createRoleWithPermissions(RoleDTO role, int employee_roleId, int employeeLoginId) {

        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (roleBus.isLocalEmpty()) roleBus.loadLocal();
        int resultr = roleBus.insert(role, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId);
        if (resultr != 1) {
//            System.err.println("Tạo role thất bại");
            return resultr;
        }

        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();
        if (!rolePermissionBus.createDefaultPermissionsForRole(role.getId(), employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId)) {

            // Rollback nếu lỗi
            roleBus.delete(role.getId(), employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId);
            return 2;
        }
        return 1;
    }

    public int deleteRoleWithPermissions(int roleId, int employee_roleId, int employeeLoginId) {
        RoleBUS roleBus = RoleBUS.getInstance();
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();

        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();
        ArrayList<RolePermissionDTO> tempList = rolePermissionBus.getAllRolePermissionByRoleIdLocal(roleId);

        // Xóa role_permission trước
        if (!AvailableUtils.getInstance().isValidRole(roleId)) {
            return roleBus.delete(roleId, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId) ? 1: 2;
        }

        int resultrp = rolePermissionBus.delete(roleId, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId);
        if (resultrp != 1) return resultrp;

        if (roleBus.isLocalEmpty()) roleBus.loadLocal();

        // Xóa role sau
        if (!roleBus.delete(roleId, employee_roleId, ServiceAccessCode.ROLE_PERMISSION_SERVICE, employeeLoginId)) {
            System.err.println("Failed to delete role. Attempting to roll back role permissions.");

            // Khôi phục role_permission nếu role không xóa được
            return rolePermissionBus.insertRollbackPermission(tempList, 1, ServiceAccessCode.ROLE_PERMISSION_SERVICE, 1) ? 1 : 2;
        }

        return 1;
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
