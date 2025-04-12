package BUS;

import DAL.DetailImportDAL;
import DTO.DetailImportDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;

import java.util.ArrayList;
import java.util.Objects;

public class DetailImportBUS extends BaseBUS<DetailImportDTO, Integer>{
    private static final DetailImportBUS INSTANCE = new DetailImportBUS();

    public static DetailImportBUS getInstance() {
        return INSTANCE;
    }
    @Override
    public ArrayList<DetailImportDTO> getAll() {
        return DetailImportDAL.getInstance().getAll();
    }

    public boolean delete(Integer id, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE || id == null || id <= 0) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 16)) {
            return false;
        }

        if (!DetailImportDAL.getInstance().deleteAllDetailImportByImportId(id)) {
            return false;
        }
        arrLocal.removeIf(role -> Objects.equals(role.getImportId(), id));
        return true;
    }

    public ArrayList<DetailImportDTO> getAllDetailImportByImportIdLocal(int importId) {
        if (importId <= 0) return null;
        ArrayList<DetailImportDTO> result = new ArrayList<>();
        for (DetailImportDTO di : arrLocal) {
            if (Objects.equals(di.getImportId(), importId)) {
                result.add(di);
            }
        }
        return result;
    }

    public boolean createDetailImportByImportId(int importId, int employee_roleId, ArrayList<DetailImportDTO> list, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE || list == null || list.isEmpty() || importId <= 0) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 15) ) {
            return false;
        }
        if (!DetailImportDAL.getInstance().insertAllDetailImportByImportId(importId, list)) {
            return false;
        }
        ArrayList<DetailImportDTO> newDetailImport = DetailImportDAL.getInstance().getAllDetailImportByImportId(importId);
        arrLocal.addAll(new ArrayList<>(newDetailImport));
        return true;
    }

    public boolean insertRollbackDetailImport(ArrayList<DetailImportDTO> list, int employee_roleId, int codeAccess, int employeeLoginId) {
        if (codeAccess != ServiceAccessCode.IMPORT_DETAILIMPORT_SERVICE || list == null || list.isEmpty()) return false;
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 16)) {
            return false;
        }
        if (!DetailImportDAL.getInstance().insertAllDetailImportByImportId(list.get(0).getImportId(), list)) {
            return false;
        }
        ArrayList<DetailImportDTO> newDetailImport = DetailImportDAL.getInstance().getAllDetailImportByImportId(list.get(0).getImportId());
        arrLocal.addAll(new ArrayList<>(newDetailImport));
        return true;
    }
}
