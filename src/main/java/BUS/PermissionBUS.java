package BUS;

import DAL.PermissionDAL;
import DTO.PermissionDTO;
import java.util.ArrayList;
import java.util.Objects;

public class PermissionBUS extends BaseBUS <PermissionDTO, Integer> {
    private static final PermissionBUS INSTANCE = new PermissionBUS();

    private PermissionBUS() {
    }

    public static PermissionBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<PermissionDTO> getAll() {
        return PermissionDAL.getInstance().getAll();
    }

    @Override
    public boolean delete(Integer permissionId) {
        throw new UnsupportedOperationException("Cannot delete permission records.");
    }

    public PermissionDTO getByIdLocal(int id) {
        for (PermissionDTO permission : arrLocal) {
            if (Objects.equals(permission.getId(), id)) {
                return new PermissionDTO (permission);
            }
        }
        return null;
    }

    public boolean isDuplicatePermissionName(int id, String name) {
        if (name == null) return false;
        for (PermissionDTO permission : arrLocal) {
            if (!Objects.equals(permission.getId(), id) && Objects.equals(permission.getName(), name)) {
                return true;
            }
        }
        return false;
    }
}
