package SERVICE;

import BUS.AccountBUS;
import BUS.EmployeeBUS;
import DTO.AccountDTO;
import DTO.EmployeeDTO;
import INTERFACE.ServiceAccessCode;

public class LoginService {
    private static final LoginService INSTANCE = new LoginService();

    public static LoginService getInstance() {
        return INSTANCE;
    }

    public boolean checkLogin(AccountDTO account) {
        EmployeeBUS empBus = EmployeeBUS.getInstance();
        AccountBUS accBus = AccountBUS.getInstance();

        // Load Account nếu danh sách trống
        if (accBus.isLocalEmpty()) accBus.loadLocal();
        if (accBus.isLocalEmpty()) return false; // Kiểm tra lại sau khi load

        // Kiểm tra đăng nhập
        int currAcc = accBus.checkLogin(account.getUsername(), account.getPassword(), ServiceAccessCode.LOGIN_SERVICE);
        if (currAcc == -1)
            return false;

        // Load Employee nếu danh sách trống
        if (empBus.isLocalEmpty()) empBus.loadLocal();
        if (empBus.isLocalEmpty()) return false;

        // Kiểm tra Employee
        EmployeeDTO employee = empBus.getByIdLocal(currAcc);
        SessionManagerService.getInstance().setLoggedInEmployee(EmployeeBUS.getInstance().getByIdLocal(employee.getId()));
        return employee.isStatus() && employee.getRoleId() != 0 && SessionManagerService.getInstance().numAllowedModules() != 0;
    }



//    public boolean registerAccount(AccountDTO account, int employee_id, int employeeLoginId) {
//        EmployeeBUS empBus = EmployeeBUS.getInstance();
//        AccountBUS accBus = AccountBUS.getInstance();
//
//        if (empBus.isLocalEmpty()) empBus.loadLocal();
//        if (empBus.isLocalEmpty()) {
//            System.out.println("Register failed: No employee records found.");
//            return false;
//        }
//
//        EmployeeDTO temp = empBus.getByIdLocal(employee_id);
//        if (temp == null || !temp.isStatus()) {
//            System.out.println("Register failed: Employee is inactive or not found.");
//            return false;
//        }
//
//        if (accBus.getByIdLocal(employee_id) != null) {
//            System.out.println("Register failed: Employee already has an account.");
//            return false;
//        }
//
//        return accBus.insert(account, employee_id, employeeLoginId);
//    }

}
