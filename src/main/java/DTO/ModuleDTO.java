package DTO;

public class ModuleDTO {
    private int id;
    private String name;

    public ModuleDTO() {
    }

    public ModuleDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ModuleDTO(ModuleDTO other) {
        if (other != null) {
            this.id = other.id;
            this.name = other.name;
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
}
