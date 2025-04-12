package BUS;

import DAL.DetailInvoiceDAL;
import DTO.DetailInvoiceDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;

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

    public boolean delete(Integer id, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE || id == null || id <= 0) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 14)) return false;

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

    public boolean createDetailInvoiceByInvoiceId(int invoiceId, int employee_roleId, ArrayList<DetailInvoiceDTO> list, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE || list == null || list.isEmpty() || invoiceId <= 0) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 13) ) {
            return false;
        }
        if (!DetailInvoiceDAL.getInstance().insertAllDetailInvoiceByInvoiceId(invoiceId, list)) {
            return false;
        }
        ArrayList<DetailInvoiceDTO> newDetailInvoice = DetailInvoiceDAL.getInstance().getAllDetailInvoiceByInvoiceId(invoiceId);
        arrLocal.addAll(new ArrayList<>(newDetailInvoice));
        return true;
    }

    public boolean insertRollbackDetailInvoice(ArrayList<DetailInvoiceDTO> list, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE || list == null || list.isEmpty()) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 14)) {
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
