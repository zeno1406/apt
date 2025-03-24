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
    public boolean delete(Integer roleId, int employee_roleId) {
        if (roleId == null || roleId <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 24)) {
            return false;
        }
        if (!RolePermissionDAL.getInstance().delete(roleId)) {
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

    public boolean update(RolePermissionDTO obj, int employee_roleId) {
        if (obj == null || obj.getRoleId() <= 0 || obj.getRoleId() == 1  || obj.getPermissionId() <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 26)) {
            return false;
        }
        if (!RolePermissionDAL.getInstance().update(obj)) {
            return false;
        }
        for (int i = 0; i < arrLocal.size(); i++) {
            RolePermissionDTO current = arrLocal.get(i);
            if (current.getRoleId() == obj.getRoleId() && current.getPermissionId() == obj.getPermissionId()) {
                arrLocal.set(i, new RolePermissionDTO(obj));
                return true;
            }
        }
        return false;
    }

    public boolean createDefaultPermissionsForRole(int roleId, int employee_roleId) {
        if (roleId <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 23)) {
            return false;
        }
        if (!RolePermissionDAL.getInstance().insertDefaultRolePermissionByRoleId(roleId)) {
            return false;
        }
        ArrayList<RolePermissionDTO> newPermissions = RolePermissionDAL.getInstance().getAllRolePermissionByRoleId(roleId);
        arrLocal.addAll(newPermissions);
        return true;
    }

    public boolean insertRollbackPermission(ArrayList<RolePermissionDTO> rolePermission, int employee_roleId) {
        if (rolePermission == null || rolePermission.isEmpty() || employee_roleId <= 0 || !hasPermission(employee_roleId, 24)) {
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
