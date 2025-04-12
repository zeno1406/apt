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

    public int delete(Integer roleId, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.ROLE_PERMISSION_SERVICE || roleId == null || roleId <= 0) {
            return 2;
        }

        // Ngăn chặn tự xóa phân quyền role của chính mình
        if (roleId == employee_roleId) return 3;

        // Nếu người thực hiện không có quyền 24, từ chối
        if (employee_roleId <= 0 || !hasPermission(employee_roleId, 24) || AuthorizationService.getInstance().isInvalidUserRole(employeeLoginId, employee_roleId)) {
            return 4;
        }

        // Nếu phân quyền của role đang bị xóa có quyền 24, chỉ cho phép role 1 xóa nó
        if (hasPermission(roleId, 24) && employee_roleId != 1) {
            return 5;
        }

        if (!AvailableUtils.getInstance().isValidRole(roleId)) return 7;

        if (!RolePermissionDAL.getInstance().delete(roleId)) {
            return 6;
        }

        arrLocal.removeIf(rp -> rp.getRoleId() == roleId);
        return 1;
    }

    public ArrayList<RolePermissionDTO> getAllRolePermissionByRoleIdLocal(int roleId) {
        if (roleId <= 0) return null;
        ArrayList<RolePermissionDTO> result = new ArrayList<>();
        for (RolePermissionDTO rp : arrLocal) {
            if (Objects.equals(rp.getRoleId(), roleId)) {
                // Tạo bản sao sâu của mỗi đối tượng RolePermissionDTO
                result.add(new RolePermissionDTO(rp)); // Giả sử có constructor sao chép trong RolePermissionDTO
            }
        }
        return result;
    }

    public int update(RolePermissionDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getRoleId() <= 0 || obj.getPermissionId() <= 0) return 2;

        // Ngăn chặn tự cập nhật phân quyền của chính mình
        if (obj.getRoleId() == employee_roleId) return 3;

        // Kiểm tra nếu role bị cập nhật có quyền 26
        boolean roleHasPermission26 = hasPermission(obj.getRoleId(), 26);

        // Nếu role này có quyền 26 nhưng không phải role 1 đang cập nhật -> Từ chối
        if (roleHasPermission26 && employee_roleId != 1) return 4;

        // Nếu người thực hiện không có quyền 26 -> Từ chối
        if (employee_roleId <= 0 || !hasPermission(employee_roleId, 26) || AuthorizationService.getInstance().isInvalidUserRole(employeeLoginId, employee_roleId)) return 5;

        if (obj.isStatus() == hasPermission(obj.getRoleId(), obj.getPermissionId())) {
            return 1;
        }

        if (!RolePermissionDAL.getInstance().update(obj)) {
            return 6;
        }

        // Cập nhật trong arrLocal
        for (RolePermissionDTO current : arrLocal) {
            if (current.getRoleId() == obj.getRoleId() && current.getPermissionId() == obj.getPermissionId()) {
                arrLocal.set(arrLocal.indexOf(current), new RolePermissionDTO(obj));
                return 1;
            }
        }

        return 1;
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
