package SERVICE;

import BUS.AccountBUS;
import BUS.EmployeeBUS;
import DAL.AccountDAL;
import DTO.AccountDTO;
import DTO.EmployeeDTO;

public class LoginRegisterService {
    private static final LoginRegisterService INSTANCE = new LoginRegisterService();

    public static LoginRegisterService getInstance() {
        return INSTANCE;
    }

    public boolean checkLogin(AccountDTO account) {
        if (AccountBUS.getInstance().getAllAccountLocal().isEmpty()) {
            AccountBUS.getInstance().loadLocal();
        }
        if (EmployeeBUS.getInstance().getAllEmployeeLocal().isEmpty()) {
            EmployeeBUS.getInstance().loadLocal();
        }
        EmployeeDTO temp = EmployeeBUS.getInstance().getEmployeeByIdLocal(account.getEmployeeId());
        if (temp != null && temp.isStatus()) {
            return AccountBUS.getInstance().checkLogin(account.getUsername(), account.getPassword());
        }
        return false;
    }

}
