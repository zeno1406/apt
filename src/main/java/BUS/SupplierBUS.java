package BUS;

import DAL.CustomerDAL;
import DAL.SupplierDAL;
import DTO.CategoryDTO;
import DTO.CustomerDTO;
import DTO.SupplierDTO;
import SERVICE.AuthorizationService;
import UTILS.ValidationUtils;

import java.util.ArrayList;
import java.util.Objects;

public class SupplierBUS extends BaseBUS<SupplierDTO, Integer>{
    private static final SupplierBUS INSTANCE = new SupplierBUS();

    private SupplierBUS() {
    }

    public static SupplierBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<SupplierDTO> getAll() {
        return SupplierDAL.getInstance().getAll();
    }

    public SupplierDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (SupplierDTO supp : arrLocal) {
            if (Objects.equals(supp.getId(), id)) {
                return new SupplierDTO(supp);
            }
        }
        return null;
    }

    public ArrayList<SupplierDTO> filterSuppliers(String searchBy, String keyword, int statusFilter) {
        ArrayList<SupplierDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = "";
        if (searchBy == null) searchBy = "";

        keyword = keyword.trim().toLowerCase();

        for (SupplierDTO sup : arrLocal) {
            boolean matchesSearch = true;
            boolean matchesStatus = (statusFilter == -1) || (sup.isStatus() == (statusFilter == 1));

            // Kiểm tra null tránh lỗi khi gọi .toLowerCase()
            String name = sup.getName() != null ? sup.getName().toLowerCase() : "";
            String id = String.valueOf(sup.getId());

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã nhà cung cấp" -> matchesSearch = id.contains(keyword);
                    case "Tên nhà cung cấp" -> matchesSearch = name.contains(keyword);
                }
            }

            // Chỉ thêm vào danh sách nếu thỏa tất cả điều kiện
            if (matchesSearch && matchesStatus) {
                filteredList.add(sup);
            }
        }

        return filteredList;
    }

    public ArrayList<SupplierDTO> searchSupplierByPhone(String phone) {
        ArrayList<SupplierDTO> temp = new ArrayList<>();

        if (phone == null || phone.trim().isEmpty()) {
            return temp; // hoặc return arrLocal nếu bạn muốn hiển thị tất cả
        }

        for (SupplierDTO s : arrLocal) {
            if (s.getPhone() != null && s.getPhone().contains(phone.trim()) && s.isStatus()) {
                temp.add(s);
            }
        }
        return temp;
    }

    public int delete(Integer id, int employee_roleId, int employeeLoginId) {
        // 1.Kiểm tra null
        if (id == null || id <= 0) return 2;

        // 2.Kiểm tra quyền xóa Nhà cung cấp (permission ID = 11)
        if (employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 11)) {
            return 4; // Không có quyền xóa
        }

        // 3.Kiểm tra Nhà cung cấp da bi xoa hoac khong ton tai
        SupplierDTO targetSup = getByIdLocal(id);
        if (targetSup == null || !targetSup.isStatus()) return 5;

        // 4.Kiểm tra đã xoá ở CSDL
        if (!SupplierDAL.getInstance().delete(id)) {
            return 6;
        }

        // Cập nhật trạng thái trong bộ nhớ local
        for (SupplierDTO sup : arrLocal) {
            if (Objects.equals(sup.getId(), id)) {
                sup.setStatus(false);
                break;
            }
        }
        return 1;
    }

    //Cap nhat cache local
    private void updateLocalCache(SupplierDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {arrLocal.set(i, new SupplierDTO(obj));
                break;
            }
        }
    }

    public boolean isDuplicateSupplier(int id, String name, String phone, String address) {
        if (name == null || phone == null || address == null) return false;

        //chỉ check trùng với các thể loại active, non active không check
        for (SupplierDTO sup : arrLocal) {
            if (sup.getId() != id &&
                    sup.getName().trim().equalsIgnoreCase(name.trim()) &&
                    sup.getPhone().trim().equalsIgnoreCase(phone.trim()) &&
                    sup.getAddress().trim().equalsIgnoreCase(address.trim()) &&
                    sup.isStatus())
                return true;
        }
        return false;
    }

    public boolean isDuplicateSupplierS(SupplierDTO obj) {
        SupplierDTO existingPro = getByIdLocal(obj.getId());
        ValidationUtils validate = ValidationUtils.getInstance();

        // Kiểm tra xem tên, mô tả, và hệ số lương có trùng không
        return existingPro != null &&
                Objects.equals(existingPro.getName(), validate.normalizeWhiteSpace(obj.getName())) &&
                Objects.equals(existingPro.getPhone(), obj.getPhone()) &&
                Objects.equals(existingPro.getAddress(), validate.normalizeWhiteSpace(obj.getAddress())) &&
                Objects.equals(existingPro.isStatus(), obj.isStatus());
    }

    private boolean isValidSupplierInput(SupplierDTO obj) {
        if (    obj.getName() == null || obj.getName().trim().isEmpty() ||
                obj.getPhone() == null || obj.getPhone().trim().isEmpty() ||
                obj.getAddress() == null || obj.getAddress().trim().isEmpty()
            ) return false;

        ValidationUtils validator = ValidationUtils.getInstance();
        return validator.validateVietnameseText50(obj.getName()) &&
                validator.validateVietnamesePhoneNumber(obj.getPhone()) &&
                validator.validateVietnameseText65k4(obj.getAddress());
    }

    public int insert(SupplierDTO obj, int employee_roleId, int employeeLoginId) {
        // 1. Kiểm tra ID hợp lệ
        if (obj == null || employee_roleId <= 0 || employeeLoginId <= 0) return 2;

        // 2. Kiểm tra quyền thêm nhà cung cấp mã 10
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 10)) return 4;

        // 3. Kiểm tra dữ liệu đầu vào trên GUI
        if (!isValidSupplierInput(obj)) return 6;

        // 4. Kiểm tra trùng
        if (isDuplicateSupplier(-1, obj.getName(), obj.getPhone(), obj.getAddress())) return 3;

        // 5. validate khi chuyen xuong database
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setStatus(true);
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));
        obj.setPhone(obj.getPhone());
        obj.setAddress(validate.normalizeWhiteSpace(obj.getAddress()));

        // 6. Kiểm tra thêm vào CSDL
        if (!SupplierDAL.getInstance().insert(obj)) return 5;

        // 7. Thêm vào danh sách tạm
        arrLocal.add(new SupplierDTO(obj));
        return 1;//them thanh cong
    }

    public int update(SupplierDTO obj, int employee_roleId, int employeeLoginId) {
        // 1. Kiểm tra null & phân quyền
        if (obj == null || employee_roleId <= 0 || employeeLoginId <= 0) return 2;

        //2. Kiểm tra phân quyền
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 12)) return 4;

        // 3. Kiểm tra đầu vào hợp lệ ở Modal Controller
        if(!isValidSupplierInput(obj)) return 6;

        // 4. Kiểm tra trùng lặp
        if (isDuplicateSupplier(obj.getId(), obj.getName(), obj.getPhone(), obj.getAddress())) return 3;

        // 5. Kiểm tra dữ liệu mới xem có trùng dữ liệu cũ không, nếu trùng thì return 1 tức là không update xuống CSDL
        if (isDuplicateSupplierS(obj)) return 1;

        // 6. Kiểm tra đầu vào hợp lệ khi truyền xuống CSDL
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setStatus(true);
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));
        obj.setPhone(validate.normalizeWhiteSpace(obj.getPhone()));
        obj.setAddress(validate.normalizeWhiteSpace(obj.getAddress()));

        // 6. Kiểm tra thêm vào CSDL
        if (!SupplierDAL.getInstance().update(obj)) {
            return 5;
        }

        //Sửa thành công
        updateLocalCache(obj);
        return 1;
    }

}
