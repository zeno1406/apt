package GUI;

import BUS.CategoryBUS;
import BUS.SupplierBUS;
import DTO.SupplierDTO;
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

public class SupplierModalController {
    // FXML Controls
    @FXML public Label modalName;
    @FXML public TextField txtSupplierId;
    @FXML public TextField txtCompanyName;
    @FXML public TextField txtPhone;
    @FXML public TextField txtAddress;
    @FXML public Button closeBtn;
    @FXML public Button saveBtn;
    @FXML public ComboBox<String> cbSelectStatus;

    // State variables
    @Getter
    private boolean isSaved;
    private int typeModal; // 0: Add, 1: Edit
    private SupplierDTO supplier;

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
            modalName.setText("Thêm nhà cung cấp");
            txtSupplierId.setText(String.valueOf(SupplierBUS.getInstance().getAllLocal().size() + 1));
        } else {
            if (supplier == null) handleClose();
            modalName.setText("Sửa nhà cung cấp");
        }
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
        if (supplier != null) {
            txtSupplierId.setText(String.valueOf(supplier.getId()));
            txtCompanyName.setText(supplier.getName());
            txtPhone.setText(supplier.getPhone());
            txtAddress.setText(supplier.getAddress());
            cbSelectStatus.getSelectionModel().select(supplier.isStatus() ? "Hoạt động" : "Ngưng hoạt động");
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String name = txtCompanyName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        ValidationUtils validator = ValidationUtils.getInstance();

        if (name.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên nhà cung cấp không được để trống.", "Thông báo");
            clearAndFocus(txtCompanyName);
            isValid = false;
        } else if (!validator.validateVietnameseText100(name)) {
            NotificationUtils.showErrorAlert("Tên nhà cung cấp không hợp lệ (tối đa 100 ký tự).", "Thông báo");
            clearAndFocus(txtCompanyName);
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
        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertSupplier();
        } else {
            updateSupplier();
        }
    }

    private void insertSupplier() {
        SupplierBUS supplierBus = SupplierBUS.getInstance();
        if (isValidInput()) {
            // Create CustomerDTO with ID 0, which will be replaced by the generated ID
            SupplierDTO temp = new SupplierDTO(-1, txtCompanyName.getText().trim(),
                    txtPhone.getText().trim(), txtAddress.getText().trim(),
                    cbSelectStatus.getValue().equals("Hoạt động"));
            int insertResult = supplierBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm nhà cung cấp. Vui lòng thử lại.", "Thông báo");
                case 3 -> NotificationUtils.showErrorAlert("Đã có nhà cung cấp trong cơ sở dữ liệu", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền thêm nhà cung cấp.", "Thông báo");
                case 5 -> NotificationUtils.showErrorAlert("Thêm nhà cung cấp vào CSDL thất bại. Vui lòng thử lại.", "Thông báo");
                case 6 -> NotificationUtils.showErrorAlert("Dữ liệu nhập không hợp lệ.","Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại.", "Thông báo");
            }
        }
    }

    private void updateSupplier() {
        SupplierBUS supplierBus = SupplierBUS.getInstance();
        if (isValidInput()) {
            SupplierDTO temp = new SupplierDTO(supplier.getId(), txtCompanyName.getText().trim(),
                    txtPhone.getText().trim(), txtAddress.getText().trim(),
                    cbSelectStatus.getValue().equals("Hoạt động"));
            int updateResult = supplierBus.update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (updateResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi cập nhật thông tin nhà cung cấp. Vui lòng thử lại", "Lỗi");
                case 3 -> NotificationUtils.showErrorAlert("Thông tin nhà cung cấp bị trùng lặp.", "Lỗi");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền cập nhật thông tin nhà cung cấp.", "Thông báo.");
                case 5 -> NotificationUtils.showErrorAlert("Không thể cập nhật thông tin nhà cung cấp. Vui lòng thử lại.", "Lỗi");
                case 6 -> NotificationUtils.showErrorAlert("Đầu vào không hợp lệ. Vui lòng thử lại.", "Thông báo");
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