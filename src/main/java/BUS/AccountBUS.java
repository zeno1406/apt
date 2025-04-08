package BUS;

import DAL.AccountDAL;
import DTO.AccountDTO;
import INTERFACE.ServiceAccessCode;
import SERVICE.AuthorizationService;
import UTILS.AvailableUtils;
import UTILS.PasswordUtils;
import UTILS.ValidationUtils;
import java.util.ArrayList;
import java.util.HashSet;
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

    public int delete(Integer id, int employee_roleId, int employeeLoginId) {
        if (id == null || id <= 0 ) return 2;

        // Ngăn chặn xóa tài khoản gốc (employeeId = 1) để bảo vệ hệ thống
        if (id == 1) {
            System.out.println("Không thể xóa tài khoản gốc (employeeId = 1)!");
            return 3;
        }

        // Ngăn chặn tự xóa tài khoản của chính mình
        if (employeeLoginId == id) {
            System.out.println("Không thể tự xóa tài khoản của chính mình!");
            return 4;
        }

        // Nếu người thực hiện không có quyền 28, từ chối
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 28)) return 5;

        // Nếu account đang bị xóa thuộc về employee có role có quyền 28, chỉ cho phép role 1 xóa nó
        if (!AuthorizationService.getInstance().canDeleteUpdateAccount(id, 28) && employee_roleId != 1) return 6;

        if (!AccountDAL.getInstance().delete(id)) {
            return 7;
        }
        arrLocal.removeIf(role -> Objects.equals(role.getEmployeeId(), id));
        return 1;
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

    public int insert(AccountDTO obj, int employee_roleId, int employeeLoginId) {
        if (obj == null || obj.getEmployeeId() <= 0 || isInvalidAccountInput(obj)) {
            return 2;
        }

        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 27)) return 3;

        // Nếu người thực hiện việc tạo tài khoản không phải role 1 thì phải gọi hàm kiểm tra sau để đảm bảo không được tạo tài khoản cho role 1
        if (employee_roleId != 1) {
            if (!AvailableUtils.getInstance().isValidForCreateAccount(obj.getEmployeeId(), 0)) return 4;
        } else {
            if (!AvailableUtils.getInstance().isValidForCreateAccount(obj.getEmployeeId(), 1)) return 4;
        }

        // Không phân biệt hoa thường
        if (isDuplicateUsername(obj.getUsername())) return 5;


        obj.setUsername(obj.getUsername().toLowerCase());
        obj.setPassword(PasswordUtils.getInstance().hashPassword(obj.getPassword()));

        if (!AccountDAL.getInstance().insert(obj)) {
            return 6;
        }

        arrLocal.add(new AccountDTO(obj));
        return 1;
    }

    public int update(AccountDTO obj, int employee_roleId, int employeeLoginId) {
        // Kiểm tra tài khoản có tồn tại và thuộc về 1 employee hợp lệ + Kiểm tra dữ liệu hợp lệ
        if (obj == null || obj.getEmployeeId() <= 0 || employee_roleId <= 0) return 2;


        // Không có quyền 29 thì không chỉnh chính mình hay người khác
        if (!AuthorizationService.getInstance().hasPermission(employeeLoginId, employee_roleId, 29)) return 3;

        if (isInvalidAccountInput(obj)) return 4;

        // Ngăn chặn cập nhật tài khoản gốc nếu không phải chính nó
        if (obj.getEmployeeId() == 1 && employeeLoginId != 1) {
//            System.out.println("Không thể cập nhật tài khoản gốc (employeeId = 1)!");
            return 5;
        }

        // Nếu cập nhật người khác thì account đó phải thuộc về employee có role không có quyền 28
        if (employeeLoginId != obj.getEmployeeId() &&
                !AuthorizationService.getInstance().canDeleteUpdateAccount(obj.getEmployeeId(), 29) && employeeLoginId != 1) {
            return 6;
        }

        if (!AvailableUtils.getInstance().isExistAccount(obj.getEmployeeId())) {
            return 7;
        }

        if (isDuplicateAccount(obj)) return 1;
        obj.setPassword(PasswordUtils.getInstance().hashPassword(obj.getPassword()));
        if (!AccountDAL.getInstance().update(obj)) return 8;

        updateLocalCache(obj);
        return 1;
    }

    private void updateLocalCache(AccountDTO obj) {
        for (int i = 0; i < arrLocal.size(); i++) {
            if (Objects.equals(arrLocal.get(i).getEmployeeId(), obj.getEmployeeId())) {
                arrLocal.set(i, new AccountDTO(obj));
                break;
            }
        }
    }

    private boolean isDuplicateUsername(String username) {
        if (username == null) return false;
        for (AccountDTO account : arrLocal) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDuplicateAccount(AccountDTO obj) {
        AccountDTO existingAcc = getByIdLocal(obj.getEmployeeId());
        // Kiểm tra xem tên, mô tả, và hệ số lương có trùng không
        return existingAcc != null &&
                Objects.equals(existingAcc.getUsername(), obj.getUsername()) &&
                PasswordUtils.getInstance().verifyPassword(obj.getPassword(), existingAcc.getPassword());
    }

    public int checkLogin(String username, String password, int codeAccess) {
        if (codeAccess != ServiceAccessCode.LOGIN_SERVICE || username == null || password == null) return -1;
        for (AccountDTO account : arrLocal) {
            if (account.getUsername().equalsIgnoreCase(username) && PasswordUtils.getInstance().verifyPassword(password, account.getPassword())) {
                return account.getEmployeeId();
            }
        }
        return -1;
    }

    private boolean isInvalidAccountInput(AccountDTO obj) {
        if (obj.getUsername() == null || obj.getPassword() == null) return true;

        ValidationUtils validator = ValidationUtils.getInstance();
        return !validator.validateUsername(obj.getUsername(), 4, 50) ||
                !validator.validatePassword(obj.getPassword(), 6, 255);
    }

    public HashSet<Integer> employeesWithAccount() {
        HashSet<Integer> result = new HashSet<>();
        for (AccountDTO account : arrLocal) {
            result.add(account.getEmployeeId());
        }
        return result;
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
