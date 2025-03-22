package BUS;

import DAL.InvoiceDAL;
import DTO.InvoiceDTO;

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
    public boolean delete(Integer id) {
        if (InvoiceDAL.getInstance().delete(id)) {
            arrLocal.removeIf(role -> Objects.equals(role.getId(), id));
            return true;
        }
        return false;
    }

    public InvoiceDTO getByIdLocal(int roleId) {
        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getId(), roleId)) {
                return new InvoiceDTO(invoice);
            }
        }
        return null;
    }

    public boolean insert(InvoiceDTO obj) {
        if (InvoiceDAL.getInstance().insert(obj)) {
            arrLocal.add(new InvoiceDTO(obj));
            return true;
        }
        return false;
    }

    public boolean isValidForDelete(Integer id) {
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại

        for (InvoiceDTO invoice : arrLocal) {
            if (Objects.equals(invoice.getId(), id)) {
                long minutesDifference = Duration.between(invoice.getCreateDate(), now).toMinutes();
                return minutesDifference <= 5;
            }
        }
        return false;
    }

}
