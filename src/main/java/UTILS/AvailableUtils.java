package UTILS;

import BUS.*;
import DTO.CategoryDTO;
import DTO.InvoiceDTO;

import java.util.ArrayList;

public class AvailableUtils {
    private static final AvailableUtils INSTANCE = new AvailableUtils();

    public static AvailableUtils getInstance() {
        return INSTANCE;
    }

    public boolean isValidRole(int roleId) {
        if (roleId <= 0) return false;

        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();

        return RoleBUS.getInstance().getByIdLocal(roleId) != null && isValidRoleWithPermissions(roleId);
    }

    public boolean isValidForCreateAccount(int employeeId, int type) {
        if (employeeId <= 0 || (type != 0 && type != 1)) return false;

        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();
        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();
        if (type == 0) {
            return AccountBUS.getInstance().getByIdLocal(employeeId) == null && EmployeeBUS.getInstance().getByIdLocal(employeeId).getRoleId() != 1;
        } else {
            return AccountBUS.getInstance().getByIdLocal(employeeId) == null;
        }
    }

    public boolean isExistAccount(int employeeId) {
        if (employeeId <= 0) return false;

        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();

        // Kiểm tra xem tài khoản có tồn tại không
        return AccountBUS.getInstance().getByIdLocal(employeeId) != null;
    }

    public boolean isValidRoleWithPermissions(int roleId) {
        if (roleId <= 0) return false;
        if (PermissionBUS.getInstance().isLocalEmpty()) PermissionBUS.getInstance().loadLocal();
        if (RolePermissionBUS.getInstance().isLocalEmpty()) RolePermissionBUS.getInstance().loadLocal();

        int rolePermissionCount = RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(roleId).size();
        int totalPermissions = PermissionBUS.getInstance().getAllLocal().size();
        // Nếu role không có đủ quyền, từ chối ngay
        return (rolePermissionCount == totalPermissions);
    }

    public boolean isValidCategory(int categoryId) {
        if (categoryId <= 0) return false;

        if (CategoryBUS.getInstance().isLocalEmpty()) CategoryBUS.getInstance().loadLocal();
        CategoryDTO temp = CategoryBUS.getInstance().getByIdLocal(categoryId);
        return temp != null && temp.isStatus();
    }

    public boolean isNotUsedDiscount(String discountCode) {
        if (discountCode == null || discountCode.isEmpty()) {
            return false;
        }

        // Load local nếu chưa có
        InvoiceBUS invoiceBUS = InvoiceBUS.getInstance();
        if (invoiceBUS.isLocalEmpty()) {
            invoiceBUS.loadLocal();
        }

        // Kiểm tra xem có hóa đơn nào dùng mã này không
        ArrayList<InvoiceDTO> invoicesUsingDiscount = invoiceBUS.filterInvoicesByDiscountCode(discountCode);

        // Nếu không có => được phép xóa
        return invoicesUsingDiscount.isEmpty();
    }

}
