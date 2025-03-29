package BUS;

import DAL.RolePermissionDAL;
import DTO.RolePermissionDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;

import java.util.ArrayList;
import java.util.Objects;

public class RolePermissionBUS extends BaseBUS <RolePermissionDTO, Integer> {
    private static final RolePermissionBUS INSTANCE = new RolePermissionBUS();

    private RolePermissionBUS() {
    }

    public static RolePermissionBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<RolePermissionDTO> getAll() {
        return RolePermissionDAL.getInstance().getAll();
    }

    public boolean delete(Integer roleId, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.ROLE_PERMISSION_SERVICE || roleId == null || roleId <= 0) {
            System.err.println("1");
            return false;
        }

        // Ngăn chặn tự xóa phân quyền role của chính mình
        if (roleId == employee_roleId) return false;

        // Nếu phân quyền của role đang bị xóa có quyền 24, chỉ cho phép role 1 xóa nó
        if (hasPermission(roleId, 24) && employee_roleId != 1) {
            System.err.println("3");
            return false;
        }

        if (!AvailableUtils.getInstance().isValidRole(roleId)) return false;

        // Nếu người thực hiện không có quyền 24, từ chối
        if (employee_roleId <= 0 || !hasPermission(employee_roleId, 24) || AuthorizationService.getInstance().isInvalidUserRole(employeeLoginId, employee_roleId)) {
            System.err.println("4");
            return false;
        }

        if (!RolePermissionDAL.getInstance().delete(roleId)) {
            System.err.println("5");
            return false;
        }

        arrLocal.removeIf(rp -> rp.getRoleId() == roleId);
        return true;
    }

    public ArrayList<RolePermissionDTO> getAllRolePermissionByRoleIdLocal(int roleId) {
        if (roleId <= 0) return null;
        ArrayList<RolePermissionDTO> result = new ArrayList<>();
        for (RolePermissionDTO rp : arrLocal) {
            if (Objects.equals(rp.getRoleId(), roleId)) {
                result.add(rp);
            }
        }
        return result;
    }

    public boolean update(RolePermissionDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getRoleId() <= 0 || obj.getPermissionId() <= 0) return false;

        // Ngăn chặn tự cập nhật phân quyền của chính mình
        if (obj.getRoleId() == employee_roleId) return false;

        // Kiểm tra nếu role bị cập nhật có quyền 26
        boolean roleHasPermission26 = hasPermission(obj.getRoleId(), 26);

        // Nếu role này có quyền 26 nhưng không phải role 1 đang cập nhật -> Từ chối
        if (roleHasPermission26 && employee_roleId != 1) return false;

        // Nếu người thực hiện không có quyền 26 -> Từ chối
        if (employee_roleId <= 0 || !hasPermission(employee_roleId, 26) || AuthorizationService.getInstance().isInvalidUserRole(employeeLoginId, employee_roleId)) return false;

        if (!RolePermissionDAL.getInstance().update(obj)) {
            return false;
        }

        // Cập nhật trong arrLocal
        for (RolePermissionDTO current : arrLocal) {
            if (current.getRoleId() == obj.getRoleId() && current.getPermissionId() == obj.getPermissionId()) {
                arrLocal.set(arrLocal.indexOf(current), new RolePermissionDTO(obj));
                return true;
            }
        }

        return false;
    }

    public boolean createDefaultPermissionsForRole(int roleId, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.ROLE_PERMISSION_SERVICE || roleId <= 0 || !hasPermission(employee_roleId, 23) || AuthorizationService.getInstance().isInvalidUserRole(employeeLoginId, employee_roleId)) {
            return false;
        }
        if (!RolePermissionDAL.getInstance().insertDefaultRolePermissionByRoleId(roleId)) {
            return false;
        }
        ArrayList<RolePermissionDTO> newPermissions = RolePermissionDAL.getInstance().getAllRolePermissionByRoleId(roleId);
        arrLocal.addAll(newPermissions);
        return true;
    }

    public boolean insertRollbackPermission(ArrayList<RolePermissionDTO> rolePermission, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.ROLE_PERMISSION_SERVICE || rolePermission == null || rolePermission.isEmpty() || !hasPermission(employee_roleId, 24) || AuthorizationService.getInstance().isInvalidUserRole(employeeLoginId, employee_roleId)) {
            return false;
        }
        if (!RolePermissionDAL.getInstance().insertListRolePermission(rolePermission.get(0).getRoleId(), rolePermission)) {
            return false;
        }
        ArrayList<RolePermissionDTO> newPermissions = RolePermissionDAL.getInstance().getAllRolePermissionByRoleId(rolePermission.get(0).getRoleId());
        arrLocal.addAll(newPermissions);
        return true;
    }

    public boolean hasPermission(int roleId, int permissionId) {
        if (roleId <= 0 || permissionId <= 0) return false;
        for (RolePermissionDTO rp : arrLocal) {
            if (Objects.equals(rp.getRoleId(), roleId) && Objects.equals(rp.getPermissionId(), permissionId) && rp.isStatus()) {
                return true;
            }
        }
        return false;
    }
}
