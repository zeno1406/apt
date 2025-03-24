package BUS;

import DAL.RoleDAL;
import DTO.RoleDTO;
import UTILS.ValidationUtils;

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
    public boolean delete(Integer id, int employee_roleId) {
        if (id == null || id <= 0 || employee_roleId <= 0 || employee_roleId == 1 || !hasPermission(employee_roleId, 24)) {
            return false;
        }

        if (!RoleDAL.getInstance().delete(id)) {
            return false;
        }

        arrLocal.removeIf(role -> role.getId() == id);
        return true;
    }

    public RoleDTO getByIdLocal(int roleId) {
        if (roleId <= 0) return null;
        for (RoleDTO role : arrLocal) {
            if (Objects.equals(role.getId(), roleId)) {
                return new RoleDTO(role);
            }
        }
        return null;
    }

    public boolean insert(RoleDTO obj, int employee_roleId) {
        if (obj == null || employee_roleId <= 0 || !hasPermission(employee_roleId, 23) || !validateRoleInput(obj)) {
            return false;
        }

        if (isDuplicateRoleName(-1, obj.getName()) || !RoleDAL.getInstance().insert(obj)) {
            return false;
        }

        arrLocal.add(new RoleDTO(obj));
        return true;
    }

    public boolean update(RoleDTO obj, int employee_roleId) {
        if (obj == null || obj.getId() <= 0 || employee_roleId <= 0 || employee_roleId == 1 ||  !hasPermission(employee_roleId, 25) || !validateRoleInput(obj)) {
            return false;
        }

        if (isDuplicateRoleName(obj.getId(), obj.getName()) || !RoleDAL.getInstance().update(obj)) {
            return false;
        }

        for (int i = 0; i < arrLocal.size(); i++) {
            if (arrLocal.get(i).getId() == obj.getId()) {
                arrLocal.set(i, new RoleDTO(obj));
                return true;
            }
        }

        return false;
    }

    private boolean isDuplicateRoleName(int id, String name) {
        if (name == null) return false;
        for (RoleDTO role : arrLocal) {
            if (!Objects.equals(role.getId(), id) && Objects.equals(role.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    private boolean validateRoleInput(RoleDTO obj) {
        if (obj.getName() == null || obj.getSalaryCoefficient() == null) return false;

        ValidationUtils validator = ValidationUtils.getInstance();
        return validator.validateVietnameseText50(obj.getName())
                && (obj.getDescription() == null || validator.validateVietnameseText255(obj.getDescription()))
                && validator.validateBigDecimal(obj.getSalaryCoefficient(), 5, 2, false);
    }




}
