package BUS;

import DAL.RolePermissionDAL;
import DTO.RolePermissionDTO;

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

    @Override
    public boolean delete(Integer roleId) {
        if (RolePermissionDAL.getInstance().deleteRolePermissionByRoleId(roleId)) {
            arrLocal.removeIf(rp -> Objects.equals(rp.getRoleId(), roleId));
            return true;
        }
        return false;
    }

    public ArrayList<RolePermissionDTO> getAllRolePermissionByRoleIdLocal(int roleId) {
        ArrayList<RolePermissionDTO> result = new ArrayList<>();
        for (RolePermissionDTO rp : arrLocal) {
            if (Objects.equals(rp.getRoleId(), roleId)) {
                result.add(rp);
            }
        }
        return result;
    }

    public boolean update(RolePermissionDTO obj) {
        if (obj == null || obj.getRoleId() <= 0 || obj.getPermissionId() <= 0) return false;

        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getRoleId(), obj.getRoleId())
                    && Objects.equals(arrLocal.get(i).getPermissionId(), obj.getPermissionId())) {
                if (RolePermissionDAL.getInstance().update(obj)) {
                    arrLocal.set(i, new RolePermissionDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    public boolean createDefaultPermissionsForRole(int roleId) {
        if (RolePermissionDAL.getInstance().insertDefaultRolePermissionByRoleId(roleId)) {
            // Nếu null tức là có thêm mới thành công nhưng local chưa có => chỉ cần bổ sung thêm không cần tải lại
            ArrayList<RolePermissionDTO> newPermissions = getAllRolePermissionByRoleIdLocal(roleId);
            if (newPermissions.isEmpty()) {
                arrLocal.addAll(new ArrayList<>(newPermissions));
            }
            return true;
        }
        return false;
    }

    public boolean insertRollbackPermission(ArrayList<RolePermissionDTO> rolePermission) {
        if (rolePermission == null || rolePermission.isEmpty()) {
            return false; // Không có gì để khôi phục
        }

        if (RolePermissionDAL.getInstance().insertRollbackPermission(rolePermission)) {
            // Nếu null tức là có thêm mới thành công nhưng local chưa có => chỉ cần bổ sung thêm không cần tải lại
            if (getAllRolePermissionByRoleIdLocal(rolePermission.get(0).getRoleId()).isEmpty()) {
                arrLocal.addAll(new ArrayList<>(rolePermission));
            }
            return true;
        }
        return false;
    }

    public boolean hasPermission(int roleId, int permissionId) {
        for (RolePermissionDTO rp : arrLocal) {
            if (Objects.equals(rp.getRoleId(), roleId) && Objects.equals(rp.getPermissionId(), permissionId) && rp.isStatus()) {
                return true;
            }
        }
        return false;
    }
}
