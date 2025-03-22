package BUS;

import DAL.CustomerDAL;
import DAL.ProductDAL;
import DTO.CustomerDTO;
import DTO.ProductDTO;

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
        for (ProductDTO product : arrLocal) {
            if (Objects.equals(product.getId(), id)) {
                return new ProductDTO(product);
            }
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        if (id == null || id == null) return false;
        if (ProductDAL.getInstance().delete(id)) {
            for (ProductDTO product : arrLocal) {
                if (Objects.equals(product.getId(), id)) {
                    product.setStatus(false);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean insert(ProductDTO obj) {
        if (obj == null) return false;
        if (ProductDAL.getInstance().insert(obj)) {
            arrLocal.add(new ProductDTO(obj));
            return true;
        }
        return false;
    }

    public boolean update(ProductDTO obj) {
        if (obj == null || obj.getId() == null) return false;

        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getId(), obj.getId())) {
                if (ProductDAL.getInstance().update(obj)) {
                    arrLocal.set(i, new ProductDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
