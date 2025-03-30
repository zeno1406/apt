package BUS;

import DAL.SupplierDAL;
import DTO.CustomerDTO;
import DTO.SupplierDTO;

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
}
