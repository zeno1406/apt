package BUS;

import DAL.EmployeeDAL;
import DTO.EmployeeDTO;
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

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        if (EmployeeDAL.getInstance().delete(id)) {
            for (EmployeeDTO employee : arrLocal) {
                if (Objects.equals(employee.getId(), id)) {
                    employee.setStatus(false);
                    return true;
                }
            }
        }
        return false;
    }

    public EmployeeDTO getByIdLocal(int id) {
        for (EmployeeDTO employee : arrLocal) {
            if (Objects.equals(employee.getId(), id)) {
                return new EmployeeDTO(employee);
            }
        }
        return null;
    }

    public boolean insert(EmployeeDTO obj) {
        if (obj == null) return false;
        if (EmployeeDAL.getInstance().insert(obj)) {
            arrLocal.add(new EmployeeDTO(obj));
            return true;
        }
        return false;
    }

    public boolean update(EmployeeDTO obj) {
        if (obj == null || obj.getId() <= 0) return false;

        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                if (EmployeeDAL.getInstance().update(obj)) {
                    arrLocal.set(i, new EmployeeDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    public boolean isDuplicateEmployeeName(int id, String first_name) {
        if (first_name == null) return false;
        for (EmployeeDTO employee : arrLocal) {
            if (!Objects.equals(employee.getId(), id) && Objects.equals(employee.getFirstName(), first_name)) {
                return true;
            }
        }
        return false;
    }
}
