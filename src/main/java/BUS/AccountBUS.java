package BUS;

import DAL.AccountDAL;
import DTO.AccountDTO;
import INTERFACE.IBUS;
import SERVICE.PasswordUtils;

import java.util.ArrayList;
import java.util.Objects;

public class AccountBUS implements IBUS<AccountDTO, Integer> {
    private final ArrayList<AccountDTO> arrAccount = new ArrayList<>();
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
    public AccountDTO getById(Integer id) {
        return AccountDAL.getInstance().getById(id);
    }

    @Override
    public boolean insert(AccountDTO obj) {
        if (obj == null || isDuplicateUsername(obj.getEmployeeId(), obj.getUsername())) return false;
        if (AccountDAL.getInstance().insert(obj)) {
            arrAccount.add(new AccountDTO(obj));
            return true;
        }
        return false;
    }

    @Override
    public boolean update(AccountDTO obj) {
        if (obj == null || obj.getEmployeeId() <= 0 || isDuplicateUsername(obj.getEmployeeId(), obj.getUsername())) return false;

        for (int i = 0; i < arrAccount.size(); i++) {
            if (Objects.equals(arrAccount.get(i).getEmployeeId(), obj.getEmployeeId())) {
                if (AccountDAL.getInstance().update(obj)) {
                    arrAccount.set(i, new AccountDTO(obj));
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (AccountDAL.getInstance().delete(id)) {
            arrAccount.removeIf(role -> Objects.equals(role.getEmployeeId(), id));
            return true;
        }
        return false;
    }

    // ===================== CÁC HÀM CHỈ LÀM VIỆC VỚI LOCAL =====================

    @Override
    public void loadLocal() {
        arrAccount.clear();
        arrAccount.addAll(getAll());
    }

    public ArrayList<AccountDTO> getAllAccountLocal() {
        return new ArrayList<>(arrAccount);
    }

    public AccountDTO getAccountByIdLocal(int id) {
        for (AccountDTO account : arrAccount) {
            if (Objects.equals(account.getEmployeeId(), id)) {
                return new AccountDTO(account);
            }
        }
        return null;
    }

    public boolean isDuplicateUsername(int id, String username) {
        if (username == null) return false;
        for (AccountDTO account : arrAccount) {
            if (!Objects.equals(account.getEmployeeId(), id) && Objects.equals(account.getUsername(), username)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkLogin(String username, String password) {
        if (username == null || password == null) return false;
        for (AccountDTO account : arrAccount) {
            if (account.getUsername().equals(username) && PasswordUtils.getInstance().verifyPassword(password, account.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
