package SERVICE;

import BUS.DetailImportBUS;
import BUS.DetailInvoiceBUS;
import BUS.ImportBUS;
import BUS.InvoiceBUS;
import DTO.DetailImportDTO;
import DTO.DetailInvoiceDTO;
import DTO.ImportDTO;
import DTO.InvoiceDTO;
import INTERFACE.ServiceAccessCode;

import java.util.ArrayList;

public class ImportService {
    private static final ImportService INSTANCE = new ImportService();

    public static ImportService getInstance() {
        return INSTANCE;
    }

    public boolean createImportWithDetailImport(ImportDTO imports, int employee_roleId, ArrayList<DetailImportDTO> list, int eployeeLoginId) {
        ImportBUS impBus = ImportBUS.getInstance();
        DetailImportBUS dimpBus = DetailImportBUS.getInstance();

        if (impBus.isLocalEmpty()) impBus.loadLocal();
        if (!impBus.insert(imports, employee_roleId, ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE, eployeeLoginId))
            return false;

        if (dimpBus.isLocalEmpty()) dimpBus.loadLocal();
        // Không quan trọng invoiceId của từng thằng trong list. vì sẽ set lại dưới database
        if (!dimpBus.createDetailImportByImportId(imports.getId(), employee_roleId, list, ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE, eployeeLoginId)) {
            // Rollback nếu lỗi
            impBus.delete(imports.getId(), employee_roleId, ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE, eployeeLoginId);
            return false;
        }

        return true;
    }
}
