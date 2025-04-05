package BUS;

import DAL.RoleDAL;
import DTO.AccountDTO;
import DTO.RoleDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.ValidationUtils;

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

    public boolean delete(Integer roleId, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.ROLE_PERMISSION_SERVICE || roleId == null || roleId <= 0) return false;

        // Ngăn chặn tự xóa role của chính mình
        if (roleId == employee_roleId) return false;

        // Nếu người thực hiện không có quyền 24, từ chối
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 24)) return false;

        // Nếu role đang bị xóa có quyền 24, chỉ cho phép role 1 xóa nó
        if (AuthorizationService.getInstance().canDeletUpdateRole(roleId, 24) && employee_roleId != 1) return false;

        if (!RoleDAL.getInstance().delete(roleId)) return false;

        arrLocal.removeIf(role -> role.getId() == roleId);
        return true;
    }

    public RoleDTO getByIdLocal(int roleId) {
        if (roleId <= 0) return null;
        if (isLocalEmpty()) loadLocal();
        for (RoleDTO role : arrLocal) {
            if (Objects.equals(role.getId(), roleId)) {
                return new RoleDTO(role);
            }
        }
        return null;
    }

    public int insert(RoleDTO obj, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.ROLE_PERMISSION_SERVICE || obj == null) return 2;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 23)) return 3;
        if (!isValidRoleInput(obj) || isDuplicateRoleName(-1, obj.getName())) return 4;

        if (!RoleDAL.getInstance().insert(obj)) return 5;

        arrLocal.add(new RoleDTO(obj));
        return 1;
    }

    public boolean update(RoleDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getId() <= 0) return false;

        // Kiểm tra đang cập nhật chính role của mình hiện có hay người khác
        boolean isSelfUpdate = employee_roleId == obj.getId();
        boolean isAdmin = (employee_roleId == 1);
        // Không có quyền 25 thì không chỉnh chính role của mình hay người khác
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 25)) return false;

        // Trường hợp role 1 (admin) có quyền cao nhất
        if (isAdmin) {
            // Tự cập nhật bản thân thì truyền id role vào để k kiểm tra trùng lập trên chính tên role đang cập nhật
            if (isValidRoleInput(obj)) return false;
            if (isDuplicateRoleName(isSelfUpdate ? employee_roleId : obj.getId(), obj.getName())) return false;
            return updateRole(obj);
        }

        // Nếu không phải admin, kiểm tra update hợp lệ
        if (!isValidRoleUpdateBasic(obj)) return false;

        // Nếu cập nhật role khác, đảm bảo role đó có quyền thấp hơn
        if (!isSelfUpdate && AuthorizationService.getInstance().canDeletUpdateRole(obj.getId(), 25)) return false;
        // Kiểm tra trùng tên role
        if (isDuplicateRoleName(isSelfUpdate ? employee_roleId : obj.getId(), obj.getName())) return false;

        return updateRoleBasic(obj);
    }

    private boolean updateRole(RoleDTO obj) {
        if (!RoleDAL.getInstance().update(obj)) return false;
        updateLocalCache(obj);
        return true;
    }

    private boolean updateRoleBasic(RoleDTO obj) {
        if (!RoleDAL.getInstance().updateBasic(obj)) return false;
        updateLocalCache(obj);
        return true;
    }

    private void updateLocalCache(RoleDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                arrLocal.set(i, new RoleDTO(obj));
                break;
            }
        }
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

    private boolean isValidRoleInput(RoleDTO obj) {
        if (obj.getName() == null || obj.getSalaryCoefficient() == null) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();

        // Kiểm tra tên
        if (!validator.validateVietnameseText50(obj.getName())) {
            return false;
        }

        // Kiểm tra mô tả nếu có
        if (obj.getDescription() != null && !validator.validateVietnameseText255(obj.getDescription())) {
            return false;
        }

        // Kiểm tra hệ số lương
        return validator.validateBigDecimal(obj.getSalaryCoefficient(), 5, 2, false);
    }

    private boolean isValidRoleUpdateBasic(RoleDTO obj) {
        if (obj.getName() == null) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();

        // Kiểm tra name hợp lệ
        if (!validator.validateVietnameseText100(obj.getName())) {
            return false;
        }

        // Nếu description không null, kiểm tra độ dài
        return obj.getDescription() == null || validator.validateStringLength(obj.getDescription(), 255);
    }

    public ArrayList<RoleDTO> filterRoles(String searchBy, String keyword) {
        ArrayList<RoleDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = "";
        if (searchBy == null) searchBy = "";

        keyword = keyword.trim().toLowerCase();

        for (RoleDTO role : arrLocal) {
            boolean matchesSearch = true;

            String id = String.valueOf(role.getId());
            String name = role.getName() != null ? role.getName().toLowerCase() : "";

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã chức vụ" -> matchesSearch = id.contains(keyword);
                    case "Tên chức vụ" -> matchesSearch = name.contains(keyword);
                }
            }

            if (matchesSearch) {
                filteredList.add(role);
            }
        }

        return filteredList;
    }


}
