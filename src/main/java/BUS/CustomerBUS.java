package BUS;

import DAL.CustomerDAL;
import DTO.CustomerDTO;
import java.util.ArrayList;
import java.util.Objects;

public class CustomerBUS extends BaseBUS <CustomerDTO, Integer> {
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

    public CustomerDTO getByIdLocal(int id) {
        for (CustomerDTO customer : arrLocal) {
            if (Objects.equals(customer.getId(), id)) {
                return new CustomerDTO(customer);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        if (CustomerDAL.getInstance().delete(id)) {
            for (CustomerDTO customer : arrLocal) {
                if (Objects.equals(customer.getId(), id)) {
                    customer.setStatus(false);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean insert(CustomerDTO obj) {
        if (obj == null) return false;
        if (CustomerDAL.getInstance().insert(obj)) {
            arrLocal.add(new CustomerDTO(obj));
            return true;
        }
        return false;
    }

    public boolean update(CustomerDTO obj) {
        if (obj == null || obj.getId() <= 0) return false;

        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                if (CustomerDAL.getInstance().update(obj)) {
                    arrLocal.set(i, new CustomerDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isDuplicateCustomerName(int id, String first_name) {
        if (first_name == null) return false;
        for (CustomerDTO customer : arrLocal) {
            if (!Objects.equals(customer.getId(), id) && Objects.equals(customer.getFirstName(), first_name)) {
                return true;
            }
        }
        return false;
    }
}
