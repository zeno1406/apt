package BUS;

import DAL.RoleDAL;
import DTO.RoleDTO;
import INTERFACE.IBUS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class RoleBUS implements IBUS<RoleDTO, Integer> {
    private final ArrayList<RoleDTO> arrRole = new ArrayList<>();
    private static final RoleBUS INSTANCE = new RoleBUS();

    public static RoleBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<RoleDTO> getAll() {
        return RoleDAL.getInstance().getAll();
    }

    @Override
    public RoleDTO getById(Integer id) {
        return RoleDAL.getInstance().getById(id);
    }

    @Override
    public boolean insert(RoleDTO obj) {
        if (RoleDAL.getInstance().insert(obj)) {
            arrRole.add(new RoleDTO(obj));
            return true;
        }
        return false;
    }

    @Override
    public boolean update(RoleDTO obj) {
        if (RoleDAL.getInstance().update(obj)) {
            for (int i = 0; i < arrRole.size(); i++) {
                if (Objects.equals(arrRole.get(i).getId(), obj.getId())) {
                    arrRole.set(i, new RoleDTO(obj)); // Tránh sửa trực tiếp
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (RoleDAL.getInstance().delete(id)) {
            arrRole.removeIf(role -> Objects.equals(role.getId(), id));
            return true;
        }
        return false;
    }
    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================
    @Override
    public void loadLocal() {
        arrRole.clear();
        arrRole.addAll(getAll());
    }

    public ArrayList<RoleDTO> getAllRoleLocal() {
        return new ArrayList<>(arrRole);
    }

    public RoleDTO getRoleByIdLocal(int id) {
        for (RoleDTO role : arrRole) {
            if (Objects.equals(role.getId(), id)) {
                return new RoleDTO(role);
            }
        }
        return null;
    }

    public boolean isDuplicateRoleName(int id, String name) {
        if (name == null) return false;
        for (RoleDTO role : arrRole) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateRoleDescription(int id, String description) {
        if (description == null) return false;
        for (RoleDTO role : arrRole) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getDescription(), description)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateSalaryCoefficient(int id, BigDecimal salaryCoefficient) {
        if (salaryCoefficient == null) return false;
        for (RoleDTO role : arrRole) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getSalaryCoefficient(), salaryCoefficient)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateRole(int id, String name, String description, BigDecimal salaryCoefficient) {
        for (RoleDTO role : arrRole) {
            if (!Objects.equals(role.getId(), id) &&
                    Objects.equals(role.getName(), name) &&
                    Objects.equals(role.getDescription(), description) &&
                    Objects.equals(role.getSalaryCoefficient(), salaryCoefficient)) {
                return true;
            }
        }
        return false;
    }


}
