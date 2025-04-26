package GUI;

import BUS.ProductBUS;
import DTO.ProductDTO;
import DTO.RoleDTO;
import DTO.TempDetailImportDTO;
import SERVICE.RolePermissionService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;

public class ImportProductModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtProductName;
    @FXML
    private TextField txtPrice;
    @FXML
    private Button saveBtn,closeBtn;
    @FXML
    private HBox hbInputPrice, hbInputSellingPrice;
    @FXML
    private AnchorPane acRootInput;
    @Getter
    private boolean isSaved;
    private int typeModal;
    @Getter
    private TempDetailImportDTO tempDetailImport;
    @FXML
    public void initialize() {
        if(ProductBUS.getInstance().getAllLocal().isEmpty()) ProductBUS.getInstance().loadLocal();
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

//   add handle for selling here
    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm chi tiết phiếu nhập");
        } else if (typeModal == 1){
            if (tempDetailImport == null) handleClose();
            modalName.setText("Sửa chi tiết phiếu nhập");
        }
    }

    public void setTempDetailImport(TempDetailImportDTO tempDetailImport) {
        this.tempDetailImport = tempDetailImport;
        txtQuantity.setText(String.valueOf(tempDetailImport.getQuantity()));
        txtPrice.setText(String.valueOf(tempDetailImport.getPrice()));
        txtProductName.setText(tempDetailImport.getName());
    }

    public void setProduct(ProductDTO product) {
        txtProductName.setText(product.getName());
        this.tempDetailImport = new TempDetailImportDTO(
                0,
                product.getId(),
                product.getName(),
                1,
                BigDecimal.ZERO, // hoặc product.getImportPrice() nếu bạn muốn
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String quantity = txtQuantity.getText().trim();
        String price = txtPrice.getText().trim();

        ValidationUtils validator = ValidationUtils.getInstance();

        if (quantity.isEmpty()) {
            NotificationUtils.showErrorAlert("Số lượng không được để trống.", "Thông báo");
            clearAndFocus(txtQuantity);
            isValid = false;
        } else {
            try {
                int quantityValue = Integer.parseInt(quantity);
                if (quantityValue < 1) {
                    NotificationUtils.showErrorAlert("Số lượng phải lớn hơn hoặc bằng 1.", "Thông báo");
                    clearAndFocus(txtQuantity);
                    isValid = false;
                }
//                if (!isValidQuantity(ValidationUtils.getInstance().canParseToInt(txtQuantity.getText().trim()), ProductBUS.getInstance().getByIdLocal(tempDetailImport.getProductId()))) {
//                        clearAndFocus(txtQuantity);
//                        isValid = false;
//                }

            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Số lượng phải là số nguyên hợp lệ.", "Thông báo");
                clearAndFocus(txtQuantity);
                isValid = false;
            }
        }

        BigDecimal priceValue = BigDecimal.ZERO;
        if (isValid && price.isEmpty()) {
            NotificationUtils.showErrorAlert("Giá nhập không được để trống.", "Thông báo");
            clearAndFocus(txtPrice);
            isValid = false;
        } else if (isValid) {
            try {
                priceValue = new BigDecimal(price);
                if (!validator.validateSalary(priceValue, 10, 2, false)) {
                    NotificationUtils.showErrorAlert("Giá nhập không hợp lệ (tối đa 10 chữ số, 2 số thập phân, không âm hoặc bằng 0).", "Thông báo");
                    clearAndFocus(txtPrice);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Giá nhập phải là số.", "Thông báo");
                clearAndFocus(txtPrice);
                isValid = false;
            }
        }
        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertDetailImport();
        } else {
            updateDetailImport();
        }
    }

    private void insertDetailImport() {
        if (isValidInput()) {
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            BigDecimal sellingPrice = new BigDecimal(txtPrice.getText().trim());
            BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));

            TempDetailImportDTO temp = new TempDetailImportDTO(
                    tempDetailImport.getImportId(),
                    tempDetailImport.getProductId(),
                    tempDetailImport.getName(),
                    quantity,
                    price,
                    sellingPrice,
                    totalPrice
            );

            if (temp != null) {
                isSaved = true;
                tempDetailImport = new TempDetailImportDTO(temp);
                handleClose();
            } else {
               NotificationUtils.showErrorAlert("Có lỗi khi thêm chi tiết phiếu nhập. Vui lòng thử lại.", "Thông báo");
            }
        }
    }

    private void updateDetailImport() {
        if (isValidInput()) {
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            BigDecimal sellingPrice = new BigDecimal(txtPrice.getText().trim());
            BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));

            isSaved = true;
            tempDetailImport.setQuantity(quantity);
            tempDetailImport.setTotalPrice(totalPrice);
            tempDetailImport.setSellingPrice(sellingPrice);
            tempDetailImport.setPrice(price);
            handleClose();
        }
    }

    private boolean isValidQuantity(int nowQuantity, ProductDTO product) {
        if (nowQuantity <= 0 || nowQuantity > product.getStockQuantity()) {
            NotificationUtils.showErrorAlert("Invalid quantity!", "Alert");
            return false;
        }
        return true;
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
