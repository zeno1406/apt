package SERVICE;

import BUS.DetailInvoiceBUS;
import BUS.InvoiceBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DTO.DetailInvoiceDTO;
import DTO.InvoiceDTO;
import DTO.RoleDTO;
import DTO.RolePermissionDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class InvoiceService {
    private static final InvoiceService INSTANCE = new InvoiceService();
    private static final int maxMinute = 5;

    public static InvoiceService getInstance() {
        return INSTANCE;
    }

    public boolean createInvoiceWithDetailInvoice(InvoiceDTO invoice, int employee_roleId, ArrayList<DetailInvoiceDTO> list) {
        InvoiceBUS invBus = InvoiceBUS.getInstance();
        DetailInvoiceBUS dinvBus = DetailInvoiceBUS.getInstance();

        if (invBus.isLocalEmpty()) invBus.loadLocal();
        if (!invBus.insert(invoice, employee_roleId)) {
            return false;
        }

        if (dinvBus.isLocalEmpty()) dinvBus.loadLocal();
        // Không quan trọng invoiceId của từng thằng trong list. vì sẽ set lại dưới database
        if (!dinvBus.createDetailInvoiceByInvoiceId(invoice.getId(), employee_roleId, list)) {
            // Rollback nếu lỗi
            invBus.delete(invoice.getId(), employee_roleId);
            return false;
        }
        // Thêm thành công thì phải lưu statistic tương ứng

        return true;
    }

    public boolean deleteInvoiceWithDetailInvoice(int invoiceId, int employee_roleId) {
        InvoiceBUS invBus = InvoiceBUS.getInstance();
        DetailInvoiceBUS dinvBus = DetailInvoiceBUS.getInstance();

        if (invBus.isLocalEmpty()) invBus.loadLocal();
        // Nếu còn xóa dc mới cho xóa
        if (!invBus.isValidForDelete(invoiceId, maxMinute)) return false;
        InvoiceDTO temp = invBus.getByIdLocal(invoiceId);


        if (!invBus.delete(invoiceId, employee_roleId)) {
            return false;
        }
        if (dinvBus.isLocalEmpty()) dinvBus.loadLocal();
        ArrayList<DetailInvoiceDTO> tempList = dinvBus.getAllDetailInvoiceByInvoiceIdLocal(invoiceId);

        if (!dinvBus.delete(invoiceId, employee_roleId)) {
            System.err.println("Failed to delete detail invoice. Attempting to roll back invoice.");
            // Thêm lại Invoice
            boolean rollbackRoleSuccess = invBus.insert(temp, 1);
            if (rollbackRoleSuccess) {
                // Đặt lại id mới của invoice vừa add để roll back
                tempList.get(0).setInvoiceId(temp.getId());
                return dinvBus.insertRollbackDetailInvoice(tempList, 1);

            } else {
                System.err.println("Failed to roll back the invoice.");
            }

            return false;
        }

        // Xóa thành công thì phải khôi phục lại số lượng sản phẩm tương ứng

        return true;
    }

}
