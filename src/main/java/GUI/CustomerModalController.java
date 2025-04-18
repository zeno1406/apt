package GUI;

import BUS.CustomerBUS;
import DTO.CustomerDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import net.bytebuddy.asm.Advice.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class CustomerModalController {
    // FXML Controls
    @FXML private TextField txtCustomerId;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private DatePicker dateOfBirth;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;
    @FXML private Button saveBtn,closeBtn;
    @FXML private Label modalName;
    @FXML private ComboBox<String> cbSelectStatus; 

    // State variables
    @Getter private boolean isSaved = false;
    private int typeModal; // 0: Add, 1: Edit
    private CustomerDTO customer;

    //done
    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    //done
    private void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    //done
    private void loadComboBox() {
        cbSelectStatus.getItems().addAll("Hoạt động", "Ngưng hoạt động");
        cbSelectStatus.getSelectionModel().selectFirst();
    }

    //done
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

    //done
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

    //done
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


    //done
    private void handleSave() {
        if (typeModal == 0) {
            insertCustomer();

        } else {
            updateCustomer();
        }
    }

    //done
    private void insertCustomer() {
        CustomerBUS customerBus = CustomerBUS.getInstance();
        if (isValidInput()) {
            CustomerDTO temp = new CustomerDTO(Integer.parseInt(txtCustomerId.getText().trim()), txtFirstName.getText().trim(),
                    txtLastName.getText().trim(), txtPhone.getText().trim(), txtAddress.getText().trim(),
                    dateOfBirth.getValue() != null ? dateOfBirth.getValue().atStartOfDay() : null,
                    cbSelectStatus.getValue().equals("Hoạt động"));
            int insertResult = customerBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    NotificationUtils.showInfoAlert("Thêm khách hàng thành công", "Thông báo");
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm khách hàng. Vui lòng thử lại.", "Thông báo");
                case 3 -> NotificationUtils.showErrorAlert("Đã có khách hàng trong cơ sở dữ liệu", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền thêm khách hàng.", "Thông báo");
                case 5 -> NotificationUtils.showErrorAlert("Thêm khách hàng thất bại. Vui lòng thử lại.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Thông báo");
            }
        }
    }

    //done
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
                    NotificationUtils.showInfoAlert("Cập nhật thông tin khách hàng thành công.", "Thông báo.");
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi cập nhật thông tin khách hàng. Vui lòng thử lại", "Lỗi");
                case 3 ->
                        NotificationUtils.showErrorAlert("Thông tin khách hàng bị trùng lặp.", "Lỗi");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền cập nhật thông tin khách hàng.", "Thông báo.");
                case 5 -> {
                    NotificationUtils.showErrorAlert("Không thể cập nhật thông tin khách hàng. Vui lòng thử lại.", "Lỗi");
                }
                case 6 -> NotificationUtils.showErrorAlert("Đầu vào không hợp lệ. Vui lòng thử lại.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Lỗi");
            }
        }
    }

    //done
    private void handleClose() {
        if(closeBtn.getScene() != null && closeBtn.getScene().getWindow()!= null){
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    //done
    private void clearAndFocus(TextField textField) {
        textField.requestFocus();
    }
}