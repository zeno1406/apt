package BUS;

import DAL.AccountDAL;
import DTO.AccountDTO;
import DTO.EmployeeDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;
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

    public boolean delete(Integer id, int employee_roleId, int employeeLoginId) {
        if (id == null || id <= 0 ) return false;

        // Ngăn chặn xóa tài khoản gốc (employeeId = 1) để bảo vệ hệ thống
        if (id == 1) {
            System.out.println("Không thể xóa tài khoản gốc (employeeId = 1)!");
            return false;
        }

        // Ngăn chặn tự xóa tài khoản của chính mình
        if (employeeLoginId == id) {
            System.out.println("Không thể tự xóa tài khoản của chính mình!");
            return false;
        }

        // Nếu người thực hiện không có quyền 28, từ chối
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 28)) return false;

        // Nếu account đang bị xóa thuộc về employee có role có quyền 28, chỉ cho phép role 1 xóa nó
        if (AuthorizationService.getInstance().canDeleteUpdateAccount(id, 28)  && employee_roleId != 1 ) return false;

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

    public boolean insert(AccountDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getEmployeeId() <= 0 || !AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 27) || isInvalidAccountInput(obj)) {
            return false;
        }

        if (!AvailableUtils.getInstance().isValidForCreateAccount(obj.getEmployeeId())) return false;

        obj.setUsername(obj.getUsername().toLowerCase());
        obj.setPassword(PasswordUtils.getInstance().hashPassword(obj.getPassword()));

        if (isDuplicateUsername(0, obj.getUsername()) || !AccountDAL.getInstance().insert(obj)) {
            return false;
        }

        arrLocal.add(new AccountDTO(obj));
        return true;
    }

    public boolean update(AccountDTO obj, int employee_roleId, int employeeLoginId) {
        // Kiểm tra tài khoản có tồn tại và thuộc về 1 employee hợp lệ + Kiểm tra dữ liệu hợp lệ
        if (obj == null || obj.getEmployeeId() <= 0 || employee_roleId <= 0 || isInvalidAccountInput(obj)) return false;


        // Không có quyền 29 thì không chỉnh chính mình hay người khác
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 29)) return false;

        // Ngăn chặn cập nhật tài khoản gốc nếu không phải chính nó
        if (obj.getEmployeeId() == 1 && employeeLoginId != 1) {
            System.out.println("Không thể cập nhật tài khoản gốc (employeeId = 1)!");
            return false;
        }

        // Nếu cập nhật người khác thì account đó phải thuộc về employee có role không có quyền 28
        if (employeeLoginId != obj.getEmployeeId() &&
                !AuthorizationService.getInstance().canDeleteUpdateAccount(obj.getEmployeeId(), 29)) {
            System.err.println("1");
            return false;
        }

        if (!AvailableUtils.getInstance().isExistAccount(obj.getEmployeeId())) {
            System.err.println("1");
            return false;
        }

        // Chỉ hash mật khẩu nếu mật khẩu mới khác mật khẩu cũ
        String oldHashedPassword = getByIdLocal(obj.getEmployeeId()).getPassword();
        if (!PasswordUtils.getInstance().verifyPassword(obj.getPassword(), oldHashedPassword)) {
            obj.setPassword(PasswordUtils.getInstance().hashPassword(obj.getPassword()));
        }

        if (!AccountDAL.getInstance().update(obj)) return false;

        updateLocalCache(obj);
        return true;
    }

    private void updateLocalCache(AccountDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getEmployeeId(), obj.getEmployeeId())) {
                arrLocal.set(i, new AccountDTO(obj));
                break;
            }
        }
    }

    private boolean isDuplicateUsername(int id, String username) {
        if (username == null) return false;
        for (AccountDTO account : arrLocal) {
            if (!Objects.equals(account.getEmployeeId(), id) && Objects.equals(account.getUsername(), username)) {
                return true;
            }
        }
        return false;
    }

    public int checkLogin(String username, String password, int codeAccess) {
        if (codeAccess != ServiceAccessCode.LOGIN_SERVICE || username == null || password == null) return -1;
        for (AccountDTO account : arrLocal) {
            if (account.getUsername().equals(username) && PasswordUtils.getInstance().verifyPassword(password, account.getPassword())) {
                return account.getEmployeeId();
            }
        }
        return -1;
    }

    private boolean isInvalidAccountInput(AccountDTO obj) {
        if (obj.getUsername() == null || obj.getPassword() == null) return true;

        ValidationUtils validator = ValidationUtils.getInstance();
        return !validator.validateUsername(obj.getUsername(), 4, 255) ||
                !validator.validatePassword(obj.getPassword(), 6, 255);
    }

    public ArrayList<AccountDTO> filterAccounts(String searchBy, String keyword) {
        ArrayList<AccountDTO> filteredList = new ArrayList<>();

        if (keyword == null) keyword = "";
        if (searchBy == null) searchBy = "";

        keyword = keyword.trim().toLowerCase();

        for (AccountDTO acc : arrLocal) {
            boolean matchesSearch = true;

            // Kiểm tra null tránh lỗi khi gọi .toLowerCase()
            String employeeId = String.valueOf(acc.getEmployeeId());
            String username = acc.getUsername() != null ? acc.getUsername().toLowerCase() : "";

            if (!keyword.isEmpty()) {
                switch (searchBy) {
                    case "Mã nhân viên" -> matchesSearch = employeeId.contains(keyword);
                    case "Tài khoản" -> matchesSearch = username.contains(keyword);
                }
            }

            // Chỉ thêm vào danh sách nếu thỏa tất cả điều kiện
            if (matchesSearch) {
                filteredList.add(acc);
            }
        }

        return filteredList;
    }
}
