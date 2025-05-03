package GUI;

import java.time.LocalDate;
import java.time.LocalDateTime;

import BUS.CustomerBUS;
import BUS.SupplierBUS;
import DTO.CustomerDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

public class CustomerModalController {
    // FXML Controls
    @FXML public Label modalName;
    @FXML public TextField txtCustomerId;
    @FXML public TextField txtFirstName;
    @FXML public TextField txtLastName;
    @FXML public DatePicker dateOfBirth;
    @FXML public TextField txtPhone;
    @FXML public TextField txtAddress;
    @FXML public Button closeBtn;
    @FXML public Button saveBtn;
    @FXML public ComboBox<String> cbSelectStatus; 

    // State variables
    @Getter
    private boolean isSaved;
    private int typeModal; // 0: Add, 1: Edit
    private CustomerDTO customer;

    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    private void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void loadComboBox() {
        cbSelectStatus.getItems().addAll("Hoạt động", "Ngưng hoạt động");
        cbSelectStatus.getSelectionModel().selectFirst();
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm khách hàng");
            txtCustomerId.setText(String.valueOf(CustomerBUS.getInstance().getAllLocal().size() + 1));
        } else {
            if (customer == null) handleClose();
            modalName.setText("Sửa khách hàng");
        }
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
        if (customer != null) {
            txtCustomerId.setText(String.valueOf(customer.getId()));
            txtFirstName.setText(customer.getFirstName());
            txtLastName.setText(customer.getLastName());
            LocalDateTime dateTime = customer.getDateOfBirth();

            if (dateTime != null) {
                LocalDate localDate = dateTime.toLocalDate();
                dateOfBirth.setValue(localDate); 
            } else {
                dateOfBirth.setValue(null);
            }
            
            txtPhone.setText(customer.getPhone());
            txtAddress.setText(customer.getAddress());
            cbSelectStatus.getSelectionModel().select(customer.isStatus() ? "Hoạt động" : "Ngưng hoạt động");
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        ValidationUtils validator = ValidationUtils.getInstance();

        if (firstName.isEmpty()) {
            NotificationUtils.showErrorAlert("Họ khách hàng không được để trống.", "Thông báo");
            clearAndFocus(txtFirstName);
            isValid = false;
        } else if (!validator.validateVietnameseText100(firstName)) {
            NotificationUtils.showErrorAlert("Họ không hợp lệ (tối đa 100 ký tự).", "Thông báo");
            clearAndFocus(txtFirstName);
            isValid = false;
        }
        
        if(isValid && lastName.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên khách hàng không được để trống.", "Thông báo");
            clearAndFocus(txtLastName);
            isValid = false; 
        } else if (isValid && !validator.validateVietnameseText100(lastName)) {
            NotificationUtils.showErrorAlert("Tên không hợp lệ (tối đa 100 ký tự).", "Thông báo");
            clearAndFocus(txtLastName);
            isValid = false;
        }

        if (isValid && phone.isEmpty()) {
            NotificationUtils.showErrorAlert("Số điện thoại không được để trống.", "Thông báo");
            clearAndFocus(txtPhone);
            isValid = false;
        } else if(isValid && !validator.validateVietnamesePhoneNumber(phone)) {
            NotificationUtils.showErrorAlert("Số điện thoại không hợp lệ(Số 0 đứng đầu và theo sau 9 ký tự)", "Thông báo");
            clearAndFocus(txtPhone);
            isValid = false;
        }

        if (isValid && address.isEmpty()) {
            NotificationUtils.showErrorAlert("Địa chỉ không được để trống.", "Thông báo");
            clearAndFocus(txtAddress);
            isValid = false;
        } else if (isValid && !validator.validateVietnameseText255(address)) {
            NotificationUtils.showErrorAlert("Địa chỉ không hợp lệ (tối đa 255 ký tự)", "Thông báo");
            isValid = false;
        }

        //Kiem tra ngay sinh
        if (isValid) {
            LocalDate date = dateOfBirth.getValue();
            if (date != null) {
                LocalDate today = LocalDate.now();
                if(date.isAfter(today)){
                    NotificationUtils.showErrorAlert("Ngày sinh không hợp lệ (ngày sinh không được lớn hơn ngày hiện tại)", "Thông báo");
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertCustomer();
        } else {
            updateCustomer();
        }
    }

    private void insertCustomer() {
        CustomerBUS customerBus = CustomerBUS.getInstance();
        if (isValidInput()) {
            // Create CustomerDTO with ID 0, which will be replaced by the generated ID
            CustomerDTO temp = new CustomerDTO(-1, txtFirstName.getText().trim(),
                    txtLastName.getText().trim(), txtPhone.getText().trim(), txtAddress.getText().trim(),
                    dateOfBirth.getValue() != null ? dateOfBirth.getValue().atStartOfDay() : null,
                    cbSelectStatus.getValue().equals("Hoạt động"));
            int insertResult = customerBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm khách hàng. Vui lòng thử lại.", "Thông báo");
                case 3 -> NotificationUtils.showErrorAlert("Đã có khách hàng trong cơ sở dữ liệu", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền thêm khách hàng.", "Thông báo");
                case 5 -> NotificationUtils.showErrorAlert("Thêm khách hàng thất bại. Vui lòng thử lại.", "Thông báo");
                case 6 -> NotificationUtils.showErrorAlert("Dữ liệu nhập không hợp lệ.","Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Thông báo");
            }
        }
    }

    private void updateCustomer() {
        CustomerBUS customerBus = CustomerBUS.getInstance();
        if (isValidInput()) {
            CustomerDTO temp = new CustomerDTO(customer.getId(), txtFirstName.getText().trim(),
                    txtLastName.getText().trim(), txtPhone.getText().trim(), txtAddress.getText().trim(),
                    dateOfBirth.getValue() != null ? dateOfBirth.getValue().atStartOfDay() : null,
                    cbSelectStatus.getValue().equals("Hoạt động"));
            int updateResult = customerBus.update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (updateResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi cập nhật thông tin khách hàng. Vui lòng thử lại", "Lỗi");
                case 3 -> NotificationUtils.showErrorAlert("Thông tin khách hàng bị trùng lặp.", "Lỗi");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền cập nhật thông tin khách hàng.", "Thông báo.");
                case 5 -> NotificationUtils.showErrorAlert("Không thể cập nhật thông tin khách hàng. Vui lòng thử lại.", "Lỗi");
                case 6 -> NotificationUtils.showErrorAlert("Đầu vào không hợp lệ. Vui lòng thử lại.", "Thông báo");
                case 7 -> NotificationUtils.showErrorAlert("Không thể xoá khách hàng mặc định.","Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Lỗi");
            }
        }
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void clearAndFocus(TextField field) {
        field.requestFocus();
    }
}