
package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class EmployeeModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtEmployeeId;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private TextField txtSalary;
    @FXML
    private Button saveBtn,closeBtn,addRoleSubBtn;
    @FXML
    private ComboBox<String> cbSelectRole;
    @FXML
    private ComboBox<String> cbSelectStatus;
    @Getter
    private boolean isSaved;
    private int typeModal;
    private EmployeeDTO employee;
    private final HashMap<String, Integer> roleMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
        addRoleSubBtn.setOnAction(e -> handleAddRoleSub());
    }

    private void loadComboBox() {
        cbSelectStatus.getItems().addAll("Hoạt động", "Ngưng hoạt động");

        RoleBUS roleBUS = RoleBUS.getInstance();
        roleMap.clear();
        String selectedRole = cbSelectRole.getSelectionModel().getSelectedItem();

        // X+�a tߦ�t cߦ� c+�c m�+�c hi�+�n c+�
        cbSelectRole.getItems().clear();
        for (RoleDTO role : roleBUS.getAllLocal()) {
            cbSelectRole.getItems().add(role.getName());
            roleMap.put(role.getName(), role.getId());
        }

        // Kh+�i ph�+�c l�+�a ch�+�n -�+� l��u
        if (selectedRole != null && roleMap.containsKey(selectedRole)) {
            cbSelectRole.getSelectionModel().select(selectedRole);
        } else {
            cbSelectRole.getSelectionModel().selectFirst();
        }

        cbSelectStatus.getSelectionModel().selectFirst();
    }

    private int getSelectedRole() {
        return roleMap.getOrDefault(cbSelectRole.getValue(), -1);
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm nhân viên");
            txtEmployeeId.setText(String.valueOf(EmployeeBUS.getInstance().getAllLocal().size() + 1));
        } else {
            if (employee == null) handleClose();
            modalName.setText("Sửa nhân viên");
        }
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
        txtEmployeeId.setText(String.valueOf(employee.getId()));
        txtFirstName.setText(employee.getFirstName());
        txtLastName.setText(employee.getLastName());
        LocalDateTime dateTime = employee.getDateOfBirth();

        if (dateTime != null) {
            LocalDate localDate = dateTime.toLocalDate();
            dateOfBirth.setValue(localDate);
        } else {
            dateOfBirth.setValue(null);
        }
        RoleDTO selectedRole = RoleBUS.getInstance().getByIdLocal(employee.getRoleId());
        if (selectedRole != null) {
            cbSelectRole.getSelectionModel().select(selectedRole.getName());
        } else {
            cbSelectRole.getSelectionModel().clearSelection();
        }
        txtSalary.setText(employee.getSalary().toString());
        cbSelectStatus.getSelectionModel().select(employee.isStatus() ? "Hoạt động" : "Ngừng hoạt động");

        int employeeLoginId = SessionManagerService.getInstance().employeeRoleId();
        int employeeRoleId = SessionManagerService.getInstance().employeeRoleId();
        if (employee.getId() == employeeLoginId) {
            // Cߦ�p nhߦ�t ch+�nh m+�nh => kh+�a status v+� role
            makeReadOnly(cbSelectRole);
            makeReadOnly(cbSelectStatus);
            makeReadOnly(addRoleSubBtn);
        }
        if (employeeRoleId != 1) {
            // Nߦ+u kh+�ng phߦ�i admin th+� kh+�ng cho ch�+�nh l����ng
            makeReadOnly(txtSalary);
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String salary = txtSalary.getText().trim();

        ValidationUtils validator = ValidationUtils.getInstance();

        if (firstName.isEmpty()) {
            NotificationUtils.showErrorAlert("Họ đệm nhân viên không được để trống.", "Thông báo");
            clearAndFocus(txtFirstName);
            isValid = false;
        } else if (!validator.validateVietnameseText100(firstName)) {
            NotificationUtils.showErrorAlert("Họ đệm nhân viên không hợp lệ (Tối đa 100 ký tự, chỉ chữ và số, \"_\", \"-\", \"/\").", "Thông báo");
            clearAndFocus(txtFirstName);
            isValid = false;
        }

        if (isValid && lastName.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên nhân viên không được để trống ", "Thông báo");
            clearAndFocus(txtLastName);
            isValid = false;
        } else if (isValid && !validator.validateVietnameseText100(lastName)) {
            NotificationUtils.showErrorAlert("Tên nhân viên không hợp lệ (Tối đa 100 ký tự, chỉ chữ và số, \"_\", \"-\", \"/\").", "Thông báo");
            clearAndFocus(txtLastName);
            isValid = false;
        }

        if (isValid && cbSelectRole.getValue() == null) {
            NotificationUtils.showErrorAlert("Chức vụ không được để trống.", "Thông báo");
            isValid = false;
        }

        if (isValid && salary.isEmpty()) {
            NotificationUtils.showErrorAlert("Lương cơ bản không được để trống.", "Thông báo");
            clearAndFocus(txtSalary);
            isValid = false;
        } else if (isValid) {
            try {
                BigDecimal salaryS = new BigDecimal(salary);
                if (!validator.validateSalary(salaryS, 10, 2, false)) {
                    NotificationUtils.showErrorAlert("Lương cơ bản không hợp lế (Tối đa 10 chữ số, 2 số thập phân, không âm hoặc bằng 0).", "Thông báo");
                    clearAndFocus(txtSalary);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Lương cơ bản phải là số.", "Thông báo");
                clearAndFocus(txtSalary);
                isValid = false;
            }
        }

        // Ki�+�m tra dateOfBirth
        if (isValid) {
            LocalDate date = dateOfBirth.getValue();
            if (date != null) {
                LocalDate today = LocalDate.now();
                if (date.isAfter(today)) {
                    NotificationUtils.showErrorAlert("Ngày sinh không được là ngày trong tương lai.", "Thông báo");
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertEmployee();

        } else {
            updateEmployee();
        }
    }

    private void insertEmployee() {
        EmployeeBUS emBus = EmployeeBUS.getInstance();
        if (isValidInput()) {
            EmployeeDTO temp = new EmployeeDTO(Integer.parseInt(txtEmployeeId.getText().trim()), txtFirstName.getText().trim(),
                    txtLastName.getText().trim(), new BigDecimal(txtSalary.getText().trim()),
                    dateOfBirth.getValue() != null ? dateOfBirth.getValue().atStartOfDay() : null,
                    getSelectedRole(), cbSelectStatus.getValue().equals("Hoạt động"));
            int insertResult = emBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm nhân viên. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Thêm nhân viên\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Thêm nhân viên thất bại. Vui lòng thử lại.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Thông báo");
            }
        }
    }

    private void updateEmployee() {
        EmployeeBUS emBus = EmployeeBUS.getInstance();
        if (isValidInput()) {
            EmployeeDTO temp = new EmployeeDTO(employee.getId(), txtFirstName.getText().trim(),
                    txtLastName.getText().trim(), new BigDecimal(txtSalary.getText().trim()),
                    dateOfBirth.getValue() != null ? dateOfBirth.getValue().atStartOfDay() : null,
                    getSelectedRole(), cbSelectStatus.getValue().equals("Hoạt động"));
            int updateResult = emBus.update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (updateResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi cập nhật nhân viên. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Cập nhật nhân viên\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Dữ liệu không hợp lệ.", "Thông báo");
                case 5 -> {
                    NotificationUtils.showErrorAlert("Không thể cập nhật nhân viên gốc.", "Thông báo");
                }
                case 6 -> NotificationUtils.showErrorAlert("Bạn không thể cập nhật nhân viên ngang quyền.", "Thông báo");
                case 7 -> NotificationUtils.showErrorAlert("Cập nhật nhân viên thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Thông báo");
            }
        }
    }

    private void makeReadOnly(Node node) {
        node.setDisable(false); // Gi�+� UI r+�
        node.setMouseTransparent(true); // Kh+�ng t����ng t+�c
        node.setFocusTraversable(false); // Kh+�ng focus
        node.setStyle("-fx-background-color: #999999; -fx-opacity: 0.75;");

        if (node instanceof TextInputControl textInput) {
            textInput.setEditable(false);
        }

        if (node instanceof ComboBox<?> comboBox) {
            comboBox.setEditable(false);

            Platform.runLater(() -> {
                Node arrow = comboBox.lookup(".arrow-button");
                if (arrow != null) {
                    arrow.setStyle("-fx-background-color: #999999; -fx-opacity: 0.75;");
                }

                Node arrowIcon = comboBox.lookup(".arrow");
                if (arrowIcon != null) {
                    arrowIcon.setStyle("-fx-shape: ''; -fx-background-color: transparent;");
                }
            });
        }
    }

    private void handleAddRoleSub() {
        RoleModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/RoleModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm chức vụ"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm chức vụ thành công.", "Thông báo");
            loadComboBox();
        }
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void clearAndFocus(TextField textField) {
        textField.requestFocus();
    }
}