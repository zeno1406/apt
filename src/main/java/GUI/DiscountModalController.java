package GUI;

import DTO.DetailDiscountDTO;
import DTO.DiscountDTO;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;

public class DiscountModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtDiscountCode;
    @FXML
    private TextField txtDiscountName;
    @FXML
    private ComboBox<String> cbTypeDiscount;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private Button saveBtn,closeBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TableView<DetailDiscountDTO> tblDetailDiscount;
    @FXML
    private TableColumn<DetailDiscountDTO, String> tlb_col_totalPriceInvoice;
    @FXML
    private TableColumn<DetailDiscountDTO, String> tlb_col_discountAmount;
    private DiscountDTO discount;
    private ArrayList<DetailDiscountDTO> arrDetailDiscount;

    @Getter
    private boolean isSaved;
    private int typeModal;

    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
        addBtn.setOnAction(event -> handleAddBtn());
        deleteBtn.setOnAction(e -> handleDeleteBtn());
        editBtn.setOnAction(e -> handleEditBtn());
    }

    private void loadComboBox() {
        cbTypeDiscount.getItems().addAll("Phần trăm", "Giảm cứng");
        cbTypeDiscount.getSelectionModel().selectFirst();
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String discountCode = txtDiscountCode.getText().trim();
        String discountName = txtDiscountName.getText().trim();
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        ValidationUtils validator = ValidationUtils.getInstance();

        if (discountCode.isEmpty()) {
            NotificationUtils.showErrorAlert("Mã khuyến mãi không được để trống.", "Thông báo");
            clearAndFocus(txtDiscountCode);
            isValid = false;
        } else if (!validator.validateDiscountCode(discountCode, 4, 50)) {
            NotificationUtils.showErrorAlert("Mã khuyến mãi không hợp lệ (Tối thiểu 4 và tối đa 50 ký tự, chỉ chữ và số).", "Thông báo");
            clearAndFocus(txtDiscountCode);
            isValid = false;
        }

        if (isValid && discountName.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên khuyến mãi không được để trống ", "Thông báo");
            clearAndFocus(txtDiscountName);
            isValid = false;
        } else if (isValid && !validator.validateVietnameseText100(discountName)) {
            NotificationUtils.showErrorAlert("Tên khuyến mãi không hợp lệ (Tối đa 100 ký tự, chỉ chữ và số, \"_\", \"-\", \"/\").", "Thông báo");
            clearAndFocus(txtDiscountName);
            isValid = false;
        }

        if (isValid && (startDate == null || endDate == null)) {
            NotificationUtils.showErrorAlert("Ngày bắt đầu và ngày kết thúc không được bỏ trống.", "Thông báo");
            isValid = false;
        } else if (isValid){
            LocalDate today = LocalDate.now();

            if (startDate.isAfter(endDate)) {
                NotificationUtils.showErrorAlert("Ngày bắt đầu không được lớn hơn ngày kết thúc.", "Thông báo");
                isValid = false;
            } else if (startDate.isBefore(today)) {
                NotificationUtils.showErrorAlert("Ngày bắt đầu phải là hôm nay hoặc sau hôm nay.", "Thông báo");
                isValid = false;
            } else if (endDate.isBefore(today)) {
                NotificationUtils.showErrorAlert("Ngày kết thúc phải là hôm nay hoặc sau hôm nay.", "Thông báo");
                isValid = false;
            }
        }

        if (isValid && arrDetailDiscount.size() == 0) {
            NotificationUtils.showErrorAlert("Vui lòng thêm ít nhất 1 chi tiết khuyến mãi.", "Thông báo");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidDetailProduct() {
        return true;
    }

    private void handleSave() {
        if (typeModal == 0) {
//            insertEmployee();

        } else {
//            updateEmployee();
        }
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm khuyến mãi");
        } else {
            if (discount == null) handleClose();
            modalName.setText("Sửa khuyến mãi");
        }
    }

    private void handleAddBtn() {
        DetailDiscountModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/DetailDiscountModal.fxml",
                null,
                "Thêm chi tiết khuyến mãi"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm chi tiết khuyến mãi thành công.", "Thông báo");
//            resetFilters();
        }
    }

    private void handleEditBtn() {

    }

    private void handleDeleteBtn() {

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
