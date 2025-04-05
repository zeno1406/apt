package GUI;

import BUS.AccountBUS;
import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.AccountDTO;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;


public class AccountModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtRePassword;
    @FXML
    private Button saveBtn,closeBtn,addEmployeeSubBtn;
    @FXML
    private ComboBox<String> cbSelectEmployee;
    @Getter
    private boolean isSaved;
    private int typeModal;
    private AccountDTO account;
    private final HashMap<String, Integer> employeeMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void loadComboBox() {

        AccountBUS accBUS = AccountBUS.getInstance();
        EmployeeBUS emBUS = EmployeeBUS.getInstance();
        employeeMap.clear();

        HashSet<Integer> employeesWithAccount = accBUS.employeesWithAccount();

        for (EmployeeDTO em : emBUS.getAllLocal()) {
            if (!employeesWithAccount.contains(em.getId())) {
                employeeMap.put(em.getFullName(), em.getId());
                cbSelectEmployee.getItems().add(em.getFirstName() + " " + em.getLastName());
            }
        }
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm tài khoản");
        } else {
            if (account == null) handleClose();
            modalName.setText("Sửa tài khoản");
        }
    }

    public void setAccount(AccountDTO account) {
        this.account = account;
//        txtRoleName.setText(role.getName());
//        txtDescription.setText(role.getDescription() == null ? "" : role.getDescription());
//        txtSalaryCoefficient.setText(role.getSalaryCoefficient().toString());
//        if (SessionManagerService.getInstance().employeeRoleId() != 1) {
//            txtSalaryCoefficient.setDisable(true);
//            txtSalaryCoefficient.setStyle("-fx-background-color: #999999;");
//        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String repassword = txtRePassword.getText().trim();

        // Chỉ cần một lần gọi
        ValidationUtils validator = ValidationUtils.getInstance();

        if (cbSelectEmployee.getValue() == null) {
            NotificationUtils.showErrorAlert("Nhân viên không được để trống.", "Thông báo");
            isValid = false;
        }

        if (isValid && username.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên tài khoản không được để trống.", "Thông báo");
            clearAndFocus(txtUsername);
            isValid = false;
        } else if (isValid && !validator.validateUsername(username, 4, 50)) {
            NotificationUtils.showErrorAlert("Tên tài khoản không hợp lệ (tối thiểu 4 ký tự, tối đa 50 ký tự, chỉ chữ và số).", "Thông báo");
            clearAndFocus(txtUsername);
            isValid = false;
        }

        if (isValid && password.isEmpty()) {
            NotificationUtils.showErrorAlert("Mật khẩu không được để trống.", "Thông báo");
            clearAndFocus(txtPassword);
            isValid = false;
        } else if (isValid && !validator.validatePassword(password, 6, 255)) {
            NotificationUtils.showErrorAlert("Mật khẩu không hợp lệ (tối thiểu 6 ký tự, tối đa 255 ký tự).", "Thông báo");
            clearAndFocus(txtPassword);
            isValid = false;
        }

        if (isValid && repassword.isEmpty()) {
            NotificationUtils.showErrorAlert("Xác nhận mật khẩu không được để trống.", "Thông báo");
            clearAndFocus(txtRePassword);
            isValid = false;
        } else if (isValid && !repassword.equals(password)) {
            NotificationUtils.showErrorAlert("Xác nhận mật khẩu không trùng khớp.", "Thông báo");
            clearAndFocus(txtRePassword);
            isValid = false;
        }
        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertAccount();
        } else {
            updateAccount();
        }
    }

    private void insertAccount() {
        AccountBUS accBus = AccountBUS.getInstance();
        if (isValidInput()) {

            AccountDTO temp = new AccountDTO(employeeMap.get(cbSelectEmployee.getValue()), txtUsername.getText().trim(), txtPassword.getText().trim());
            int insertResult = accBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm tài khoản. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Thêm tài khoản\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Nhân viên đã có tài khoản hoặc nhân viên này có vai trò quản trị", "Thông báo");
                case 5 -> {
                    NotificationUtils.showErrorAlert("Tên tài khoản đã tồn tại trong hệ thống.", "Thông báo");
                    clearAndFocus(txtUsername);
                }
                case 6 -> NotificationUtils.showErrorAlert("Thêm tài khoản thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }
    }

    private void updateAccount() {

    }


    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void clearAndFocus(TextField textField) {
        textField.clear();
        textField.requestFocus();
    }
}
