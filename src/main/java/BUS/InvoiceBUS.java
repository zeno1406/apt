package BUS;

import DAL.InvoiceDAL;
import DTO.InvoiceDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.ValidationUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        if (codeAccess != ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE || id == null || id <= 0) return false;
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
        if (!validator.validateBigDecimal(obj.getTotalPrice(), 12, 2, false)
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

    public ArrayList<InvoiceDTO> filterInvoicesAdvance(
            String employeeId, String customerId, String discountCode,
            LocalDate startDate, LocalDate endDate,
            BigDecimal startTotalPrice, BigDecimal endTotalPrice
    ) {
        ArrayList<InvoiceDTO> filteredList = new ArrayList<>();

        for (InvoiceDTO invoice : arrLocal) {
            LocalDate invoiceDate = invoice.getCreateDate().toLocalDate();

            // --- Kiểm tra ngày ---
            boolean matchesDate = true;
            if (startDate != null && endDate != null) {
                matchesDate = !invoiceDate.isBefore(startDate) && !invoiceDate.isAfter(endDate);
            } else if (startDate != null) {
                matchesDate = !invoiceDate.isBefore(startDate);
            } else if (endDate != null) {
                matchesDate = !invoiceDate.isAfter(endDate);
            }

            // --- Kiểm tra điều kiện còn lại ---
            boolean matchEmployee = true;
            if (employeeId != null && !employeeId.isEmpty()) {
                matchEmployee = String.valueOf(invoice.getEmployeeId()).contains(employeeId);
            }

            boolean matchCustomer = true;
            if (customerId != null && !customerId.isEmpty()) {
                matchCustomer = String.valueOf(invoice.getCustomerId()).contains(customerId);
            }

            boolean matchDiscount = true;
            if (discountCode != null && !discountCode.isEmpty()) {
                matchDiscount = invoice.getDiscountCode() != null && invoice.getDiscountCode().equals(discountCode);
            }

            boolean matchPrice = true;
            if (startTotalPrice != null && endTotalPrice != null) {
                matchPrice = invoice.getTotalPrice().compareTo(startTotalPrice) >= 0 &&
                        invoice.getTotalPrice().compareTo(endTotalPrice) <= 0;
            } else if (startTotalPrice != null) {
                matchPrice = invoice.getTotalPrice().compareTo(startTotalPrice) >= 0;
            } else if (endTotalPrice != null) {
                matchPrice = invoice.getTotalPrice().compareTo(endTotalPrice) <= 0;
            }

            // --- Có điều kiện nào khác không ---
            boolean hasOtherConditions = (employeeId != null && !employeeId.isEmpty()) ||
                    (customerId != null && !customerId.isEmpty()) ||
                    (discountCode != null && !discountCode.isEmpty()) ||
                    startTotalPrice != null ||
                    endTotalPrice != null;

            boolean matchesOther = !hasOtherConditions || (matchEmployee || matchCustomer || matchDiscount || matchPrice);

            // --- Nếu thỏa điều kiện ngày và ít nhất 1 điều kiện còn lại ---
            if (matchesDate && matchesOther) {
                filteredList.add(new InvoiceDTO(invoice));
            }
        }

        return filteredList;
    }

    public ArrayList<InvoiceDTO> filterInvoicesByDiscountCode(String discountCode) {
        ArrayList<InvoiceDTO> result = new ArrayList<>();
        if (discountCode == null || discountCode.isEmpty()) return result;

        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getDiscountCode(), discountCode)) {
                result.add(new InvoiceDTO(invoice));
            }
        }
        return result;
    }


}
