package BUS;

import DAL.AccountDAL;
import DTO.AccountDTO;
import UTILS.PasswordUtils;
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
    public boolean delete(Integer id) {
        if (AccountDAL.getInstance().delete(id)) {
            arrLocal.removeIf(role -> Objects.equals(role.getEmployeeId(), id));
            return true;
        }
        return false;
    }

    public AccountDTO getByIdLocal(int id) {
        for (AccountDTO account : arrLocal) {
            if (Objects.equals(account.getEmployeeId(), id)) {
                return new AccountDTO(account);
            }
        }
        return null;
    }

    public boolean insert(AccountDTO obj) {
        if (obj == null || isDuplicateUsername(obj.getEmployeeId(), obj.getUsername())) return false;
        if (AccountDAL.getInstance().insert(obj)) {
            arrLocal.add(new AccountDTO(obj));
            return true;
        }
        return false;
    }

    public boolean update(AccountDTO obj) {
        if (obj == null || obj.getEmployeeId() <= 0 || isDuplicateUsername(obj.getEmployeeId(), obj.getUsername())) return false;

        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getEmployeeId(), obj.getEmployeeId())) {
                if (AccountDAL.getInstance().update(obj)) {
                    arrLocal.set(i, new AccountDTO(obj));
                    return true;
                }
                return false;
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
}
