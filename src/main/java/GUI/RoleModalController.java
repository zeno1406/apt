package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.RoleDTO;
import SERVICE.RolePermissionService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;

public class RoleModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtRoleName;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtSalaryCoefficient;
    @FXML
    private Button saveBtn,closeBtn;
    @Getter
    private boolean isSaved;
    private int typeModal;
    private RoleDTO role;

    @FXML
    public void initialize() {
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm chức vụ");
        } else {
            if (role == null) handleClose();
            modalName.setText("Sửa chức vụ");
        }
    }

    public void setRole(RoleDTO role) {
        this.role = role;
        txtRoleName.setText(role.getName());
        txtDescription.setText(role.getDescription() == null ? "" : role.getDescription());
        txtSalaryCoefficient.setText(role.getSalaryCoefficient().toString());
        if (SessionManagerService.getInstance().employeeRoleId() != 1) {
            makeReadOnly(txtSalaryCoefficient);
        }
    }

    private void makeReadOnly(Node node) {
        node.setDisable(false);
        node.setMouseTransparent(true);
        node.setFocusTraversable(false);
        if (node instanceof TextInputControl textInput) {
            textInput.setEditable(false);
        }
        node.setStyle("-fx-background-color: #999999; -fx-opacity: 0.75;");
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String roleName = txtRoleName.getText().trim();
        String salaryCoefficientText = txtSalaryCoefficient.getText().trim();
        String description = txtDescription.getText().trim();

        // Chỉ cần một lần gọi
        ValidationUtils validator = ValidationUtils.getInstance();

        // Kiểm tra tên chức vụ
        if (roleName.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên chức vụ không được để trống.", "Thông báo");
            clearAndFocus(txtRoleName);
            isValid = false;
        } else if (!validator.validateVietnameseText50(roleName)) {
            NotificationUtils.showErrorAlert("Tên chức vụ không hợp lệ (tối đa 50 ký tự, không chứa ký tự đặc biệt).", "Thông báo");
            clearAndFocus(txtRoleName);
            isValid = false;
        }

        // Kiểm tra hệ số lương
        if (isValid && salaryCoefficientText.isEmpty()) { // Chỉ kiểm tra hệ số lương nếu tên chức vụ hợp lệ
            NotificationUtils.showErrorAlert("Hệ số lương không được để trống.", "Thông báo");
            clearAndFocus(txtSalaryCoefficient);
            isValid = false;
        } else if (isValid) {
            try {
                BigDecimal salaryCoefficient = new BigDecimal(salaryCoefficientText);
                if (!validator.validateBigDecimal(salaryCoefficient, 5, 2, false)) {
                    NotificationUtils.showErrorAlert("Hệ số lương không hợp lệ (tối đa 5 chữ số, 2 số thập phân, không âm hoặc bằng 0).", "Thông báo");
                    clearAndFocus(txtSalaryCoefficient);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Hệ số lương phải là số.", "Thông báo");
                clearAndFocus(txtSalaryCoefficient);
                isValid = false;
            }
        }

        // Kiểm tra mô tả
        if (isValid && !description.isEmpty() && !validator.validateVietnameseText255(description)) {
            NotificationUtils.showErrorAlert("Mô tả không hợp lệ (tối đa 255 ký tự, không chứa ký tự đặc biệt).", "Thông báo");
            clearAndFocus(txtDescription);
            isValid = false;
        }

        return isValid;
    }


    private void handleSave() {
        if (typeModal == 0) {
            insertRole();
        } else {
            updateRole();
        }
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void insertRole() {
        RolePermissionService rolePermissionService = RolePermissionService.getInstance();
        if (isValidInput()) {

            RoleDTO temp = new RoleDTO(0, txtRoleName.getText().trim(), txtDescription.getText().trim(), new BigDecimal(txtSalaryCoefficient.getText().trim()));
            int insertResult = rolePermissionService.createRoleWithPermissions(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm chức vụ. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Thêm chức vụ\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> {
                    NotificationUtils.showErrorAlert("Tên chức vụ đã tồn tại trong hệ thống.", "Thông báo");
                    clearAndFocus(txtRoleName);
                }
                case 5 -> NotificationUtils.showErrorAlert("Thêm chức vụ thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }
    }

    private void updateRole() {
        RoleBUS rBus = RoleBUS.getInstance();
        if (isValidInput()) {
            RoleDTO temp = new RoleDTO(role.getId(), txtRoleName.getText().trim(), txtDescription.getText().trim() , new BigDecimal(txtSalaryCoefficient.getText().trim()));
            int updateResult = rBus.update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (updateResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi cập nhật chức vụ. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Cập nhật chức vụ\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Dữ liệu đầu vào không hợp lệ", "Thông báo");
                case 5 -> {
                    NotificationUtils.showErrorAlert("Tên chức vụ đã tồn tại trong hệ thống.", "Thông báo");
                    clearAndFocus(txtRoleName);
                }
                case 6 -> NotificationUtils.showErrorAlert("Bạn không thể cập nhật chức vụ ngang quyền.", "Thông báo");
                case 7 -> NotificationUtils.showErrorAlert("Cập nhật chức vụ thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }

    }

    private void clearAndFocus(TextField textField) {
        textField.requestFocus();
    }
}
