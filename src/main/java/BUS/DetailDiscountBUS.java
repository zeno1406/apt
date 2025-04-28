package BUS;

import DAL.DetailDiscountDAL;
import DTO.DetailDiscountDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;

import java.math.BigDecimal;
import java.util.*;

public class DetailDiscountBUS extends BaseBUS<DetailDiscountDTO, String>{
    private static final DetailDiscountBUS INSTANCE = new DetailDiscountBUS();

    public static DetailDiscountBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<DetailDiscountDTO> getAll() {
        return DetailDiscountDAL.getInstance().getAll();
    }

    public int delete(String code, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.DISCOUNT_DETAILDISCOUNT_SERVICE || code == null || code.isEmpty()) return 2;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 21)) {
            return 3;
        }

        if (!AvailableUtils.getInstance().isNotUsedDiscount(code)) {
            return 4;
        }

        if (!DetailDiscountDAL.getInstance().deleteAllDetailDiscountByDiscountCode(code)) {
            return 5;
        }
        arrLocal.removeIf(discount -> Objects.equals(discount.getDiscountCode(), code));
        return 1;
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

    public boolean createDetailDiscountByDiscountCode(String discountCode, int employee_roleId, ArrayList<DetailDiscountDTO> list, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.DISCOUNT_DETAILDISCOUNT_SERVICE || list == null || list.isEmpty() || discountCode == null || discountCode.isEmpty()) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 20) ) {
            return false;
        }
        Set<BigDecimal> seenPrices = new HashSet<>();
        for (DetailDiscountDTO dto : list) {
            if (!seenPrices.add(dto.getTotalPriceInvoice())) {
                return false;
            }
        }

        list.sort(Comparator.comparing(DetailDiscountDTO::getTotalPriceInvoice));
        if (!DetailDiscountDAL.getInstance().insertAllDetailDiscountByDiscountCode(discountCode, list)) {
            return false;
        }
        ArrayList<DetailDiscountDTO> newDetailDiscount = DetailDiscountDAL.getInstance().getAllDetailDiscountByDiscountCode(discountCode);
        arrLocal.addAll(new ArrayList<>(newDetailDiscount));
        return true;
    }

    public boolean insertRollbackDetailDiscount(ArrayList<DetailDiscountDTO> list, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE || list == null || list.isEmpty()) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 21)) {
            return false;
        }
        if (!DetailDiscountDAL.getInstance().insertAllDetailDiscountByDiscountCode(list.get(0).getDiscountCode(), list)) {
            return false;
        }
        ArrayList<DetailDiscountDTO> newDetailDiscount = DetailDiscountDAL.getInstance().getAllDetailDiscountByDiscountCode(list.get(0).getDiscountCode());
        arrLocal.addAll(new ArrayList<>(newDetailDiscount));
        return true;
    }

}
