package BUS;

import DAL.CustomerDAL;
import DTO.CustomerDTO;
import UTILS.ValidationUtils;

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
        if (id <= 0) return null;
        for (CustomerDTO customer : arrLocal) {
            if (Objects.equals(customer.getId(), id)) {
                return new CustomerDTO(customer);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Integer id, int employee_roleId) {
        if (id == null || id <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 5)) {
            return false;
        }
        if (!CustomerDAL.getInstance().delete(id)) {
            return false;
        }
        for (CustomerDTO customer : arrLocal) {
            if (Objects.equals(customer.getId(), id)) {
                customer.setStatus(false);
                return true;
            }
        }
        return false;
    }

    public boolean insert(CustomerDTO obj, int employee_roleId) {
        if (obj == null || employee_roleId <= 0 || !hasPermission(employee_roleId, 4) || !isValidCustomerInput(obj)) {
            return false;
        }

        // image_url và date_of_birth có thể null
        obj.setStatus(true);

        if (isDuplicateCustomer(-1, obj.getFirstName(), obj.getLastName(), obj.getPhone(), obj.getAddress()) ||
                !CustomerDAL.getInstance().insert(obj)) {
            return false;
        }

        arrLocal.add(new CustomerDTO(obj));
        return true;
    }


    public boolean update(CustomerDTO obj, int employee_roleId) {
        if (obj == null || obj.getId() <= 0 || employee_roleId <= 0 ||
                !hasPermission(employee_roleId, 6) || !isValidCustomerInput(obj)) {
            return false;
        }

        // Kiểm tra trùng lặp trước khi cập nhật
        if (isDuplicateCustomer(obj.getId(), obj.getFirstName(), obj.getLastName(), obj.getPhone(), obj.getAddress())) {
            return false;
        }

        // Thực hiện update trong database
        if (!CustomerDAL.getInstance().update(obj)) {
            return false;
        }

        // Cập nhật lại dữ liệu trong bộ nhớ cache `arrLocal`
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                arrLocal.set(i, new CustomerDTO(obj));
                return true;
            }
        }

        return false;
    }

    public boolean isDuplicateCustomer(int id, String firstName, String lastName, String phone, String address) {
        if (firstName == null || lastName == null || phone == null || address == null) return false;

        for (CustomerDTO customer : arrLocal) {
            if (customer.getId() != id &&
                    customer.getFirstName().trim().equalsIgnoreCase(firstName.trim()) &&
                    customer.getLastName().trim().equalsIgnoreCase(lastName.trim()) &&
                    customer.getPhone().trim().equals(phone.trim()) &&
                    customer.getAddress().trim().equalsIgnoreCase(address.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidCustomerInput(CustomerDTO obj) {
        if (obj.getFirstName() == null || obj.getLastName() == null || obj.getPhone() == null || obj.getAddress() == null) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();

        return validator.validateVietnameseText100(obj.getFirstName()) &&
                validator.validateVietnameseText100(obj.getLastName()) &&
                validator.validateVietnamesePhoneNumber(obj.getPhone()) &&
                validator.validateVietnameseText255(obj.getAddress());
    }

}
