package BUS;

import DAL.DetailDiscountDAL;
import DAL.DetailImportDAL;
import DTO.DetailDiscountDTO;
import DTO.DetailImportDTO;

import java.util.ArrayList;
import java.util.Objects;

public class DetailDiscountBUS extends BaseBUS<DetailDiscountDTO, String>{
    private static final DetailDiscountBUS INSTANCE = new DetailDiscountBUS();

    public static DetailDiscountBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<DetailDiscountDTO> getAll() {
        return DetailDiscountDAL.getInstance().getAll();
    }

    public ArrayList<DetailDiscountDTO> getAllDetailDiscountByDiscountIdLocal(String discountCode) {
        if (discountCode == null || discountCode.isEmpty()) return null;
        ArrayList<DetailDiscountDTO> result = new ArrayList<>();
        for (DetailDiscountDTO di : arrLocal) {
            if (Objects.equals(di.getDiscountCode(), discountCode)) {
                result.add(new DetailDiscountDTO(di));
            }
        }
        return result;
    }
}
