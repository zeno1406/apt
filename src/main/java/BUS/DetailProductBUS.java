package BUS;

import DAL.DetailProductDAL;
import DAL.ProductDAL;
import DTO.DetailInvoiceDTO;
import DTO.DetailProductDTO;
import DTO.ProductDTO;

import java.util.ArrayList;
import java.util.Objects;

public class DetailProductBUS extends BaseBUS<DetailProductDTO, Integer>{
    private static final DetailProductBUS INSTANCE = new DetailProductBUS();

    public static DetailProductBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<DetailProductDTO> getAll() {
        return DetailProductDAL.getInstance().getAll();
    }

    public DetailProductDTO getByIdLocal(String id) {
        if (id == null || id.isEmpty()) return null;
        for (DetailProductDTO dproduct : arrLocal) {
            if (Objects.equals(dproduct.getProductId(), id)) {
                return new DetailProductDTO(dproduct);
            }
        }
        return null;
    }
}
