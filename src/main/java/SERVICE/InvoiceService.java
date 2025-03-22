//package SERVICE;
//
//import BUS.RoleBUS;
//import BUS.RolePermissionBUS;
//import DTO.InvoiceDTO;
//import DTO.RoleDTO;
//import DTO.RolePermissionDTO;
//
//import java.util.ArrayList;
//
//public class InvoiceService {
//    private static final InvoiceService INSTANCE = new InvoiceService();
//
//    public static InvoiceService getInstance() {
//        return INSTANCE;
//    }
//
//    public boolean createInvoiceWithDetailInvoice(InvoiceDTO invoice) {
//        InvoiceBUS roleBus = RoleBUS.getInstance();
//        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();
//
//        if (roleBus.getAllLocal().isEmpty()) roleBus.loadLocal();
//        if (!roleBus.insert(role)) {
//            return false;
//        }
//
//        if (rolePermissionBus.getAllLocal().isEmpty()) rolePermissionBus.loadLocal();
//        if (!rolePermissionBus.createDefaultPermissionsForRole(role.getId())) {
//            // Rollback nếu lỗi
//            roleBus.delete(role.getId());
//            return false;
//        }
//        return true;
//    }
//
//    public boolean deleteRoleWithPermissions(int roleId) {
//        RoleBUS roleBus = RoleBUS.getInstance();
//        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();
//
//        if (rolePermissionBus.getAllLocal().isEmpty()) rolePermissionBus.loadLocal();
//        ArrayList<RolePermissionDTO> temp = rolePermissionBus.getAllRolePermissionByRoleIdLocal(roleId);
//
//        if (!rolePermissionBus.delete(roleId)) {
//            return false;
//        }
//
//        if (roleBus.getAllLocal().isEmpty()) roleBus.loadLocal();
//        if (!roleBus.delete(roleId)) {
//            // Nếu xóa Role thất bại, khôi phục lại RolePermission đã xóa trước đó
//            boolean rollbackSuccess = rolePermissionBus.insertRollbackPermission(temp);
//            if (!rollbackSuccess) {
//                System.err.println("Rollback failed! Some role permissions might be lost.");
//            }
//            return false;
//        }
//        return true;
//    }
//}
