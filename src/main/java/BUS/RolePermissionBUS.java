package BUS;

import DAL.RolePermissionDAL;
import DTO.RolePermissionDTO;
import INTERFACE.IBUS;

import java.util.ArrayList;
import java.util.Objects;

public class RolePermissionBUS implements IBUS<RolePermissionDTO, Integer> {
    private final ArrayList<RolePermissionDTO> arrRolePermission = new ArrayList<>();
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
    public RolePermissionDTO getById(Integer id) {
        throw new UnsupportedOperationException("Cannot get by role id permission records.");
    }

    @Override
    public boolean insert(RolePermissionDTO obj) {
        throw new UnsupportedOperationException("Cannot insert role permission records.");
    }

    @Override
    public boolean update(RolePermissionDTO obj) {
        if (obj == null || obj.getRoleId() <= 0 || obj.getPermissionId() <= 0) return false;

        for (int i = 0; i < arrRolePermission.size(); i++) {
            if (Objects.equals(arrRolePermission.get(i).getRoleId(), obj.getRoleId())
                    && Objects.equals(arrRolePermission.get(i).getPermissionId(), obj.getPermissionId())) {
                if (RolePermissionDAL.getInstance().update(obj)) {
                    arrRolePermission.set(i, new RolePermissionDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Cannot delete role permission records.");
    }

    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================
    @Override
    public void loadLocal() {
        arrRolePermission.clear();
        arrRolePermission.addAll(getAll());
    }

    public ArrayList<RolePermissionDTO> getAllRolePermissionLocal() {
        return new ArrayList<>(arrRolePermission);
    }

    public RolePermissionDTO getRolePermissionByRoleId(int roleId) {
        for (RolePermissionDTO rp : arrRolePermission) {
            if (Objects.equals(rp.getRoleId(), roleId)) {
                return new RolePermissionDTO(rp);
            }
        }
        return null;
    }

    public ArrayList<RolePermissionDTO> getAllRolePermissionByRoleIdLocal(int roleId) {
        ArrayList<RolePermissionDTO> result = new ArrayList<>();
        for (RolePermissionDTO rp : arrRolePermission) {
            if (Objects.equals(rp.getRoleId(), roleId)) {
                result.add(rp);
            }
        }
        return result;
    }

    public boolean createDefaultPermissionsForRole(int roleId) {
        if (RolePermissionDAL.getInstance().insertDefaultRolePermissionByRoleId(roleId)) {
            // Nếu null tức là có thêm mới thành công nhưng local chưa có => chỉ cần bổ sung thêm không cần tải lại
            ArrayList<RolePermissionDTO> newPermissions = getAllRolePermissionByRoleIdLocal(roleId);
            if (newPermissions.isEmpty()) {
                arrRolePermission.addAll(newPermissions);
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
                arrRolePermission.addAll(rolePermission);
            }
            return true;
        }
        return false;
    }

    public boolean deleteRolePermissionByRoleId(int roleId) {
        if (RolePermissionDAL.getInstance().deleteRolePermissionByRoleId(roleId)) {
            arrRolePermission.removeIf(rp -> Objects.equals(rp.getRoleId(), roleId));
            return true;
        }
        return false;
    }

    public boolean hasPermission(int roleId, int permissionId) {
        for (RolePermissionDTO rp : arrRolePermission) {
            if (Objects.equals(rp.getRoleId(), roleId) && Objects.equals(rp.getPermissionId(), permissionId) && rp.isStatus()) {
                return true;
            }
        }
        return false;
    }
}
