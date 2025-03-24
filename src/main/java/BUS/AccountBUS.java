package BUS;

import DAL.AccountDAL;
import DAL.ProductDAL;
import DAL.RoleDAL;
import DTO.AccountDTO;
import DTO.RoleDTO;
import UTILS.PasswordUtils;
import UTILS.ValidationUtils;

import java.util.ArrayList;
import java.util.Objects;

public class AccountBUS extends BaseBUS <AccountDTO, Integer> {
    private static final AccountBUS INSTANCE = new AccountBUS();

    private AccountBUS() {
    }

    public static AccountBUS getInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<AccountDTO> getAll() {
        return AccountDAL.getInstance().getAll();
    }

    @Override
    public boolean delete(Integer id, int employee_roleId) {
        if (id == null || id <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 28)) {
            return false;
        }

        if (!AccountDAL.getInstance().delete(id)) {
            return false;
        }
        arrLocal.removeIf(role -> Objects.equals(role.getEmployeeId(), id));
        return true;
    }

    public AccountDTO getByIdLocal(int id) {
        if (id <= 0) return null;
        for (AccountDTO account : arrLocal) {
            if (Objects.equals(account.getEmployeeId(), id)) {
                return new AccountDTO(account);
            }
        }
        return null;
    }

    public boolean insert(AccountDTO obj, int employee_roleId) {
        if (obj == null || obj.getEmployeeId() <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 27) || !validateAccountInput(obj)) {
            return false;
        }

        obj.setUsername(obj.getUsername().toLowerCase());
        obj.setPassword(PasswordUtils.getInstance().hashPassword(obj.getPassword()));

        if (isDuplicateUsername(0, obj.getUsername()) || !AccountDAL.getInstance().insert(obj)) {
            return false;
        }

        arrLocal.add(new AccountDTO(obj));
        return true;
    }

    public boolean update(AccountDTO obj, int employee_roleId, String username) {
        if (obj == null || obj.getEmployeeId() <= 0 || employee_roleId <= 0 || !hasPermission(employee_roleId, 29) || !validateAccountInput(obj) || obj.getUsername().equals(username)) {
            return false;
        }

        obj.setPassword(PasswordUtils.getInstance().hashPassword(obj.getPassword()));

        if (!AccountDAL.getInstance().update(obj)) {
            return false;
        }

        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getEmployeeId(), obj.getEmployeeId())) {
                arrLocal.set(i, new AccountDTO(obj));
                return true;
            }
        }

        return false;
    }

    public boolean isDuplicateUsername(int id, String username) {
        if (username == null) return false;
        for (AccountDTO account : arrLocal) {
            if (!Objects.equals(account.getEmployeeId(), id) && Objects.equals(account.getUsername(), username)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkLogin(String username, String password) {
        if (username == null || password == null) return false;
        for (AccountDTO account : arrLocal) {
            if (account.getUsername().equals(username) && PasswordUtils.getInstance().verifyPassword(password, account.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateAccountInput(AccountDTO obj) {
        if (obj.getUsername() == null || obj.getPassword() == null) return false;

        ValidationUtils validator = ValidationUtils.getInstance();
        return validator.validateUsername(obj.getUsername(), 4, 255)
                && validator.validatePassword(obj.getPassword(), 6, 255);
    }
}
