package BUS;

import DAL.ProductDAL;
import DTO.ProductDTO;
import SERVICE.AuthorizationService;
import UTILS.ValidationUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class ProductBUS  extends BaseBUS <ProductDTO, String>{
    private static final ProductBUS INSTANCE = new ProductBUS();

    private ProductBUS() {
    }

    public static ProductBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<ProductDTO> getAll() {
        return ProductDAL.getInstance().getAll();
    }

    public ProductDTO getByIdLocal(String id) {
        if (id == null || id.isEmpty()) return null;
        for (ProductDTO product : arrLocal) {
            if (Objects.equals(product.getId(), id)) {
                return new ProductDTO(product);
            }
        }
        return null;
    }

    public boolean delete(String id, int employee_roleId, int employeeLoginId) {
        if (id == null || id.isEmpty() || employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 8)) {
            return false;
        }

        if (!ProductDAL.getInstance().delete(id)) {
            return false;
        }

        // Cập nhật trạng thái sản phẩm trong `arrLocal`
        for (ProductDTO product : arrLocal) {
            if (product.getId().equals(id)) {
                product.setStatus(false);
                return true;
            }
        }
        return false;
    }

    private String autoId() {
        if (isLocalEmpty()) {
            return "SP00001";
        }

        String lastId = arrLocal.get(arrLocal.size() - 1).getId();
        try {
            int id = Integer.parseInt(lastId.substring(2)) + 1;
            return String.format("SP%05d", id);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid product ID format: " + lastId);
        }
    }

    public boolean insert(ProductDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 7) || !isValidProductInput(obj)) {
            return false;
        }

        // Xuống dc đây là đúng hết rồi
        obj.setId(autoId()); // Tạo ID mới
        obj.setStockQuantity(0);
        obj.setSellingPrice(new BigDecimal(0));
        obj.setStatus(true);

        if (isDuplicateProductName("", obj.getName()) || !ProductDAL.getInstance().insert(obj)) {
            return false;
        }

        arrLocal.add(new ProductDTO(obj));
        return true;
    }

    public boolean update(ProductDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getId().isEmpty() || employee_roleId <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 9) || !isValidProductUpdate(obj)) {
            return false;
        }

        if (isDuplicateProductName(obj.getId(), obj.getName()) || !ProductDAL.getInstance().update(obj)) {
            return false;
        }

        // Cập nhật arrLocal nếu database cập nhật thành công
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                arrLocal.set(i, new ProductDTO(obj));
                return true;
            }
        }
        return false;
    }

    private boolean isDuplicateProductName(String id, String name) {
        if (name == null) return false;
        for (ProductDTO product : arrLocal) {
            if (!Objects.equals(product.getId(), id) && Objects.equals(product.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidProductInput(ProductDTO obj) {
        if (obj.getName() == null) return false;

        return ValidationUtils.getInstance().validateVietnameseText255(obj.getName());
    }

    private boolean isValidProductUpdate(ProductDTO obj) {
        if (obj == null || obj.getName() == null || obj.getSellingPrice() == null) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();
        return obj.getStockQuantity() >= 0
                && validator.validateVietnameseText255(obj.getName())
                && validator.validateBigDecimal(obj.getSellingPrice(), 10, 2, false);
    }


}
