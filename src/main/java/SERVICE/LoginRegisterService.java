//package SERVICE;
//
//import DTO.EmployeeDTO;
//
//public class LoginRegisterService {
//    private static final LoginRegisterService INSTANCE = new LoginRegisterService();
//
//    public static LoginRegisterService getInstance() {
//        return INSTANCE;
//    }
//
//    public boolean checkLogin(AccountDTO account) {
//        EmployeeBUS empBus = EmployeeBUS.getInstance();
//        AccountBUS accBus = AccountBUS.getInstance();
//
//        if (empBus.isLocalEmpty()) {
//            empBus.loadLocal();
//            if (empBus.isLocalEmpty()) return false;
//        }
//
//        EmployeeDTO temp = empBus.getByIdLocal(account.getEmployeeId());
//        if (temp != null && temp.isStatus()) {
//            if (accBus.isLocalEmpty()) {
//                accBus.loadLocal();
//                if (accBus.isLocalEmpty()) return false;
//            }
//            return accBus.checkLogin(account.getUsername(), account.getPassword());
//        }
//        return false;
//    }
//
//    public boolean registerAccount(AccountDTO account) {
//        EmployeeBUS empBus = EmployeeBUS.getInstance();
//        AccountBUS accBus = AccountBUS.getInstance();
//
//        if (empBus.isLocalEmpty()) {
//            empBus.loadLocal();
//            if (empBus.isLocalEmpty()) return false; // Nếu vẫn rỗng -> không có nhân viên -> return luôn
//        }
//
//        EmployeeDTO temp = empBus.getByIdLocal(account.getEmployeeId());
//        if (temp != null && temp.isStatus() && accBus.getByIdLocal(account.getEmployeeId()) == null) {
//            return accBus.insert(account);
//        }
//        return false;
//    }
//
//}
