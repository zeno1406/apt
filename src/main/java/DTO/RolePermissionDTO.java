package DTO;

public class RolePermissionDTO {
    private int roleId;
    private int permissionId;
    private boolean status;

    public RolePermissionDTO(int roleId, int permissionId, boolean status) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.status = status;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
