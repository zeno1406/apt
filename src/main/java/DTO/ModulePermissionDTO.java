package DTO;

import java.util.ArrayList;

public class ModulePermissionDTO {
    private ModuleDTO module;
    private ArrayList<PermissionDTO> permissions;

    public ModulePermissionDTO(ModuleDTO module, ArrayList<PermissionDTO> permissions) {
        this.module = module;
        this.permissions = permissions;
    }

    public ModuleDTO getModule() {
        return module;
    }

    public ArrayList<PermissionDTO> getPermissions() {
        return permissions;
    }
}

