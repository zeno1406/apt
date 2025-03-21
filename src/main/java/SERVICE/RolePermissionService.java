package SERVICE;


import BUS.ModuleBUS;
import BUS.PermissionBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DTO.*;

import java.util.ArrayList;

public class RolePermissionService {
    private static final RolePermissionService INSTANCE = new RolePermissionService();

    public static RolePermissionService getInstance() {
        return INSTANCE;
    }

    public boolean createRoleWithPermissions(RoleDTO role) {
        // Để đảm báo nên load data local trước
        if (RoleBUS.getInstance().getAllRoleLocal().isEmpty()) RoleBUS.getInstance().loadLocal();
        if (!RoleBUS.getInstance().insert(role)) {
            return false;
        }

        // Nếu tạo thành công thì role local cũng đã được thêm theo
        if (RolePermissionBUS.getInstance().getAllRolePermissionLocal().isEmpty()) RolePermissionBUS.getInstance().loadLocal();
        if (!RolePermissionBUS.getInstance().createDefaultPermissionsForRole(role.getId())) {
            // Rollback nếu lỗi
            RoleBUS.getInstance().delete(role.getId());
            return false;
        }
        // Nếu tạo thành công thì role permission local cũng đã được thêm theo

        return true;
    }

    public boolean deleteRoleWithPermissions(int roleId) {
        if (RolePermissionBUS.getInstance().getAllRolePermissionLocal().isEmpty()) RolePermissionBUS.getInstance().loadLocal();
        ArrayList<RolePermissionDTO> temp = RolePermissionBUS.getInstance().getAllRolePermissionByRoleIdLocal(roleId);
        if (!RolePermissionBUS.getInstance().deleteRolePermissionByRoleId(roleId)) {
            return false;
        }
        // Nếu xóa thành công thì role permission local cũng đã tự xóa theo

        if (RoleBUS.getInstance().getAllRoleLocal().isEmpty()) RoleBUS.getInstance().loadLocal();
        if (!RoleBUS.getInstance().delete(roleId)) {
            // Nếu xóa Role thất bại, khôi phục lại RolePermission đã xóa trước đó
            return RolePermissionBUS.getInstance().insertRollbackPermission(temp);
        }

        // Nếu xóa thành công thì role local cũng đã tự xóa theo
        return true;
    }

    public ArrayList<ModulePermissionDTO> getPermissionsGroupedByModule() {
        if (ModuleBUS.getInstance().getAllModuleLocal().isEmpty()) {
            ModuleBUS.getInstance().loadLocal();
        }
        if (PermissionBUS.getInstance().getAllPermissionLocal().isEmpty()) {
            PermissionBUS.getInstance().loadLocal();
        }

        ArrayList<ModuleDTO> arrModule = ModuleBUS.getInstance().getAllModuleLocal();
        ArrayList<PermissionDTO> arrPermission = PermissionBUS.getInstance().getAllPermissionLocal();
        ArrayList<ModulePermissionDTO> modulePermissions = new ArrayList<>();

        for (ModuleDTO module : arrModule) {
            ArrayList<PermissionDTO> permissionsForModule = new ArrayList<>();

            for (PermissionDTO permission : arrPermission) {
                if (permission.getModule_id() == module.getId()) {
                    permissionsForModule.add(permission);
                }
            }

            if (!permissionsForModule.isEmpty()) {
                modulePermissions.add(new ModulePermissionDTO(module, permissionsForModule));
            }
        }

        return modulePermissions;
    }

    public void printPermissionsGroupedByModule() {
        ArrayList<ModulePermissionDTO> modulePermissions = getPermissionsGroupedByModule();

        for (ModulePermissionDTO mp : modulePermissions) {
            System.out.println("Module: " + mp.getModule().getName());
            for (PermissionDTO permission : mp.getPermissions()) {
                System.out.println("    - " + permission.getName() + "|id: " + permission.getId());
            }
        }
    }


}
