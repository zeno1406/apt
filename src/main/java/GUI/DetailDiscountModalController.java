package GUI;

import BUS.DetailDiscountBUS;
import BUS.EmployeeBUS;
import DTO.DetailDiscountDTO;
import DTO.EmployeeDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class DetailDiscountModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtTypeDiscount;
    @FXML
    private TextField txtTotalPriceInvoice;
    @FXML
    private TextField txtDiscountAmount;
    @FXML
    private Button saveBtn,closeBtn;
    @Getter
    private boolean isSaved;
    private int typeModal;
    private boolean typeDiscount;
    @Getter
    private DetailDiscountDTO detailDiscount;
    private ArrayList<DetailDiscountDTO> arrDetailDiscount = new ArrayList<>();

    @FXML
    public void initialize() {
        setupListeners();
        arrDetailDiscount.clear();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertDetailDiscount();

        } else {
            updateDetailDiscount();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String totalPriceInvoice = txtTotalPriceInvoice.getText().trim();
        String discountAmount = txtDiscountAmount.getText().trim();


        ValidationUtils validator = ValidationUtils.getInstance();

        if (totalPriceInvoice.isEmpty()) {
            NotificationUtils.showErrorAlert("Tổng tiền hóa đơn tối thiểu không được để trống.", "Thông báo");
            clearAndFocus(txtTotalPriceInvoice);
            isValid = false;
        } else if (isValid) {
            try {
                BigDecimal totalPrice = new BigDecimal(totalPriceInvoice);
                if (!validator.validateSalary(totalPrice, 12, 2, false)) {
                    NotificationUtils.showErrorAlert("Tổng tiền hóa đơn tối thiểu không hợp lệ (Tối đa 12 chữ số, 2 số thập phân, không âm hoặc bằng 0).", "Thông báo");
                    clearAndFocus(txtTotalPriceInvoice);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Tổng tiền hóa đơn tối thiểu phải là số.", "Thông báo");
                clearAndFocus(txtTotalPriceInvoice);
                isValid = false;
            }
        }

        if (isValid && discountAmount.isEmpty()) {
            NotificationUtils.showErrorAlert("Số tiền giảm giá không được để trống.", "Thông báo");
            clearAndFocus(txtDiscountAmount);
            isValid = false;
        } else if (isValid) {
            try {
                BigDecimal discount = new BigDecimal(discountAmount);
                if (typeDiscount) {
                    // Giảm theo phần trăm: từ >0 đến <100
                    if (discount.compareTo(BigDecimal.ZERO) <= 0 || discount.compareTo(BigDecimal.valueOf(100)) >= 0) {
                        NotificationUtils.showErrorAlert("Phần trăm giảm giá phải lớn hơn 0 và nhỏ hơn 100.", "Thông báo");
                        clearAndFocus(txtDiscountAmount);
                        isValid = false;
                    }
                } else {
                    // Giảm cứng: số tiền, không âm, > 0
                    if (!validator.validateSalary(discount, 10, 2, false)) {
                        NotificationUtils.showErrorAlert("Số tiền giảm giá không hợp lệ (Tối đa 10 chữ số, 2 số thập phân, không âm hoặc bằng 0).", "Thông báo");
                        clearAndFocus(txtDiscountAmount);
                        isValid = false;
                    }
                }
            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Số tiền giảm giá phải là số.", "Thông báo");
                clearAndFocus(txtDiscountAmount);
                isValid = false;
            }
        }

        return isValid;
    }

    private void insertDetailDiscount() {
        if (isValidInput()) {
            DetailDiscountDTO tempDetailDiscount = new DetailDiscountDTO("", new BigDecimal(txtTotalPriceInvoice.getText().trim()), new BigDecimal(txtDiscountAmount.getText().trim()));
            isSaved = true;
            detailDiscount = tempDetailDiscount;
            handleClose();
        }
    }

    private void updateDetailDiscount() {
        if (isValidInput()) {
            BigDecimal totalPriceInvoice = new BigDecimal(txtTotalPriceInvoice.getText().trim());
            BigDecimal discountAmount = new BigDecimal(txtDiscountAmount.getText().trim());
            isSaved = true;
            detailDiscount.setTotalPriceInvoice(totalPriceInvoice);
            detailDiscount.setDiscountAmount(discountAmount);
            handleClose();
        }
    }

    public void setTypeModal(int type, boolean typeDiscount) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        this.typeDiscount = typeDiscount;
        txtTypeDiscount.setText(typeDiscount ? "Phần trăm" : "Giảm cứng");
        if (typeModal == 0) {
            modalName.setText("Thêm chi tiết khuyến mãi");
        } else {
            if (detailDiscount == null) handleClose();
            modalName.setText("Sửa chi tiết khuyến mãi");
        }
    }

    public void setDetailDiscount(DetailDiscountDTO detailDiscount) {
        this.detailDiscount = detailDiscount;
        txtTotalPriceInvoice.setText(detailDiscount.getTotalPriceInvoice().toString());
        txtDiscountAmount.setText(detailDiscount.getDiscountAmount().toString());
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
