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
        for (CategoryDTO categoryx : arrLocal) {
            if (Objects.equals(categoryx.getId(), id)) {
                return new CategoryDTO(categoryx);
            }
        }
        return null;
    }

    public int insert(CategoryDTO obj, int employee_roleId, int employeeLoginId) {
        // 1. Kiểm tra null & phân quyền
        if (obj == null || employee_roleId <= 0 || employee_roleId <= 0) return 2;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 17)) return 4;

        // 2. Kiểm tra đầu vào hợp lệ
        if (!isValidCategoryInput(obj)) return 2;

        // 3. Kiểm tra trùng tên
        if (isDuplicateCategory(-1, obj.getName())) return 3;

        // 4. Kiểm tra thêm vào CSDL
        if(!CategoryDAL.getInstance().insert(obj)) return 5;

        // 5. Thêm vào danh sách tạm
        arrLocal.add(new CategoryDTO(obj));
        return 1; // thêm thành công
    }

    public int update(CategoryDTO obj, int employee_roleId, int employeeLoginId) {
        // 1. Kiểm tra null & phân quyền
        if (obj == null || employee_roleId <= 0 || employee_roleId <= 0) return 2;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 19)) return 4;

        // 2. Kiểm tra đầu vào hợp lệ
        if (!isValidCategoryInput(obj)) return 2;

        // 3. Kiểm tra trùng tên
        if (isDuplicateCategory(-1, obj.getName())) return 3;

        // 4. Kiểm tra thêm vào CSDL
        if(!CategoryDAL.getInstance().insert(obj)) return 5;

        updateLocalCache(obj);
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

        for (CategoryDTO category : arrLocal) {
            if (category.getId() != id && category.getName().trim().equalsIgnoreCase(name.trim())) return true;
        }
        return false;
    }

    private boolean isValidCategoryInput(CategoryDTO obj) {
        int MIN_LENGTH =4;
        int MAX_LENGTH = 50;
        if (obj.getName() == null || obj.getName().trim().isEmpty()) return false;
        ValidationUtils validator = ValidationUtils.getInstance();
        return validator.validateUsername(obj.getName(),MIN_LENGTH,MAX_LENGTH);
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
