package DTO;

public class PermissionDTO {
    private int id;
    private String name;
    private int module_id;

    public PermissionDTO() {
    }

    public PermissionDTO(int id, String name, int module_id) {
        this.id = id;
        this.name = name;
        this.module_id = module_id;
    }

    public PermissionDTO(PermissionDTO other) {
        if (other != null) {
            this.id = other.id;
            this.name = other.name;
            this.module_id = other.module_id;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModule_id() {
        return module_id;
    }

    public void setModule_id(int module_id) {
        this.module_id = module_id;
    }
}
