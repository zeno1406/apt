package GUI;

import BUS.CustomerBUS;
import BUS.DetailDiscountBUS;
import BUS.DiscountBUS;
import DTO.CustomerDTO;
import DTO.DetailDiscountDTO;
import DTO.DiscountDTO;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DiscountForSellingModalController {
    @FXML
    private TextField txtSearchDiscount;
    @FXML
    private Button btnSearchDiscount, btnSubmitDiscount, btnExitGetDiscount;
    @FXML
    private TableView<DiscountDTO> tbvDiscount;
    @FXML
    private TableColumn<DiscountDTO, Integer> tbcCode;
    @FXML
    private TableColumn<DiscountDTO, String> tbcDiscountName;
    @FXML
    private TableColumn<DiscountDTO, String> tbcDiscountType;
    @FXML
    private TableColumn<DiscountDTO, String> tbcStartDate;
    @FXML
    private TableColumn<DiscountDTO, String> tbcEndDate;

    @Getter
    private boolean isSaved;
    @Getter @Setter
    private BigDecimal price;
    @Getter
    private DiscountDTO selectedDiscount;
    @Getter
    private ArrayList<DetailDiscountDTO> detailDiscountList = new ArrayList<>();

    @FXML
    public void initialize() {
        if(DiscountBUS.getInstance().getAllLocal().isEmpty()) DiscountBUS.getInstance().loadLocal();
        if(DetailDiscountBUS.getInstance().getAllLocal().isEmpty()) DetailDiscountBUS.getInstance().loadLocal();
        setOnMouseClicked();
        loadTable();
    }

    // Setup Customer Table
    public void loadTable() {
        tbcCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        tbcDiscountName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbcDiscountType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType() == 0 ? "Phần trăm" : "Giảm cứng"));
        tbcStartDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(ValidationUtils.getInstance().formatDateTime(cellData.getValue().getStartDate())));
        tbcEndDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(ValidationUtils.getInstance().formatDateTime(cellData.getValue().getEndDate())));

        tbvDiscount.setItems(FXCollections.observableArrayList(DiscountBUS.getInstance().filterDiscountsActive()));
        tbvDiscount.getSelectionModel().clearSelection();
    }

    // Set click Event
    public void setOnMouseClicked() {
        btnExitGetDiscount.setOnAction(e -> handleClose());
        btnSubmitDiscount.setOnAction(e -> handleGetDiscount());
//        btnSearchCustomer.setOnMouseClicked(e -> handleSearch());
        txtSearchDiscount.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    // handle search
    private void handleSearch() {
        tbvDiscount.setItems(FXCollections.observableArrayList(DiscountBUS.getInstance().searchByCodeLocal(txtSearchDiscount.getText().trim())));
        tbvDiscount.getSelectionModel().clearSelection();
    }

    // close
    private void handleClose() {
        if (btnExitGetDiscount.getScene() != null && btnExitGetDiscount.getScene().getWindow() != null) {
            Stage stage = (Stage) btnExitGetDiscount.getScene().getWindow();
            stage.close();
        }
    }

    // handle select customer
    private void handleGetDiscount() {
        selectedDiscount = tbvDiscount.getSelectionModel().getSelectedItem();
        detailDiscountList.clear();
        detailDiscountList = getDetails(selectedDiscount.getCode());
        if (!isValid(detailDiscountList)) {
            NotificationUtils.showErrorAlert("Vui lòng mua thêm tối thiểu " + ValidationUtils.getInstance().formatCurrency(detailDiscountList.getFirst().getTotalPriceInvoice().subtract(price)) + " Đ để dùng voucher này!", "Thông báo");
            return;
        }
        isSaved = true;
        handleClose();
    }

    private ArrayList<DetailDiscountDTO> getDetails(String discountID) {
        return DetailDiscountBUS.getInstance().getAllDetailDiscountByDiscountIdLocal(discountID);
    }

    private boolean isValid(ArrayList<DetailDiscountDTO> details) {
        for (DetailDiscountDTO detail : details) {
            // Kiểm tra nếu price >= mốc giá trị của detail
            if (detail.getTotalPriceInvoice().compareTo(price) <= 0) {
                return true;  // Nếu có ít nhất một mốc thỏa mãn thì trả về true
            }
        }
        return false;  // Nếu không có mốc nào thỏa mãn thì trả về false
    }
}
