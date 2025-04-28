package BUS;

import DAL.CategoryDAL;
import DAL.CustomerDAL;
import DTO.CategoryDTO;
import DTO.CustomerDTO;
import SERVICE.AuthorizationService;
import UTILS.ValidationUtils;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryBUS extends BaseBUS<CategoryDTO, Integer> {
    private static final CategoryBUS INSTANCE = new CategoryBUS();

    private CategoryBUS() {
    }

    public static CategoryBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<CategoryDTO>  getAll() {
        return CategoryDAL.getInstance().getAll();
    }

    public CategoryDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (CategoryDTO category : arrLocal) {
            if (Objects.equals(category.getId(), id)) {
                return new CategoryDTO(category);
            }
        }
        return null;
    }

    public int insert(CategoryDTO obj, int employee_roleId, int employeeLoginId) {
        // 1. Kiểm tra null
        if (obj == null || employee_roleId <= 0 || employeeLoginId <= 0) return 2;

        // 2.Kiểm tra phân quyền
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 17)) return 4;

        // 3. Kiểm tra đầu vào hợp lệ
        if (!isValidCategoryInput(obj)) return 2;

        // 4. Kiểm tra trùng tên
        if (isDuplicateCategory(-1, obj.getName())) return 3;

        // 5. validate khi chuyen xuong database
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setStatus(true);
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));

        // 6. Kiểm tra thêm vào CSDL
        if(!CategoryDAL.getInstance().insert(obj)) return 5;

        // 7. Thêm vào danh sách tạm
        arrLocal.add(new CategoryDTO(obj));
        return 1; // thêm thành công
    }

    public int update(CategoryDTO obj, int employee_roleId, int employeeLoginId) {
        // 1. Kiểm tra null & phân quyền
        if (obj == null || employee_roleId <= 0 || employeeLoginId <= 0) return 2;

        //2. Kiểm tra phân quyền
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 19)) return 4;
        // 3.Kiểm tra phần tử root
        if (obj.getId() == 1) return 6;

        // 3. Kiểm tra đầu vào hợp lệ ở Modal Controller
        if (!isValidCategoryInput(obj)) return 2;

        // 4. Kiểm tra trùng tên
        if (isDuplicateCategory(obj.getId(), obj.getName())) return 3;

        // 5. Kiểm tra đầu vào hợp lệ khi truyền xuống CSDL
        if(isDuplicateCategoryS(obj)) return 1;//Kiểm tra dữ liệu mới xem có trùng dữ liệu cũ không, nếu trùng thì return 1 tức là không update xuống CSDL
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setStatus(true);
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));

        // 6. Kiểm tra thêm vào CSDL
        if(!CategoryDAL.getInstance().update(obj)) return 5;

        updateLocalCache(obj);
        return 1;
    }

    public int delete(Integer id, int employee_roleId, int employeeLoginId) {
        // 1.Kiểm tra null
        if (id == null || id <= 0) return 2;

        // 2.Kiểm tra phân quyền
        if (employee_roleId <= 0 || employeeLoginId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 18)) return 4;

        // 3.Kiểm tra phần tử root
        if (id == 1) return 3;

        // 4.Kiểm tra thể loại đã bị xoá hoặc không tồn tại
        CategoryDTO targetCategory = getByIdLocal(id);
        if (targetCategory == null || !targetCategory.isStatus()) return 5;

        // 5.Kiểm tra đã xoá ở CSDL
        if(!CategoryDAL.getInstance().delete(id)) return 6;

        // Cập nhật trạng thái trong bộ nhớ local
        for (CategoryDTO category : arrLocal) {
            if (Objects.equals(category.getId(), id)) {
                category.setStatus(false);
                break;
            }
        }
        return 1;
    }

    //Cap nhat cache local
    private void updateLocalCache(CategoryDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {arrLocal.set(i, new CategoryDTO(obj));
                break;
            }
        }
    }

    public boolean isDuplicateCategory(int id, String name) {
        if (name == null) return false;

        //chỉ check trùng với các thể loại active, non active không check
        for (CategoryDTO category : arrLocal) {
            if (category.getId() != id &&
                category.getName().trim().equalsIgnoreCase(name.trim()) &&
                category.isStatus())
                return true;
        }
        return false;
    }

    public boolean isDuplicateCategoryS(CategoryDTO obj) {
        CategoryDTO existingPro = getByIdLocal(obj.getId());
        ValidationUtils validate = ValidationUtils.getInstance();

        // Kiểm tra xem tên, mô tả, và hệ số lương có trùng không
        return existingPro != null &&
                Objects.equals(existingPro.getName(), validate.normalizeWhiteSpace(obj.getName())) &&
                Objects.equals(existingPro.isStatus(), obj.isStatus());
    }

    private boolean isValidCategoryInput(CategoryDTO obj) {
        if (obj.getName() == null || obj.getName().trim().isEmpty()) return false;
        ValidationUtils validator = ValidationUtils.getInstance();
        return validator.validateVietnameseText50(obj.getName());
    }

    public ArrayList<CategoryDTO> filterCategories(String searchBy, String keyword, int statusFilter) {
        ArrayList<CategoryDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = "";
        if (searchBy == null) searchBy = "";

        keyword = keyword.trim().toLowerCase();

        for (CategoryDTO cate : arrLocal) {
            boolean matchesSearch = true;
            boolean matchesStatus = (statusFilter == -1) || (cate.isStatus() == (statusFilter == 1));

            String id = String.valueOf(cate.getId());
            String username = cate.getName() != null ? cate.getName().toLowerCase() : "";

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã thể loại" -> matchesSearch = id.contains(keyword);
                    case "Tên thể loại" -> matchesSearch = username.contains(keyword);
                }
            }

            if (matchesSearch && matchesStatus) {
                filteredList.add(cate);
            }
        }

        return filteredList;
    }
}
