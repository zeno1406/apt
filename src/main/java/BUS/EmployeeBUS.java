
package BUS;

import DAL.EmployeeDAL;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import SERVICE.AuthorizationService;
import SERVICE.ExcelService;
import UTILS.AvailableUtils;
import UTILS.ValidationUtils;

import java.io.IOException;
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

    public int delete(Integer id, int employee_roleId, int employeeLoginId) {
        if (id == null || id <= 0) return 2;

        // Ng-�n chߦ+n t�+� x+�a th+�ng tin employee c�+�a ch+�nh m+�nh
        if (employeeLoginId == id) return 3;

        // Ng-�n chߦ+n x+�a th+�ng tin nh+�n vi+�n g�+�c (id = 1) -��+� bߦ�o v�+� h�+� th�+�ng
        if (id == 1) {
//            System.out.println("Kh+�ng th�+� x+�a nh+�n vi+�n g�+�c (employeeId = 1)!");
            return 8;
        }

        // Nߦ+u ng���+�i th�+�c hi�+�n kh+�ng c+� quy�+�n 2, t�+� ch�+�i lu+�n
        if (employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 2)) return 4;

        // Nߦ+u employee -�ang b�+� x+�a c+� quy�+�n 2, ch�+� cho ph+�p role 1 x+�a n+�
        EmployeeDTO targetEmployee = getByIdLocal(id);
        if (targetEmployee == null) return 7;
        if (AuthorizationService.getInstance().hasPermission(targetEmployee.getId(), targetEmployee.getRoleId(), 2) && employee_roleId != 1) return 5;
        if (!EmployeeDAL.getInstance().delete(id)) {
            return 6;
        }
        for (EmployeeDTO employee : arrLocal) {
            if (Objects.equals(employee.getId(), id)) {
                employee.setStatus(false);
                break;
            }
        }
        return 1;
    }

    public int insert(EmployeeDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getRoleId() <= 0 || employee_roleId <= 0 || !isValidEmployeeInput(obj)) {
            return 2;
        }

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 1)) return 3;

        if (!EmployeeDAL.getInstance().insert(obj)) {
            return 4;
        }
        arrLocal.add(new EmployeeDTO(obj));
        return 1;
    }

    public int update(EmployeeDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getId() <= 0 || employee_roleId <= 0) return 2;

        // Kh+�ng c+� quy�+�n 3 th+� kh+�ng ch�+�nh ch+�nh m+�nh hay ng���+�i kh+�c
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 3)) {
            return 3;
        }

        // Ng-�n chߦ+n cߦ�p nhߦ�t nh+�n vi+�n g�+�c nߦ+u kh+�ng phߦ�i ch+�nh n+�
        if (obj.getId() == 1 && employeeLoginId != 1) {
//            System.out.println("Kh+�ng th�+� cߦ�p nhߦ�t nh+�n vi+�n g�+�c (employeeId = 1)!");
            return 5;
        }

        // Ki�+�m tra -�ang cߦ�p nhߦ�t ch+�nh m+�nh hay ng���+�i kh+�c
        boolean isSelfUpdate = (employeeLoginId == obj.getId());

        // Role 1: c+� quy�+�n cao nhߦ�t nh��ng vߦ�n kh+�ng t�+� s�+�a role_id & status
        if (employee_roleId == 1) {
            boolean canUpdateAdvanced = !isSelfUpdate; // Ch�+� cߦ�p nhߦ�t full nߦ+u kh+�ng phߦ�i ch+�nh m+�nh
            if (isInvalidEmployeeUpdate(obj, canUpdateAdvanced, true)) {
                return 4;
            }
            if (isDuplicateEmployee(obj)) return 1;
            if (!EmployeeDAL.getInstance().updateAdvance(obj, canUpdateAdvanced)) return 7;
            updateLocalCache(obj);
            return 1;
        }

        // C+�c role kh+�c
        if (isSelfUpdate) {
            // Ch�+� c+� th�+� cߦ�p nhߦ�t basic c�+�a ch+�nh m+�nh
            if (isInvalidEmployeeUpdate(obj, false, false)) return 4;
            if (isDuplicateEmployee(obj)) return 1;
            if (!EmployeeDAL.getInstance().updateBasic(obj, false)) return 7;
        } else {
            // Nߦ+u cߦ�p nhߦ�t ng���+�i kh+�c, ch�+� -榦�+�c ph+�p nߦ+u ng���+�i -�+� c+� quy�+�n thߦ�p h��n
            if (AuthorizationService.getInstance().hasPermission(obj.getId(), getByIdLocal(obj.getId()).getRoleId(), 3)) return 6;
            if (isInvalidEmployeeUpdate(obj, true, false)) return 4;
            if (isDuplicateEmployee(obj)) return 1;
            if (!EmployeeDAL.getInstance().updateBasic(obj, true)) return 7;
        }

        updateLocalCache(obj);
        return 1;
    }

    // Cߦ�p nhߦ�t cache local
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
            return false;
        }

        obj.setDateOfBirth(obj.getDateOfBirth() != null ? obj.getDateOfBirth() : null);

        ValidationUtils validator = ValidationUtils.getInstance();
        if (obj.getDateOfBirth() != null && !validator.validateDateOfBirth(obj.getDateOfBirth())) {
            return false;
        }
        return validator.validateVietnameseText100(obj.getFirstName()) &&
                validator.validateVietnameseText100(obj.getLastName()) &&
                validator.validateSalary(obj.getSalary(), 10, 2, false);
    }

    private boolean isInvalidEmployeeUpdate(EmployeeDTO obj, boolean allowAdvanceChange, boolean isAdvance) {
        if (obj.getFirstName() == null || obj.getLastName() == null || (isAdvance && obj.getSalary() == null)) {
            return true;
        }

        if (allowAdvanceChange && obj.getRoleId() <= 0) return true;
        if (!AvailableUtils.getInstance().isValidRole(obj.getRoleId())) {
            return true;
        }

        obj.setDateOfBirth(obj.getDateOfBirth() != null ? obj.getDateOfBirth() : null);


        ValidationUtils validator = ValidationUtils.getInstance();
        if (obj.getDateOfBirth() != null && !validator.validateDateOfBirth(obj.getDateOfBirth())) {
            return true;
        }

        return !validator.validateVietnameseText100(obj.getFirstName()) ||
                !validator.validateVietnameseText100(obj.getLastName()) ||
                (isAdvance && !validator.validateSalary(obj.getSalary(), 10, 2, false));
    }

    public ArrayList<EmployeeDTO> filterEmployees(String searchBy, String keyword, int roleIdFilter, int statusFilter) {
        ArrayList<EmployeeDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = ""; // Tr+�nh l�+�i khi keyword b�+� null
        if (searchBy == null) searchBy = ""; // Tr+�nh l�+�i khi searchBy b�+� null

        keyword = keyword.trim().toLowerCase();

        for (EmployeeDTO emp : arrLocal) {
            boolean matchesSearch = true;
            boolean matchesRole = (roleIdFilter == -1) || (emp.getRoleId() == roleIdFilter);
            boolean matchesStatus = (statusFilter == -1) || (emp.isStatus() == (statusFilter == 1)); // S�+�a l�+�i �+� -�+�y

            // Ki�+�m tra null tr+�nh l�+�i khi g�+�i .toLowerCase()
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

            if (matchesSearch && matchesRole && matchesStatus) {
                filteredList.add(emp);
            }
        }

        return filteredList;
    }

    public int numEmployeeHasRoleId(int roleId) {
        if (roleId <= 0) return 0;

        int num = 0; // Kh�+�i tߦ�o biߦ+n -�ߦ+m
        for (EmployeeDTO e : arrLocal) {
            if (e.getRoleId() == roleId) {
                num++;
            }
        }
        return num;
    }

    private boolean isDuplicateEmployee(EmployeeDTO obj) {
        EmployeeDTO existingEm = getByIdLocal(obj.getId());
        ValidationUtils validate = ValidationUtils.getInstance();
        // Ki�+�m tra xem t+�n, m+� tߦ�, v+� h�+� s�+� l����ng c+� tr+�ng kh+�ng
        return existingEm != null &&
                Objects.equals(existingEm.getFirstName(), validate.normalizeWhiteSpace(obj.getFirstName())) &&
                Objects.equals(existingEm.getLastName(), validate.normalizeWhiteSpace(obj.getLastName())) &&
                Objects.equals(existingEm.getSalary(), obj.getSalary()) &&
                Objects.equals(existingEm.getDateOfBirth(), obj.getDateOfBirth()) &&
                Objects.equals(existingEm.isStatus(), obj.isStatus()) &&
                Objects.equals(existingEm.getRoleId(), obj.getRoleId());
    }
}