package BUS;

import DAL.EmployeeDAL;
import DTO.EmployeeDTO;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;
import UTILS.ValidationUtils;

import java.util.ArrayList;
import java.util.Objects;

public class EmployeeBUS extends BaseBUS <EmployeeDTO, Integer> {
    private static final EmployeeBUS INSTANCE = new EmployeeBUS();

    private EmployeeBUS() {
    }

    public static EmployeeBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<EmployeeDTO> getAll() {
        return EmployeeDAL.getInstance().getAll();
    }

    public EmployeeDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (EmployeeDTO employee : arrLocal) {
            if (Objects.equals(employee.getId(), id)) {
                return new EmployeeDTO(employee);
            }
        }
        return null;
    }

    public boolean delete(Integer id, int employee_roleId, int employeeLoginId) {
        if (id == null || id <= 0) return false;

        // Ngăn chặn tự xóa thông tin employee của chính mình
        if (employeeLoginId == id) return false;

        // Ngăn chặn xóa thông tin nhân viên gốc (id = 1) để bảo vệ hệ thống
        if (id == 1) {
            System.out.println("Không thể xóa nhân viên gốc (employeeId = 1)!");
            return false;
        }

        // Nếu người thực hiện không có quyền 2, từ chối luôn
        if (employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 2)) return false;

        // Nếu employee đang bị xóa có quyền 2, chỉ cho phép role 1 xóa nó
        EmployeeDTO targetEmployee = getByIdLocal(id);
        if (targetEmployee == null) return false;
        if (AuthorizationService.getInstance().hasPermission(targetEmployee.getId(), targetEmployee.getRoleId(), 2) && employee_roleId != 1) return false;
        if (!EmployeeDAL.getInstance().delete(id)) {
            return false;
        }
        for (EmployeeDTO employee : arrLocal) {
            if (Objects.equals(employee.getId(), id)) {
                employee.setStatus(false);
                return true;
            }
        }
        return false;
    }

    public boolean insert(EmployeeDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getRoleId() <= 0 || employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 1) || !isValidEmployeeInput(obj)) {
            return false;
        }
        // image_url và date_of_birth có thể null
        obj.setStatus(true);


        if (!EmployeeDAL.getInstance().insert(obj)) {
            return false;
        }
        arrLocal.add(new EmployeeDTO(obj));
        return true;
    }

    public boolean update(EmployeeDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getId() <= 0 || employee_roleId <= 0) return false;

        // Không có quyền 3 thì không chỉnh chính mình hay người khác
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 3)) {
            return false;
        }

        // Ngăn chặn cập nhật nhân viên gốc nếu không phải chính nó
        if (obj.getId() == 1 && employeeLoginId != 1) {
//            System.out.println("Không thể cập nhật nhân viên gốc (employeeId = 1)!");
            return false;
        }

        // Kiểm tra đang cập nhật chính mình hay người khác
        boolean isSelfUpdate = (employeeLoginId == obj.getId());

        // Role 1: có quyền cao nhất nhưng vẫn không tự sửa role_id & status
        if (employee_roleId == 1) {
            boolean canUpdateAdvanced = !isSelfUpdate; // Chỉ cập nhật full nếu không phải chính mình
            if (isInvalidEmployeeUpdate(obj, canUpdateAdvanced, true)) {
                return false;
            }
            if (!EmployeeDAL.getInstance().updateAdvance(obj, canUpdateAdvanced)) return false;
            updateLocalCache(obj);
            return true;
        }

        // Các role khác
        if (isSelfUpdate) {
            // Chỉ có thể cập nhật basic của chính mình
            if (isInvalidEmployeeUpdate(obj, false, false)) return false;
            if (!EmployeeDAL.getInstance().updateBasic(obj, false)) return false;
        } else {
            // Nếu cập nhật người khác, chỉ được phép nếu người đó có quyền thấp hơn
            if (AuthorizationService.getInstance().hasPermission(obj.getId(), obj.getRoleId(), 3)) return false;
            if (isInvalidEmployeeUpdate(obj, true, false)) return false;
            if (!EmployeeDAL.getInstance().updateBasic(obj, true)) return false;
        }

        updateLocalCache(obj);
        return true;
    }

    // Cập nhật cache local
    private void updateLocalCache(EmployeeDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                arrLocal.set(i, new EmployeeDTO(obj));
                break;
            }
        }
    }

    private boolean isValidEmployeeInput(EmployeeDTO obj) {
        if (obj.getFirstName() == null || obj.getLastName() == null || obj.getSalary() == null) {
            return false;
        }

        if (!AvailableUtils.getInstance().isValidRole(obj.getRoleId())) {
            System.err.println("Không thể cập nhật: RoleId không tồn tại!");
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();

        return validator.validateVietnameseText100(obj.getFirstName()) &&
                validator.validateVietnameseText100(obj.getLastName()) &&
                validator.validateBigDecimal(obj.getSalary(), 10, 2, false);
    }

    private boolean isInvalidEmployeeUpdate(EmployeeDTO obj, boolean allowAdvanceChange, boolean isAdvance) {
        if (obj.getFirstName() == null || obj.getLastName() == null || (isAdvance && obj.getSalary() == null)) {
            return true;
        }

        if (!AvailableUtils.getInstance().isValidRole(obj.getRoleId())) {
            System.err.println("Không thể cập nhật: RoleId không tồn tại!");
            return true;
        }

        if (allowAdvanceChange && obj.getRoleId() <= 0) return true;

        ValidationUtils validator = ValidationUtils.getInstance();

        return !validator.validateVietnameseText100(obj.getFirstName()) ||
                !validator.validateVietnameseText100(obj.getLastName()) ||
                (isAdvance && !validator.validateBigDecimal(obj.getSalary(), 10, 2, false));
    }

    public ArrayList<EmployeeDTO> filterEmployees(String searchBy, String keyword, int roleIdFilter, int statusFilter) {
        ArrayList<EmployeeDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = ""; // Tránh lỗi khi keyword bị null
        if (searchBy == null) searchBy = ""; // Tránh lỗi khi searchBy bị null

        keyword = keyword.trim().toLowerCase();

        for (EmployeeDTO emp : arrLocal) {
            boolean matchesSearch = true;
            boolean matchesRole = (roleIdFilter == -1) || (emp.getRoleId() == roleIdFilter);
            boolean matchesStatus = (statusFilter == -1) || (emp.isStatus() == (statusFilter == 1)); // Sửa lỗi ở đây

            // Kiểm tra null tránh lỗi khi gọi .toLowerCase()
            String firstName = emp.getFirstName() != null ? emp.getFirstName().toLowerCase() : "";
            String lastName = emp.getLastName() != null ? emp.getLastName().toLowerCase() : "";
            String employeeId = String.valueOf(emp.getId());

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã nhân viên" -> matchesSearch = employeeId.contains(keyword);
                    case "Họ đệm" -> matchesSearch = firstName.contains(keyword);
                    case "Tên" -> matchesSearch = lastName.contains(keyword);
                }
            }

            // Chỉ thêm vào danh sách nếu thỏa tất cả điều kiện
            if (matchesSearch && matchesRole && matchesStatus) {
                filteredList.add(emp);
            }
        }

        return filteredList;
    }



}
