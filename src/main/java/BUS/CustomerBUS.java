package BUS;

import DAL.CustomerDAL;
import DTO.CustomerDTO;
import DTO.EmployeeDTO;
import SERVICE.AuthorizationService;
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

    //done
    public int delete(Integer id, int employee_roleId, int employeeLoginId) {
        // Kiểm tra ID hợp lệ
        if (id == null || id <= 0) return 2; // Khách hàng không tồn tại
    
        // Kiểm tra quyền xóa khách hàng (permission ID = 5)
        if (employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 5)) {
            return 4; // Không có quyền xóa
        }

        // // Kiểm tra khách hàng có đơn hàng không
        // if (OrderBUS.getInstance().getByCustomerId(id) != null) {
        //     return 3; // Không thể xóa khách hàng có đơn hàng
        // }

        //Khach hang da bi xoa hoac khong ton tai
        CustomerDTO targetCustomer = getByIdLocal(id);
        if (targetCustomer == null) return 5;

        
        // Xóa khách hàng trong database
        if (!CustomerDAL.getInstance().delete(id)) {
            return 6;
        }
        // Cập nhật trạng thái trong bộ nhớ local
        for (CustomerDTO customer : arrLocal) {
            if (Objects.equals(customer.getId(), id)) {
                customer.setStatus(false);
                break;
            }
        }
        return 1;
    }

    //done
    public int insert(CustomerDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 4) || !isValidCustomerInput(obj)) {
            return 2;
        }

        if (!AuthorizationService.getInstance().hasPermission
        (employeeLoginId, employee_roleId, 4)) return 4;

        // image_url và date_of_birth có thể null
        obj.setStatus(true);

        if (isDuplicateCustomer(-1, obj.getFirstName(), obj.getLastName(), obj.getPhone(), obj.getAddress()) ||
                !CustomerDAL.getInstance().insert(obj)) {
            return 3;
        }

        if (!CustomerDAL.getInstance().insert(obj)) {
            return 5;
        }

        arrLocal.add(new CustomerDTO(obj));
        return 1;//them thanh cong
    }


    public int update(CustomerDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getId() <= 0 || employee_roleId <= 0 ||
                !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 6) || !isValidCustomerInput(obj)) {
            return 2;
        }

        // Kiểm tra trùng lặp trước khi cập nhật
        if (isDuplicateCustomer(obj.getId(), obj.getFirstName(), obj.getLastName(), obj.getPhone(), obj.getAddress())) {
            return 3;
        }

        //Không có quyền sửa
        if (!AuthorizationService.getInstance().
        hasPermission(employeeLoginId, employee_roleId, 6)) return 4;

        // Thực hiện update trong database
        if (!CustomerDAL.getInstance().update(obj)) {
            return 5;
        }

        //Kiểm tra đầu vào hợp lệ
        if (!isValidCustomerInput(obj)) {
            return 6;
        }

        updateLocalCache(obj);
        //Them thanh cong
        return 1;
    }

    //Cap nhat cache local
    private void updateLocalCache(CustomerDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {                    arrLocal.set(i, new CustomerDTO(obj));
            break;
            }
        }  
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

    public ArrayList<CustomerDTO> filterCustomers(String searchBy, String keyword, int statusFilter) {
        ArrayList<CustomerDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = "";
        if (searchBy == null) searchBy = "";

        keyword = keyword.trim().toLowerCase();

        for (CustomerDTO cus : arrLocal) {
            boolean matchesSearch = true;
            boolean matchesStatus = (statusFilter == -1) || (cus.isStatus() == (statusFilter == 1)); // Sửa lỗi ở đây

            // Kiểm tra null tránh lỗi khi gọi .toLowerCase()
            String firstName = cus.getFirstName() != null ? cus.getFirstName().toLowerCase() : "";
            String lastName = cus.getLastName() != null ? cus.getLastName().toLowerCase() : "";
            String id = String.valueOf(cus.getId());

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã khách hàng" -> matchesSearch = id.contains(keyword);
                    case "Họ đệm" -> matchesSearch = firstName.contains(keyword);
                    case "Tên" -> matchesSearch = lastName.contains(keyword);
                }
            }

            // Chỉ thêm vào danh sách nếu thỏa tất cả điều kiện
            if (matchesSearch && matchesStatus) {
                filteredList.add(cus);
            }
        }

        return filteredList;
    }

}
