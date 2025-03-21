package BUS;

import DAL.ModuleDAL;
import DTO.ModuleDTO;
import INTERFACE.IBUS;

import java.util.ArrayList;
import java.util.Objects;

public class ModuleBUS implements IBUS<ModuleDTO, Integer> {
    private final ArrayList<ModuleDTO> arrModule = new ArrayList<>();
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
    public ModuleDTO getById(Integer id) {
        throw new UnsupportedOperationException("Cannot get by module id records.");
    }

    @Override
    public boolean insert(ModuleDTO obj) {
        throw new UnsupportedOperationException("Cannot insert module records.");
    }

    @Override
    public boolean update(ModuleDTO obj) {
        throw new UnsupportedOperationException("Cannot update module records.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Cannot delete module records.");
    }

    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================

    @Override
    public void loadLocal() {
        arrModule.clear();
        arrModule.addAll(getAll());
    }

    public ArrayList<ModuleDTO> getAllModuleLocal() {
        return new ArrayList<>(arrModule);
    }

    public ModuleDTO getModuleByIdLocal(int id) {
        for (ModuleDTO module : arrModule) {
            if (Objects.equals(module.getId(), id)) {
                return new ModuleDTO(module);
            }
        }
        return null;
    }

    public boolean isDuplicateModuleName(int id, String name) {
        if (name == null) return false;
        for (ModuleDTO module : arrModule) {
            if (!Objects.equals(module.getId(), id) && Objects.equals(module.getName(), name)) {
                return true;
            }
        }
        return false;
    }
}
