package BUS;

import DAL.InvoiceDAL;
import DTO.InvoiceDTO;
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

    @Override
    public boolean delete(Integer id, int employee_roleId) {
        if (id == null || id <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 14)) {
            return false;
        }

        if (!InvoiceDAL.getInstance().delete(id)) {
            return false;
        }
        arrLocal.removeIf(role -> Objects.equals(role.getId(), id));
        return true;
    }

    public InvoiceDTO getByIdLocal(int roleId) {
        if (roleId <= 0) return null;
        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getId(), roleId)) {
                return new InvoiceDTO(invoice);
            }
        }
        return null;
    }

    public boolean insert(InvoiceDTO obj, int employee_roleId) {
        if (obj == null || employee_roleId <= 0 || !hasPermission(employee_roleId, 13) || !validateInvoiceInput(obj)) {
            return false;
        }

        obj.setCreateDate(LocalDateTime.now());

        if (InvoiceDAL.getInstance().insert(obj)) {
            arrLocal.add(new InvoiceDTO(obj));
            return true;
        }
        return false;
    }

    private boolean validateInvoiceInput(InvoiceDTO obj) {
        if (obj.getEmployeeId() <= 0 || obj.getCustomerId() <= 0) return false;

        if (obj.getDiscountCode() == null && obj.getDiscountAmount().compareTo(BigDecimal.ZERO) != 0) {
            return false;
        }

        ValidationUtils validator = ValidationUtils.getInstance();
        return validator.validateBigDecimal(obj.getTotalPrice(), 10, 2, false)
                && validator.validateBigDecimal(obj.getDiscountAmount(), 10, 2, false);
    }

    public boolean isValidForDelete(Integer id, int maxMinute) {
        if (id == null || id <= 0 || maxMinute < 0) return false;

        LocalDateTime now = LocalDateTime.now();

        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getId(), id)) {
                long minutesDifference = Duration.between(invoice.getCreateDate(), now).toMinutes();
                return minutesDifference <= maxMinute;
            }
        }
        return false;
    }

}
