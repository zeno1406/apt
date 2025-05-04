package BUS;

import DAL.DiscountDAL;
import DTO.DiscountDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;
import UTILS.ValidationUtils;

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
        if (keyword == null || keyword.isEmpty()) return getAllLocal();
        for (DiscountDTO dis : arrLocal) {
            if (dis.getCode().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(new DiscountDTO(dis));
            }
        }
        return result;
    }

    public ArrayList<DiscountDTO> filterDiscountsAdvance(String discountName, int type, LocalDate startDate, LocalDate endDate) {
        ArrayList<DiscountDTO> filteredList = new ArrayList<>();

        for (DiscountDTO dis : arrLocal) {
            boolean matchesDate = true;
            boolean matchesOther = false;

            LocalDate discountStartDate = dis.getStartDate().toLocalDate();
            LocalDate discountEndDate = dis.getEndDate().toLocalDate();

            // Xử lý logic ngày
            if (startDate != null && endDate != null) {
                matchesDate = !discountEndDate.isAfter(endDate);
            } else if (startDate != null) {
                matchesDate = !discountStartDate.isBefore(startDate);
            } else if (endDate != null) {
                matchesDate = !discountEndDate.isAfter(endDate);
            }

            if (discountName != null && !discountName.isBlank()) {
                if (dis.getName().toLowerCase().contains(discountName.toLowerCase())) {
                    matchesOther = true;
                }
            }

            if (type != -1) {
                if (dis.getType() == type) {
                    matchesOther = true;
                }
            }

            // Nếu không nhập gì => mặc định true
            if ((discountName == null || discountName.isBlank()) && type == -1) {
                matchesOther = true;
            }

            if (matchesDate && matchesOther) {
                filteredList.add(new DiscountDTO(dis));
            }
        }

        return filteredList;
    }

    public int insert(DiscountDTO obj, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.DISCOUNT_DETAILDISCOUNT_SERVICE || obj == null) return 2;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 20) || !isValidateDiscountInput(obj)) {
            return 3;
        }
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setCode(obj.getCode().toUpperCase());
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));
        if (isDuplicateDiscountCode(obj.getCode())) return 4;

        if (!DiscountDAL.getInstance().insert(obj)) return 5;
        arrLocal.add(new DiscountDTO(obj));
        return 1;
    }

    public boolean delete(String code, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.DISCOUNT_DETAILDISCOUNT_SERVICE || code == null || code.isEmpty()) {
            return false;
        }

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 21)) {
            return false;
        }

        if (!AvailableUtils.getInstance().isNotUsedDiscount(code)) {
            return false;
        }

        if (!DiscountDAL.getInstance().delete(code)) {
            return false;
        }

        arrLocal.removeIf(dis -> dis.getCode().equals(code));
        return true;
    }


    public int update(DiscountDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getCode().isEmpty() || employee_roleId <= 0) {
            return 2;
        }

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 22)) return 3;

        if (!isValidateDiscountInput(obj)) return 4;

        if (isDuplicateDiscount(obj)) return 1;
        ValidationUtils validate = ValidationUtils.getInstance();
        obj.setName(validate.normalizeWhiteSpace(obj.getName()));
        if (!DiscountDAL.getInstance().update(obj)) return 6;

        // Cập nhật arrLocal nếu database cập nhật thành công
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getCode(), obj.getCode())) {
                arrLocal.set(i, new DiscountDTO(obj));
                break;
            }
        }
        return 1;
    }

    private boolean isDuplicateDiscountCode(String code) {
        if (code == null) return false;
        for (DiscountDTO dis : arrLocal) {
            if (dis.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateDiscount(DiscountDTO obj) {
        DiscountDTO existingDis = getByIdLocal(obj.getCode());
        ValidationUtils validate = ValidationUtils.getInstance();
        // Kiểm tra xem tên, mô tả, và hệ số lương có trùng không
        return existingDis != null &&
                Objects.equals(existingDis.getName(), validate.normalizeWhiteSpace(obj.getName())) &&
                Objects.equals(existingDis.getType(), obj.getType()) &&
                Objects.equals(existingDis.getStartDate(), obj.getStartDate()) &&
                Objects.equals(existingDis.getEndDate(), obj.getEndDate());
    }

    private boolean isValidateDiscountInput(DiscountDTO obj) {
        if (obj.getName() == null || obj.getCode() == null || obj.getStartDate() == null || obj.getEndDate() == null || (obj.getType() != 0 && obj.getType() != 1)) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();

        boolean isValidCode = validator.validateDiscountCode(obj.getCode(), 4, 50);
        boolean isValidName = validator.validateVietnameseText100(obj.getName());

        LocalDate today = LocalDate.now();
        LocalDate startDate = obj.getStartDate().toLocalDate();
        LocalDate endDate = obj.getEndDate().toLocalDate();

        //

        boolean isValidDate = !startDate.isAfter(endDate) &&
                !startDate.isBefore(today) &&
                !endDate.isBefore(today);

        return isValidCode && isValidName && isValidDate;
    }

    public ArrayList<DiscountDTO> filterDiscountsActive() {
        ArrayList<DiscountDTO> filteredList = new ArrayList<>();

        for (DiscountDTO dis : arrLocal) {
            LocalDate discountEndDate = dis.getEndDate().toLocalDate();
            LocalDate now = LocalDate.now();
            if (!discountEndDate.isBefore(now)) {
                filteredList.add(new DiscountDTO(dis));
            }
        }
        return filteredList;
    }

}
