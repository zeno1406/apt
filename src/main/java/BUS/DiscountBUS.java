package BUS;

import DAL.DiscountDAL;
import DTO.DiscountDTO;
import DTO.EmployeeDTO;

import java.time.LocalDate;
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

    public ArrayList<DiscountDTO> searchByCodeLocal(String keyword) {
        ArrayList<DiscountDTO> result = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) return result;
        for (DiscountDTO dis : arrLocal) {
            if (dis.getCode().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(new DiscountDTO(dis));
            }
        }
        return result;
    }

    public ArrayList<DiscountDTO> filterDiscountsAdvance(
            String discountName, int type, LocalDate startDate,
            LocalDate endDate
    ) {
        ArrayList<DiscountDTO> filteredList = new ArrayList<>();

        for (DiscountDTO dis : arrLocal) {
            boolean matchesDate = true;
            boolean matchesOther = false;

            LocalDate discountStartDate = dis.getStartDate().toLocalDate();
            LocalDate discountEndDate = dis.getEndDate().toLocalDate();

            // Logic l�+�c ng+�y m�+�i
            if (startDate != null && endDate != null) {
                matchesDate = !discountEndDate.isAfter(endDate);
            } else if (startDate != null) {
                matchesDate = !discountStartDate.isBefore(startDate);
            } else if (endDate != null) {
                matchesDate = !discountEndDate.isAfter(endDate);
            }

            // L�+�c t+�n hoߦ+c type
            if (discountName != null && !discountName.isBlank()) {
                matchesOther |= dis.getName().toLowerCase().contains(discountName.toLowerCase());
            }

            if (type != -1) {
                matchesOther |= dis.getType() == type;
            }

            // Nߦ+u kh+�ng nhߦ�p g+� cߦ� => mߦ+c -��+�nh true
            if ((discountName == null || discountName.isBlank()) && type == -1) {
                matchesOther = true;
            }

            if (matchesDate && matchesOther) {
                filteredList.add(new DiscountDTO(dis));
            }
        }

        return filteredList;
    }
}
