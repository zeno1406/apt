package BUS;

import DAL.InvoiceDAL;
import DTO.InvoiceDTO;
import INTERFACE.ServiceAccessCode;
import INTERFACE.SystemConfig;
import SERVICE.AuthorizationService;
import UTILS.ValidationUtils;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.Objects;

public class InvoiceBUS extends BaseBUS<InvoiceDTO, Integer>{
    private static final InvoiceBUS INSTANCE = new InvoiceBUS();

    public static InvoiceBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<InvoiceDTO> getAll() {
        return InvoiceDAL.getInstance().getAll();
    }

    public boolean delete(Integer id, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE || id == null || id <= 0 || !isValidForDelete(id)) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 14)) return false;

        if (!InvoiceDAL.getInstance().delete(id)) {
            return false;
        }
        arrLocal.removeIf(role -> Objects.equals(role.getId(), id));
        return true;
    }

    public InvoiceDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getId(), id)) {
                return new InvoiceDTO(invoice);
            }
        }
        return null;
    }

    public boolean insert(InvoiceDTO obj, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE || obj == null) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 13) || !isValidateInvoiceInput(obj)) return false;

        obj.setCreateDate(LocalDateTime.now());
        if (!InvoiceDAL.getInstance().insert(obj)) return false;
        arrLocal.add(new InvoiceDTO(obj));
        return true;
    }

    private boolean isValidateInvoiceInput(InvoiceDTO obj) {
        if (obj.getEmployeeId() <= 0 || obj.getCustomerId() <= 0) return false;

        ValidationUtils validator = ValidationUtils.getInstance();

        // Kiểm tra giá trị số hợp lệ
        if (!validator.validateBigDecimal(obj.getTotalPrice(), 10, 2, false)
                || !validator.validateBigDecimal(obj.getDiscountAmount(), 10, 2, false)) {
            return false;
        }

        // Nếu có discountCode, kiểm tra độ dài
        if (obj.getDiscountCode() != null && !validator.validateStringLength(obj.getDiscountCode(), 50)) {
            return false;
        }

        // Nếu discountAmount > 0 nhưng không có mã giảm giá => sai
        return obj.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0 || obj.getDiscountCode() != null;
    }

    private boolean isValidForDelete(Integer id) {
        if (id == null || id <= 0) return false;

        LocalDateTime now = LocalDateTime.now();
        int maxMinutes = SystemConfig.INVOICE_DELETE_TIME_LIMIT / (60 * 1000);

        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getId(), id)) {
                if (invoice.getCreateDate() == null) return false; // Tránh NullPointerException
                long minutesDifference = Duration.between(invoice.getCreateDate(), now).toMinutes();
                return minutesDifference <= maxMinutes;
            }
        }
        return false;
    }

}
