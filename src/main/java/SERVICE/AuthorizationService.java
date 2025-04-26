package SERVICE;

import BUS.*;
import DTO.EmployeeDTO;
import UTILS.AvailableUtils;

public class AuthorizationService {
    private static final AuthorizationService INSTANCE = new AuthorizationService();

    public static AuthorizationService getInstance() {
        return INSTANCE;
    }

    public boolean hasPermission(int employeeId, int roleId, int permissionId) {
        if (employeeId <= 0 || roleId <= 0 || permissionId <= 0) return false;
        // Kiểm tra employeeId đó có role đó thật không và role có tồn tại không và hợp lệ không
        if (isInvalidUserRole(employeeId, roleId) || !AvailableUtils.getInstance().isValidRole(roleId)) return false;

        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();
        if (PermissionBUS.getInstance().isLocalEmpty()) PermissionBUS.getInstance().loadLocal();
        if (RolePermissionBUS.getInstance().isLocalEmpty()) RolePermissionBUS.getInstance().loadLocal();

        // Kiểm tra role có quyền cụ thể không
        return RolePermissionBUS.getInstance().hasPermission(roleId, permissionId);
    }

    public boolean canDeleteUpdateRole(int roleId, int permissionId) {
        if (roleId <= 0 || permissionId <= 0) return false;
        // Nếu role này không có quyền đó => false => trả về true
        return !RolePermissionBUS.getInstance().hasPermission(roleId, permissionId);
    }

    public boolean canDeleteUpdateAccount(int employeeId, int permissionId) {
        if (employeeId <= 0 || permissionId <= 0) return false;
        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();

        EmployeeDTO targetEmployee = EmployeeBUS.getInstance().getByIdLocal(employeeId);
        if (targetEmployee == null) return false;
        // Nếu role của employee tài khoản này không có quyền tương ứng thì có thể xóa
        return !RolePermissionBUS.getInstance().hasPermission(targetEmployee.getRoleId(), permissionId);
    }

    public boolean isInvalidUserRole(int employeeId, int expectedRoleId) {
        if (employeeId <= 0 || expectedRoleId <= 0) return true;
        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();

        EmployeeDTO employee = EmployeeBUS.getInstance().getByIdLocal(employeeId);
        return employee == null || employee.getRoleId() != expectedRoleId;
    }

}
