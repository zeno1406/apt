package GUI;

import BUS.CategoryBUS;
import BUS.CustomerBUS;
import DTO.CategoryDTO;
import DTO.CustomerDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import com.mysql.cj.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

import javax.swing.*;

public class CategoryModalController {

    // FXML Controller
    public Label modalName;
    public TextField txtCategoryId;
    public TextField txtCategoryName;
    public Button closeBtn;
    public Button saveBtn;
    public ComboBox cbSelectStatus;

    // State variables
    @Getter
    private boolean isSaved;
    private int typeModal; // 0: Add, 1: Edit
    private CategoryDTO category;

    @FXML
    public void initialize() {
        loadCombobox();
        setupListener();
    }

    private void setupListener() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void loadCombobox() {
        cbSelectStatus.getItems().addAll("Hoạt động", "Ngưng hoạt động");
        cbSelectStatus.getSelectionModel().selectFirst();
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm thể loại");
        } else {
            if (category == null) handleClose();
            modalName.setText("Sửa thể loại");
        }
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
        if (category != null) {
            txtCategoryId.setText(String.valueOf(category.getId()));
            txtCategoryName.setText(String.valueOf(category.getName()));
            cbSelectStatus.getSelectionModel().select(category.isStatus() ? "Hoạt động" : "Ngưng hoạt động");
        }
    }

    private boolean isValidInput() {
        int MIN_LENGTH = 1;
        int MAX_LENGTH = 50;
        boolean isValid = true;
        String categoryName = txtCategoryName.getText().trim();

        ValidationUtils validator = ValidationUtils.getInstance();

        //Kiểm tra empty
        if (categoryName.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên thể loại không được để trống", "Thông báo");
            clearAndFocus(txtCategoryName);
            isValid = false;
        }
        //Kiểm tra vượt 50 ký tự
        else if (!validator.validateUsername(categoryName, MIN_LENGTH, MAX_LENGTH)) {
            NotificationUtils.showErrorAlert("Tên không hợp lệ (1-50 ký tự, a-z, A-Z, 0-9, không có khoảng trắng.)", "Thông báo");
            clearAndFocus(txtCategoryName);
            isValid = false;
        }

        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertCategory();
        } else {
            updateCategory();
        }
    }

    private void insertCategory() {
        CategoryBUS categoryBus = CategoryBUS.getInstance();
        if (isValidInput()) {
            CategoryDTO temp = new CategoryDTO(
                    Integer.parseInt(txtCategoryId.getText().trim()),
                    txtCategoryName.getText().trim(),
                    cbSelectStatus.getValue().equals("Hoạt động"));

            int insertResult = categoryBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
//                    NotificationUtils.showInfoAlert("Thêm thể loại thành công!", "Thông báo");
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.", "Thông báo");
                case 3 -> NotificationUtils.showErrorAlert("Thể loại bị trùng lặp. Không thể thêm.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền thêm thể loại.", "Thông báo");
                case 5 -> NotificationUtils.showErrorAlert("Lỗi kết nối CSDL. Thêm thể loại thất bại.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Thêm thể loại thất bại.", "Thông báo");
            }
        }
    }

    private void updateCategory() {
        CategoryBUS categoryBus = CategoryBUS.getInstance();
        if (isValidInput()) {
            CategoryDTO temp = new CategoryDTO(category.getId(),
                    txtCategoryName.getText().trim(),
                    cbSelectStatus.getValue().equals("Hoạt động"));

            int updateResult = categoryBus.update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (updateResult) {
                case 1 -> {
//                    NotificationUtils.showInfoAlert("Thêm thể loại thành công!", "Thông báo");
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.", "Thông báo");
                case 3 -> NotificationUtils.showErrorAlert("Thể loại bị trùng lặp. Không thể thêm.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Không có quyền thêm thể loại.", "Thông báo");
                case 5 -> NotificationUtils.showErrorAlert("Lỗi kết nối CSDL. Thêm thể loại thất bại.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Thêm thể loại thất bại.", "Thông báo");
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