package BUS;

import DAL.CategoryDAL;
import DTO.CategoryDTO;

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

    public int searchByName (String filterName) {
        for (CategoryDTO category : arrLocal)
            if (category.getName().equals(filterName) && category.isStatus())
                return category.getId();
        return -1;
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
