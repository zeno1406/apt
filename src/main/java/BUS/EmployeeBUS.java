package BUS;

import DAL.CustomerDAL;
import DAL.EmployeeDAL;
import DTO.CustomerDTO;
import DTO.EmployeeDTO;
import UTILS.ValidationUtils;

import java.math.BigDecimal;
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

    @Override
    public boolean delete(Integer id, int employee_roleId) {
        throw new UnsupportedOperationException("Cannot delete module records.");
    }

    public boolean deleteCustomer(Integer id, int employee_roleId, int employeeLoginId) {
        if (id == null || id <= 0 || id == 1 || id == employeeLoginId || employee_roleId <= 0 || !hasPermission(employee_roleId, 2)) {
            return false;
        }
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

    public boolean insert(EmployeeDTO obj, int employee_roleId) {
        if (obj == null || obj.getRoleId() <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 1) || !isValidEmployeeInput(obj)) {
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
        if (obj == null || obj.getId() == 1 || obj.getId() == employeeLoginId || obj.getId() <= 0 || employee_roleId <= 0 ||
                !hasPermission(employee_roleId, 3) || !isValidEmployeeInput(obj)) {
            return false;
        }

        if (!EmployeeDAL.getInstance().update(obj)) {
            return false;
        }
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                arrLocal.set(i, new EmployeeDTO(obj));
                return true;
            }
        }
        return false;
    }

    private boolean isValidEmployeeInput(EmployeeDTO obj) {
        if (obj.getFirstName() == null || obj.getLastName() == null || obj.getSalary() == null) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();

        return validator.validateVietnameseText100(obj.getFirstName()) &&
                validator.validateVietnameseText100(obj.getLastName()) &&
                validator.validateBigDecimal(obj.getSalary(), 10, 2, false);
    }
}
