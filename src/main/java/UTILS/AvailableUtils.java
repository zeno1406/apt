package UTILS;

import BUS.AccountBUS;
import BUS.PermissionBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import SERVICE.AuthorizationService;

public class AvailableUtils {
    private static final AvailableUtils INSTANCE = new AvailableUtils();

    public static AvailableUtils getInstance() {
        return INSTANCE;
    }

    public boolean isValidRole(int roleId) {
        if (roleId <= 0) return false;

        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();

        return RoleBUS.getInstance().getByIdLocal(roleId) != null && isValidRoleWithPermissions(roleId);
    }

    public boolean isValidForCreateAccount(int employeeId) {
        if (employeeId <= 0) return false;

        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();

        // Nếu nhân viên đã có tài khoản rồi thì không được tạo nữa
        return AccountBUS.getInstance().getByIdLocal(employeeId) == null;
    }

    public boolean isExistAccount(int employeeId) {
        if (employeeId <= 0) return false;

        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();

        // Kiểm tra xem tài khoản có tồn tại khôngữa
        return AccountBUS.getInstance().getByIdLocal(employeeId) != null;
    }

    public boolean isValidRoleWithPermissions(int roleId) {
        if (roleId <= 0) return false;
        if (PermissionBUS.getInstance().isLocalEmpty()) PermissionBUS.getInstance().loadLocal();
        if (RolePermissionBUS.getInstance().isLocalEmpty()) RolePermissionBUS.getInstance().loadLocal();

        int rolePermissionCount = RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(roleId).size();
        int totalPermissions = PermissionBUS.getInstance().getAllLocal().size();
        // Nếu role không có đủ quyền, từ chối ngay
        return (rolePermissionCount == totalPermissions);
    }
}
