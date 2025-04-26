package SERVICE;

import BUS.AccountBUS;
import BUS.PermissionBUS;
import BUS.RolePermissionBUS;
import DTO.EmployeeDTO;
import DTO.RolePermissionDTO;

import java.util.HashSet;

public class SessionManagerService {
    private static SessionManagerService instance;
    private EmployeeDTO loggedInEmployee;
    private HashSet<Integer> allowedModules;
    private HashSet<Integer> allowedPermissions;

    private SessionManagerService() {
        allowedModules = new HashSet<>();
        allowedPermissions = new HashSet<>();
    }

    public static SessionManagerService getInstance() {
        if (instance == null) {
            instance = new SessionManagerService();
        }
        return instance;
    }

    public void setLoggedInEmployee(EmployeeDTO employee) {
        this.loggedInEmployee = employee;
        loadPermissions();
    }

    public void logout() {
        this.loggedInEmployee = null;
        allowedModules.clear();
        allowedPermissions.clear();
    }

    public boolean hasModuleAccess(int moduleId) {
        return allowedModules.contains(moduleId);
    }

    public boolean hasPermission(int permissionId) {
        return allowedPermissions.contains(permissionId);
    }

    private void loadPermissions() {
        allowedModules.clear();
        allowedPermissions.clear();
        if (loggedInEmployee == null) return;

        PermissionBUS permissionBUS = PermissionBUS.getInstance();
        RolePermissionBUS rolePermissionBUS = RolePermissionBUS.getInstance();

        if (permissionBUS.isLocalEmpty()) permissionBUS.loadLocal();
        if (rolePermissionBUS.isLocalEmpty()) rolePermissionBUS.loadLocal();

        for (RolePermissionDTO rp : rolePermissionBUS.getAllRolePermissionByRoleIdLocal(loggedInEmployee.getRoleId())) {
            if (rp.isStatus()) {
                allowedPermissions.add(rp.getPermissionId());

                // Chỉ lấy `moduleId` một lần
                int moduleId = permissionBUS.getByIdLocal(rp.getPermissionId()).getModule_id();
                allowedModules.add(moduleId);
            }
        }
    }

    public EmployeeDTO currEmployee() {
        return loggedInEmployee != null ? new EmployeeDTO(loggedInEmployee) : null;
    }

    public int numAllowedModules() {
        return allowedModules.size();
    }

    public int employeeLoginId() {
        return loggedInEmployee.getId();
    }
    public int employeeRoleId() {
        return loggedInEmployee.getRoleId();
    }

    public boolean canManage() {
        for (Integer permissionId : allowedPermissions) {
            if (permissionId != 13 && permissionId != 15) {
                return true;
            }
        }
        return false;
    }

    public boolean canSelling() {
        return hasPermission(13);
    }

    public boolean canImporting() {
        return hasPermission(15);
    }
}
