package BUS;

import DAL.CustomerDAL;
import DAL.EmployeeDAL;
import DTO.CustomerDTO;
import DTO.EmployeeDTO;
import INTERFACE.IBUS;

import java.util.ArrayList;
import java.util.Objects;

public class CustomerBUS implements IBUS<CustomerDTO, Integer> {
    private final ArrayList<CustomerDTO> arrCustomer = new ArrayList<>();
    private static final CustomerBUS INSTANCE = new CustomerBUS();

    private CustomerBUS() {
    }

    public static CustomerBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<CustomerDTO> getAll() {
        return CustomerDAL.getInstance().getAll();
    }

    @Override
    public CustomerDTO getById(Integer id) {
        return CustomerDAL.getInstance().getById(id);
    }

    @Override
    public boolean insert(CustomerDTO obj) {
        if (obj == null) return false;
        if (CustomerDAL.getInstance().insert(obj)) {
            arrCustomer.add(new CustomerDTO(obj));
            return true;
        }
        return false;
    }

    @Override
    public boolean update(CustomerDTO obj) {
        if (obj == null || obj.getId() <= 0) return false;

        for (int i = 0; i < arrCustomer.size(); i++) {
            if (Objects.equals(arrCustomer.get(i).getId(), obj.getId())) {
                if (CustomerDAL.getInstance().update(obj)) {
                    arrCustomer.set(i, new CustomerDTO(obj));
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
            for (CustomerDTO customer : arrCustomer) {
                if (Objects.equals(customer.getId(), id)) {
                    customer.setStatus(false);
                    return true;
                }
            }
        }
        return false;
    }

    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================

    @Override
    public void loadLocal() {
        arrCustomer.clear();
        arrCustomer.addAll(getAll());
    }

    public ArrayList<CustomerDTO> getAllCustomerLocal() {
        return new ArrayList<>(arrCustomer);
    }

    public CustomerDTO getCustomerByIdLocal(int id) {
        for (CustomerDTO customer : arrCustomer) {
            if (Objects.equals(customer.getId(), id)) {
                return new CustomerDTO(customer);
            }
        }
        return null;
    }

    public boolean isDuplicateCustomerName(int id, String first_name) {
        if (first_name == null) return false;
        for (CustomerDTO customer : arrCustomer) {
            if (!Objects.equals(customer.getId(), id) && Objects.equals(customer.getFirstName(), first_name)) {
                return true;
            }
        }
        return false;
    }
}
