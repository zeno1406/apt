package BUS;

import DAL.EmployeeDAL;
import DAL.RolePermissionDAL;
import DTO.EmployeeDTO;
import DTO.RolePermissionDTO;
import INTERFACE.IBUS;

import java.util.ArrayList;
import java.util.Objects;

public class EmployeeBUS implements IBUS<EmployeeDTO, Integer> {
    private final ArrayList<EmployeeDTO> arrEmployee = new ArrayList<>();
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

    @Override
    public EmployeeDTO getById(Integer id) {
        return EmployeeDAL.getInstance().getById(id);
    }

    @Override
    public boolean insert(EmployeeDTO obj) {
        if (obj == null) return false;
        if (EmployeeDAL.getInstance().insert(obj)) {
            arrEmployee.add(new EmployeeDTO(obj)); // Tạo bản sao để tránh thay đổi từ bên ngoài
            return true;
        }
        return false;
    }

    @Override
    public boolean update(EmployeeDTO obj) {
        if (obj == null || obj.getId() <= 0) return false;

        for (int i = 0; i < arrEmployee.size(); i++) {
            if (Objects.equals(arrEmployee.get(i).getId(), obj.getId())) {
                if (EmployeeDAL.getInstance().update(obj)) {
                    arrEmployee.set(i, new EmployeeDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        if (EmployeeDAL.getInstance().delete(id)) {
            for (EmployeeDTO employee : arrEmployee) {
                if (Objects.equals(employee.getId(), id)) {
                    employee.setStatus(false);
                    return true;
                }
            }
        }
        return false;
    }

    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================

    @Override
    public void loadLocal() {
        arrEmployee.clear();
        arrEmployee.addAll(getAll());
    }

    public ArrayList<EmployeeDTO> getAllEmployeeLocal() {
        return new ArrayList<>(arrEmployee);
    }

    public EmployeeDTO getEmployeeByIdLocal(int id) {
        for (EmployeeDTO employee : arrEmployee) {
            if (Objects.equals(employee.getId(), id)) {
                return new EmployeeDTO(employee);
            }
        }
        return null;
    }

    public boolean isDuplicateEmployeeName(int id, String first_name) {
        if (first_name == null) return false;
        for (EmployeeDTO employee : arrEmployee) {
            if (!Objects.equals(employee.getId(), id) && Objects.equals(employee.getFirstName(), first_name)) {
                return true;
            }
        }
        return false;
    }
}
