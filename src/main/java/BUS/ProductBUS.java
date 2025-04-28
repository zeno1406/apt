package BUS;

import DAL.ProductDAL;

import DTO.ProductDTO;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;
import UTILS.ValidationUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public int delete(String id, int employee_roleId, int employeeLoginId) {
        if (id == null || id.isEmpty() || employee_roleId <= 0) {
            return 2;
        }

        if (getByIdLocal(id).getStockQuantity() != 0 ) return 5;

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 8)) return 3;

        if (!ProductDAL.getInstance().delete(id)) {
            return 4;
        }

        // Cập nhật trạng thái sản phẩm trong `arrLocal`
        for (ProductDTO product : arrLocal) {
            if (product.getId().equals(id)) {
                product.setStatus(false);
                break;
            }
        }
        return 1;
    }

    public String autoId() {
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

    private String nextProductId(String currentId) {
        int temp = Integer.parseInt(currentId.substring(2)) + 1;
        return String.format("SP%05d", temp);
    }


    public int insert(ProductDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getCategoryId() <= 0 || employee_roleId <= 0 || !isValidProductInput(obj)) {
            return 2;
        }

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 7)) return 3;

        if (!AvailableUtils.getInstance().isValidCategory(obj.getCategoryId())) return 4;

        if (isDuplicateProductName("", obj.getName())) {
            return 5;
        }
        ValidationUtils validate = ValidationUtils.getInstance();
        // Xuống dc đây là đúng hết rồi
        obj.setId(autoId()); // Tạo ID mới
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));
        obj.setDescription(validate.normalizeWhiteSpace(obj.getDescription()));
        obj.setStockQuantity(0);
        obj.setSellingPrice(new BigDecimal(0));
        if (!ProductDAL.getInstance().insert(obj)) return 6;

        arrLocal.add(new ProductDTO(obj));
        return 1;
    }

    public int insertListProductExcel(ArrayList<ProductDTO> listProducts) {
        if (listProducts == null || listProducts.isEmpty()) {
            return 2; // Danh sách rỗng
        }

        AvailableUtils ava = AvailableUtils.getInstance();
        Set<String> excelProductNames = new HashSet<>();
        ValidationUtils validate = ValidationUtils.getInstance();
        String id = autoId();

        // Duyệt qua từng sản phẩm trong danh sách
        for (ProductDTO p : listProducts) {
            if (!isValidProductInputForExcel(p)) return 3; // Kiểm tra tính hợp lệ của sản phẩm

            // Kiểm tra tên trùng trong danh sách Excel
            String normalizedName = validate.normalizeWhiteSpace(p.getName());
            if (excelProductNames.contains(normalizedName)) {
                p.setName(p.getName() + " (" + id + ")"); // Nếu trùng, thêm ID vào tên
            }
            excelProductNames.add(normalizedName); // Thêm vào danh sách đã kiểm tra tên

            // Kiểm tra danh mục hợp lệ
            if (!ava.isValidCategory(p.getCategoryId())) return 4;

            // Kiểm tra trùng tên trong hệ thống
            if (isDuplicateProductName("", p.getName())) {
                p.setName(p.getName() + " (" + id + ")"); // Thêm ID vào tên sản phẩm nếu trùng
            }

            // Thiết lập các giá trị còn lại cho sản phẩm
            p.setId(id); // Tạo ID mới
            p.setDescription(validate.normalizeWhiteSpace(p.getDescription()));
            p.setStockQuantity(0);
            p.setSellingPrice(new BigDecimal(0));
            id = nextProductId(id); // Tạo ID mới cho sản phẩm tiếp theo
        }

        // Lưu sản phẩm vào cơ sở dữ liệu
        if (!ProductDAL.getInstance().insertListProductExcel(listProducts)) return 7;

        // Thêm vào danh sách cục bộ (arrLocal)
        arrLocal.addAll(new ArrayList<>(listProducts));

        return 1; // Thành công
    }

    public int update(ProductDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getId().isEmpty() || employee_roleId <= 0 || obj.getCategoryId() <= 0) {
            return 2;
        }

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 9)) return 3;

        if (!isValidProductUpdate(obj)) return 4;


        if (isDuplicateProductName(obj.getId(), obj.getName())) {
            return 5;
        }

        if (isDuplicateProduct(obj)) return 1;
        if (isDuplicateImageUrl(obj)) return 1;
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));
        obj.setDescription(validate.normalizeWhiteSpace(obj.getDescription()));
        if (!ProductDAL.getInstance().update(obj)) return 6;

        // Cập nhật arrLocal nếu database cập nhật thành công
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                ProductDTO local = arrLocal.get(i);
                ProductDTO newProduct = new ProductDTO(local); // Giữ nguyên các field khác

                newProduct.setName(obj.getName());
                newProduct.setSellingPrice(obj.getSellingPrice());
                newProduct.setDescription(obj.getDescription());
                newProduct.setImageUrl(obj.getImageUrl());
                newProduct.setCategoryId(obj.getCategoryId());
                newProduct.setStatus(obj.isStatus());

                arrLocal.set(i, newProduct);
                break;
            }
        }
        return 1;
    }

    public boolean updateQuantitySellingPriceListProduct(ArrayList<ProductDTO> listProducts, boolean isAdd) {
        if (listProducts == null || listProducts.isEmpty()) {
            return false;
        }

        ArrayList<ProductDTO> tempList = (ArrayList<ProductDTO>) listProducts.clone();

        if (isAdd) {
            for (ProductDTO p : tempList) {
                ProductDTO current = getByIdLocal(p.getId());
                if (current == null) continue;

                p.setStockQuantity(p.getStockQuantity() + current.getStockQuantity());
            }
        } else {
            for (ProductDTO p : tempList) {
                ProductDTO current = getByIdLocal(p.getId());
                if (current == null) return false;

                int newQty = current.getStockQuantity() - p.getStockQuantity();
                p.setStockQuantity(Math.max(0, newQty));
                p.setSellingPrice(p.getSellingPrice()); // giữ nguyên giá
            }
        }

        // Cập nhật database
        if (!ProductDAL.getInstance().updateProductQuantityAndSellingPrice(tempList)) return false;

        // Cập nhật lại arrLocal
        for (ProductDTO updated : tempList) {
            for (int i = 0; i < arrLocal.size(); i++) {
                if (Objects.equals(arrLocal.get(i).getId(), updated.getId())) {
                    ProductDTO local = arrLocal.get(i);
                    ProductDTO newProduct = new ProductDTO(local); // Giữ nguyên các field khác

                    newProduct.setStockQuantity(updated.getStockQuantity());
                    newProduct.setSellingPrice(updated.getSellingPrice());

                    arrLocal.set(i, newProduct);
                    break;
                }
            }
        }

        return true;
    }

    //    VALIDATE IS HERE!!!
    private boolean isDuplicateProductName(String id, String name) {
        if (name == null) return false;
        ValidationUtils validate = ValidationUtils.getInstance();
        name = validate.normalizeWhiteSpace(name);
        for (ProductDTO product : arrLocal) {
            if (!Objects.equals(product.getId(), id) && product.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidProductInput(ProductDTO obj) {
        if (obj.getName() == null) return false;

        obj.setDescription(obj.getDescription() != null && obj.getDescription().trim().isEmpty() ? null : obj.getDescription());
        obj.setImageUrl(obj.getImageUrl() != null && obj.getImageUrl().trim().isEmpty() ? null : obj.getImageUrl());
        ValidationUtils validator = ValidationUtils.getInstance();
        // Kiểm tra mô tả nếu có
        if (obj.getDescription() != null && !validator.validateVietnameseText65k4(obj.getDescription())) {
            return true;
        }
        if (obj.getImageUrl() != null && !validator.validateVietnameseText255(obj.getImageUrl())) {
            return true;
        }

        return validator.validateVietnameseText255(obj.getName());
    }

    public boolean isValidProductInputForExcel(ProductDTO obj) {
        if (obj.getName() == null) return false;

        obj.setDescription(obj.getDescription() != null && obj.getDescription().trim().isEmpty() ? null : obj.getDescription());
        obj.setImageUrl(obj.getImageUrl() != null && obj.getImageUrl().trim().isEmpty() ? null : obj.getImageUrl());
        ValidationUtils validator = ValidationUtils.getInstance();
        // Kiểm tra mô tả nếu có
        if (obj.getDescription() != null && !validator.validateVietnameseText65k4(obj.getDescription())) {
            return true;
        }
        if (obj.getImageUrl() != null && !validator.validateVietnameseText255(obj.getImageUrl())) {
            return true;
        }

        return validator.validateVietnameseText248(obj.getName());
    }

    private boolean isValidProductUpdate(ProductDTO obj) {
        if (obj == null || obj.getName() == null || obj.getSellingPrice() == null) {
//            System.out.println("1");
            return false;
        }

        // Xử lý mô tả và ảnh trống
        obj.setDescription(obj.getDescription() != null && obj.getDescription().trim().isEmpty() ? null : obj.getDescription());
        obj.setImageUrl(obj.getImageUrl() != null && obj.getImageUrl().trim().isEmpty() ? null : obj.getImageUrl());

        ValidationUtils validator = ValidationUtils.getInstance();

        // Kiểm tra mô tả nếu có
        if (obj.getDescription() != null && !validator.validateVietnameseText65k4(obj.getDescription())) {
            return false;  // Nếu mô tả không hợp lệ, trả về false
        }

        // Kiểm tra ảnh URL nếu có
        if (obj.getImageUrl() != null && !validator.validateVietnameseText255(obj.getImageUrl())) {
            return false;  // Nếu ảnh không hợp lệ, trả về false
        }

        // Kiểm tra số lượng tồn kho không âm, tên và giá bán hợp lệ
        return obj.getStockQuantity() >= 0
                && validator.validateVietnameseText255(obj.getName())  // Kiểm tra tên
                && validator.validateBigDecimal(obj.getSellingPrice(), 10, 2, false);  // Kiểm tra giá bán
    }

    public boolean isDuplicateProduct(ProductDTO obj) {
        ProductDTO existingPro = getByIdLocal(obj.getId());
        ValidationUtils validate = ValidationUtils.getInstance();

        // Kiểm tra xem tên, mô tả, và hệ số lương có trùng không
        return existingPro != null &&
                Objects.equals(existingPro.getName(), validate.normalizeWhiteSpace(obj.getName())) &&
                Objects.equals(existingPro.getCategoryId(), obj.getCategoryId()) &&
                Objects.equals(existingPro.getStockQuantity(), obj.getStockQuantity()) &&
                Objects.equals(existingPro.getSellingPrice(), obj.getSellingPrice()) &&
                Objects.equals(existingPro.isStatus(), obj.isStatus()) &&
                Objects.equals(existingPro.getDescription(), validate.normalizeWhiteSpace(obj.getDescription())) &&
                Objects.equals(existingPro.getImageUrl(), obj.getImageUrl());
    }

    public boolean isDuplicateImageUrl(ProductDTO obj) {
        ProductDTO existingPro = getByIdLocal(obj.getId());
        return existingPro != null &&
                Objects.equals(existingPro.getImageUrl(), obj.getImageUrl());
    }

    public ArrayList<ProductDTO> filterProducts(String searchBy, String keyword, int categoryIdFilter, int statusFilter,
                                                BigDecimal startPrice, BigDecimal endPrice, boolean inStockOnly) {
        ArrayList<ProductDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = "";
        if (searchBy == null) searchBy = "";

        keyword = keyword.trim().toLowerCase();

        for (ProductDTO pro : arrLocal) {
            boolean matchesSearch = true;
            boolean matchesCategory = (categoryIdFilter == -1) || (pro.getCategoryId() == categoryIdFilter);
            boolean matchesStatus = (statusFilter == -1) || (pro.isStatus() == (statusFilter == 1));
            boolean matchesPrice = true;
            boolean matchesStock = !inStockOnly || pro.getStockQuantity() > 0;

            // Giá
            if (startPrice != null && endPrice != null) {
                matchesPrice = pro.getSellingPrice().compareTo(startPrice) >= 0 &&
                        pro.getSellingPrice().compareTo(endPrice) <= 0;
            } else if (startPrice != null) {
                matchesPrice = pro.getSellingPrice().compareTo(startPrice) >= 0;
            } else if (endPrice != null) {
                matchesPrice = pro.getSellingPrice().compareTo(endPrice) <= 0;
            }

            String name = pro.getName() != null ? pro.getName().toLowerCase() : "";
            String productId = pro.getId() != null ? pro.getId().toLowerCase() : "";

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã sản phẩm" -> matchesSearch = productId.contains(keyword);
                    case "Tên sản phẩm" -> matchesSearch = name.contains(keyword);
                }
            }

            if (matchesSearch && matchesCategory && matchesStatus && matchesPrice && matchesStock) {
                filteredList.add(pro);
            }
        }

        return filteredList;
    }

    public ArrayList<ProductDTO> filterProducts(String searchBy, String keyword, int categoryIdFilter,
                                                int statusFilter, BigDecimal startPrice, BigDecimal endPrice) {
        return filterProducts(searchBy, keyword, categoryIdFilter, statusFilter, startPrice, endPrice, false);
    }


}