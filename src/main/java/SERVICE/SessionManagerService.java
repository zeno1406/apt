package SERVICE;

import BUS.AccountBUS;
import BUS.PermissionBUS;
import BUS.RolePermissionBUS;
import DTO.EmployeeDTO;
import DTO.RolePermissionDTO;

import java.util.ArrayList;

public class SessionManagerService {
    private static SessionManagerService instance;
    private EmployeeDTO loggedInEmployee;
    private ArrayList<Integer> allowedModules;

    private SessionManagerService() {
        allowedModules = new ArrayList<>();
    }

    public static SessionManagerService getInstance() {
        if (instance == null) {
            instance = new SessionManagerService();
        }
        return instance;
    }

    public void setLoggedInEmployee(EmployeeDTO employee) {
        this.loggedInEmployee = employee;
    }

    public EmployeeDTO getLoggedInEmployee() {
        return loggedInEmployee;
    }

    public void logout() {
        this.loggedInEmployee = null;
        this.allowedModules.clear(); // Xóa quyền khi logout
    }

    public ArrayList<Integer> getAllowedModules() {
        return allowedModules;
    }

    public boolean hasModuleAccess(int moduleId) {
        return allowedModules.contains(moduleId);
    }

    private void loadAllowedModules() {
        allowedModules.clear();
        if (loggedInEmployee == null) return;

        // Load dữ liệu nếu chưa có
        if (PermissionBUS.getInstance().isLocalEmpty()) PermissionBUS.getInstance().loadLocal();
        if (RolePermissionBUS.getInstance().isLocalEmpty()) RolePermissionBUS.getInstance().loadLocal();

        // Lấy danh sách quyền theo role của nhân viên
        ArrayList<RolePermissionDTO> rolePermissions =
                RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(loggedInEmployee.getRoleId());

        for (RolePermissionDTO rp : rolePermissions) {
            if (rp.isStatus()) { // Chỉ lấy quyền có trạng thái true
                int moduleId = PermissionBUS.getInstance().getByIdLocal(rp.getPermissionId()).getModule_id();
                if (!allowedModules.contains(moduleId)) {
                    allowedModules.add(moduleId);
                }
            }
        }
    }
}
