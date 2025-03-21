package BUS;

import DTO.RoleDTO;
import INTERFACE.IBUS;

import java.util.ArrayList;

public class RoleBus implements IBUS<RoleDTO, Integer> {
    private static final RoleBus INSTANCE = new RoleBus();

    public static RoleBus getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<RoleDTO> getAll() {
        return null;
    }

    @Override
    public RoleDTO getById(Integer id) {
        return null;
    }

    @Override
    public boolean insert(RoleDTO obj) {
        return false;
    }

    @Override
    public boolean update(RoleDTO obj) {
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }
}

