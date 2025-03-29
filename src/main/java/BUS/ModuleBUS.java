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

    public ModuleDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (ModuleDTO module : arrLocal) {
            if (Objects.equals(module.getId(), id)) {
                return new ModuleDTO(module);
            }
        }
        return null;
    }
}
