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

    public PermissionDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (PermissionDTO permission : arrLocal) {
            if (Objects.equals(permission.getId(), id)) {
                return new PermissionDTO (permission);
            }
        }
        return null;
    }
}
