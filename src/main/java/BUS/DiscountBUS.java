package BUS;

import DAL.DiscountDAL;
import DTO.DiscountDTO;
import DTO.EmployeeDTO;

import java.util.ArrayList;
import java.util.Objects;

public class DiscountBUS extends BaseBUS<DiscountDTO, String> {
    private static final DiscountBUS INSTANCE = new DiscountBUS();

    private DiscountBUS() {
    }

    public static DiscountBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<DiscountDTO> getAll() {
        return DiscountDAL.getInstance().getAll();
    }

    public DiscountDTO getByIdLocal(String code) {
        if (code == null || code.isEmpty()) return null;
        for (DiscountDTO dis : arrLocal) {
            if (Objects.equals(dis.getCode(), code)) {
                return new DiscountDTO(dis);
            }
        }
        return null;
    }
}
