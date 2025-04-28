package SERVICE;

import BUS.DetailInvoiceBUS;
import BUS.InvoiceBUS;
import DTO.*;
import INTERFACE.ServiceAccessCode;

import java.util.ArrayList;

public class InvoiceService {
    private static final InvoiceService INSTANCE = new InvoiceService();
    public static InvoiceService getInstance() {
        return INSTANCE;
    }

    public boolean createInvoiceWithDetailInvoice(InvoiceDTO invoice, int employee_roleId, ArrayList<DetailInvoiceDTO> list, int eployeeLoginId) {
        InvoiceBUS invBus = InvoiceBUS.getInstance();
        DetailInvoiceBUS dinvBus = DetailInvoiceBUS.getInstance();

        if (invBus.isLocalEmpty()) invBus.loadLocal();
        if (!invBus.insert(invoice, employee_roleId, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId)) {
            return false;
        }

        if (dinvBus.isLocalEmpty()) dinvBus.loadLocal();
        // Không quan trọng invoiceId của từng thằng trong list. vì sẽ set lại dưới database
        if (!dinvBus.createDetailInvoiceByInvoiceId(invoice.getId(), employee_roleId, list, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId)) {
            // Rollback nếu lỗi
            invBus.delete(invoice.getId(), employee_roleId, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId);
            return false;
        }

        return true;
    }

//    public boolean deleteInvoiceWithDetailInvoice(int invoiceId, int employee_roleId, int eployeeLoginId) {
//        InvoiceBUS invBus = InvoiceBUS.getInstance();
//        DetailInvoiceBUS dinvBus = DetailInvoiceBUS.getInstance();
//
//        if (invBus.isLocalEmpty()) invBus.loadLocal();
//        // Nếu còn xóa dc mới cho xóa
//        InvoiceDTO temp = invBus.getByIdLocal(invoiceId);
//
//        if (!invBus.delete(invoiceId, employee_roleId, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId)) {
//            return false;
//        }
//        if (dinvBus.isLocalEmpty()) dinvBus.loadLocal();
//        ArrayList<DetailInvoiceDTO> tempList = dinvBus.getAllDetailInvoiceByInvoiceIdLocal(invoiceId);
//
//        if (!dinvBus.delete(invoiceId, employee_roleId, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId)) {
//            System.err.println("Failed to delete detail invoice. Attempting to roll back invoice.");
//            // Thêm lại Invoice
//            boolean rollbackRoleSuccess = invBus.insert(temp, 1, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId);
//            if (rollbackRoleSuccess) {
//                // Đặt lại id mới của invoice vừa add để roll back
//                if (tempList != null && !tempList.isEmpty()) { // Kiểm tra tránh lỗi
//                    tempList.get(0).setInvoiceId(temp.getId());
//                    return dinvBus.insertRollbackDetailInvoice(tempList, 1, ServiceAccessCode.INVOICE_DETAILINVOICE_SERVICE, eployeeLoginId);
//                } else {
//                    System.err.println("Rollback successful, but no detail invoices to restore.");
//                }
//            } else {
//                System.err.println("Failed to roll back the invoice.");
//            }
//
//            return false;
//        }
//
//        // Xóa thành công thì phải khôi phục lại số lượng sản phẩm tương ứng
//
//        return true;
//    }

}
