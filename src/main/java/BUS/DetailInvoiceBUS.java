package BUS;

import DAL.DetailInvoiceDAL;
import DTO.DetailInvoiceDTO;

import java.util.ArrayList;
import java.util.Objects;

public class DetailInvoiceBUS extends BaseBUS<DetailInvoiceDTO, Integer>{
    private static final DetailInvoiceBUS INSTANCE = new DetailInvoiceBUS();

    public static DetailInvoiceBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<DetailInvoiceDTO> getAll() {
        return DetailInvoiceDAL.getInstance().getAll();
    }

    @Override
    public boolean delete(Integer id, int employee_roleId) {
        if (id == null || id <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 14)) {
            return false;
        }

        if (!DetailInvoiceDAL.getInstance().deleteAllDetailInvoiceByInvoiceId(id)) {
            return false;
        }
        arrLocal.removeIf(role -> Objects.equals(role.getInvoiceId(), id));
        return true;
    }

    public ArrayList<DetailInvoiceDTO> getAllDetailInvoiceByInvoiceIdLocal(int invoiceId) {
        if (invoiceId <= 0) return null;
        ArrayList<DetailInvoiceDTO> result = new ArrayList<>();
        for (DetailInvoiceDTO iv : arrLocal) {
            if (Objects.equals(iv.getInvoiceId(), invoiceId)) {
                result.add(iv);
            }
        }
        return result;
    }

    public boolean createDetailInvoiceByInvoiceId(int invoiceId, int employee_roleId, ArrayList<DetailInvoiceDTO> list) {
        if (employee_roleId <= 0 || !hasPermission(employee_roleId, 13) || list == null || list.isEmpty() || invoiceId <= 0) {
            return false;
        }
        if (!DetailInvoiceDAL.getInstance().insertAllDetailInvoiceByInvoiceId(invoiceId, list)) {
            return false;
        }
        ArrayList<DetailInvoiceDTO> newDetailInvoice = DetailInvoiceDAL.getInstance().getAllDetailInvoiceByInvoiceId(invoiceId);
        arrLocal.addAll(new ArrayList<>(newDetailInvoice));
        return true;
    }

    public boolean insertRollbackDetailInvoice(ArrayList<DetailInvoiceDTO> list, int employee_roleId) {
        if (list == null || list.isEmpty() || employee_roleId <= 0 || !hasPermission(employee_roleId, 14)) {
            return false;
        }
        if (!DetailInvoiceDAL.getInstance().insertAllDetailInvoiceByInvoiceId(list.get(0).getInvoiceId(), list)) {
            return false;
        }
        ArrayList<DetailInvoiceDTO> newDetailInvoice = DetailInvoiceDAL.getInstance().getAllDetailInvoiceByInvoiceId(list.get(0).getInvoiceId());
        arrLocal.addAll(new ArrayList<>(newDetailInvoice));
        return true;
    }
}
