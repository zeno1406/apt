package BUS;

import DAL.ModuleDAL;
import DTO.ModuleDTO;
import java.util.ArrayList;
import java.util.Objects;

public class ModuleBUS extends BaseBUS <ModuleDTO, Integer> {
    private static final ModuleBUS INSTANCE = new ModuleBUS();

    private ModuleBUS() {
    }

    public static ModuleBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<ModuleDTO> getAll() {
        return ModuleDAL.getInstance().getAll();
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Cannot delete module records.");
    }

    public ModuleDTO getByIdLocal(int id) {
        for (ModuleDTO module : arrLocal) {
            if (Objects.equals(module.getId(), id)) {
                return new ModuleDTO(module);
            }
        }
        return null;
    }

    public boolean isDuplicateModuleName(int id, String name) {
        if (name == null) return false;
        for (ModuleDTO module : arrLocal) {
            if (!Objects.equals(module.getId(), id) && Objects.equals(module.getName(), name)) {
                return true;
            }
        }
        return false;
    }
}
