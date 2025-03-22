package BUS;

import DAL.RoleDAL;
import DTO.RoleDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class RoleBUS extends BaseBUS<RoleDTO, Integer> {
    private static final RoleBUS INSTANCE = new RoleBUS();

    public static RoleBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<RoleDTO> getAll() {
        return RoleDAL.getInstance().getAll();
    }

    @Override
    public boolean delete(Integer id) {
        if (RoleDAL.getInstance().delete(id)) {
            arrLocal.removeIf(role -> Objects.equals(role.getId(), id));
            return true;
        }
        return false;
    }

    public RoleDTO getByIdLocal(int roleId) {
        for (RoleDTO role : arrLocal) {
            if (Objects.equals(role.getId(), roleId)) {
                return new RoleDTO(role);
            }
        }
        return null;
    }

    public boolean insert(RoleDTO obj) {
        if (RoleDAL.getInstance().insert(obj)) {
            arrLocal.add(new RoleDTO(obj));
            return true;
        }
        return false;
    }

    public boolean update(RoleDTO obj) {
        if (RoleDAL.getInstance().update(obj)) {
            for (int i = 0; i < arrLocal.size(); i++) {
                if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                    arrLocal.set(i, new RoleDTO(obj)); // Tránh sửa trực tiếp
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDuplicateRoleName(int id, String name) {
        if (name == null) return false;
        for (RoleDTO role : arrLocal) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateRoleDescription(int id, String description) {
        if (description == null) return false;
        for (RoleDTO role : arrLocal) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getDescription(), description)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateSalaryCoefficient(int id, BigDecimal salaryCoefficient) {
        if (salaryCoefficient == null) return false;
        for (RoleDTO role : arrLocal) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getSalaryCoefficient(), salaryCoefficient)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateRole(int id, String name, String description, BigDecimal salaryCoefficient) {
        for (RoleDTO role : arrLocal) {
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
