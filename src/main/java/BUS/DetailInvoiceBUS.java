package BUS;

import DAL.DetailInvoiceDAL;
import DAL.InvoiceDAL;
import DAL.RolePermissionDAL;
import DTO.DetailImportDTO;
import DTO.DetailInvoiceDTO;
import DTO.InvoiceDTO;
import DTO.RolePermissionDTO;
import UTILS.PasswordUtils;

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
    public boolean delete(Integer id) {
        if (InvoiceDAL.getInstance().delete(id)) {
            arrLocal.removeIf(role -> Objects.equals(role.getInvoiceId(), id));
            return true;
        }
        return false;
    }

    public ArrayList<DetailInvoiceDTO> getAllDetailInvoiceByInvoiceIdLocal(int invoiceId) {
        ArrayList<DetailInvoiceDTO> result = new ArrayList<>();
        for (DetailInvoiceDTO iv : arrLocal) {
            if (Objects.equals(iv.getInvoiceId(), invoiceId)) {
                result.add(iv);
            }
        }
        return result;
    }

    public boolean createDetailInvoiceByInvoiceId(int invoiceId, ArrayList<DetailInvoiceDTO> list) {
        if (DetailInvoiceDAL.getInstance().insertDetailInvoiceByInvoiceId(list)) {
            // Nếu null tức là có thêm mới thành công nhưng local chưa có => chỉ cần bổ sung thêm không cần tải lại
            arrLocal.addAll(new ArrayList<>(list));
            return true;
        }
        return false;
    }

    public boolean insertDetailInvoiceByInvoiceId(ArrayList<DetailInvoiceDTO> list) {
        if (list == null || list.isEmpty()) {
            return false; // Không có gì để khôi phục
        }

        if (DetailInvoiceDAL.getInstance().insertDetailInvoiceByInvoiceId(list)) {
            // Nếu null tức là có thêm mới thành công nhưng local chưa có => chỉ cần bổ sung thêm không cần tải lại
            if (getAllDetailInvoiceByInvoiceIdLocal(list.get(0).getInvoiceId()).isEmpty()) {
                arrLocal.addAll(new ArrayList<>(list));
            }
            return true;
        }
        return false;
    }
}
