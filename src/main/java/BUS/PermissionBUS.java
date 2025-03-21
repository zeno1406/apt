package BUS;

import DAL.PermissionDAL;
import DTO.PermissionDTO;
import INTERFACE.IBUS;

import java.util.ArrayList;
import java.util.Objects;

public class PermissionBUS implements IBUS<PermissionDTO, Integer> {
    private final ArrayList<PermissionDTO> arrPermission = new ArrayList<>();
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
    public PermissionDTO getById(Integer id) {
        throw new UnsupportedOperationException("Cannot get by permission id records.");
    }

    @Override
    public boolean insert(PermissionDTO obj) {
        throw new UnsupportedOperationException("Cannot insert permission records.");
    }

    @Override
    public boolean update(PermissionDTO obj) {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Cannot delete permission records.");
    }

    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================

    @Override
    public void loadLocal() {
        arrPermission.clear();
        arrPermission.addAll(getAll());
    }

    public ArrayList<PermissionDTO> getAllPermissionLocal() {
        return new ArrayList<>(arrPermission);
    }

    public PermissionDTO getPermissionByIdLocal(int id) {
        for (PermissionDTO permission : arrPermission) {
            if (Objects.equals(permission.getId(), id)) {
                return new PermissionDTO (permission);
            }
        }
        return null;
    }

    public boolean isDuplicatePermissionName(int id, String name) {
        if (name == null) return false;
        for (PermissionDTO permission : arrPermission) {
            if (!Objects.equals(permission.getId(), id) && Objects.equals(permission.getName(), name)) {
                return true;
            }
        }
        return false;
    }
}
