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
    private  BigDecimal discountPrice = BigDecimal.ZERO;
    @Getter
    private BigDecimal totalPriceInvoice = BigDecimal.ZERO;
    @Getter
    private BigDecimal discountPercent = BigDecimal.ZERO;

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

        tbvDiscount.setItems(FXCollections.observableArrayList(DiscountBUS.getInstance().filterDiscountsAdvance("", -1, null, null)));
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
        ArrayList<DetailDiscountDTO> listDetails = getDetails(selectedDiscount.getCode());
//        this.discountPrice = this.price.subtract(listDetails.getFirst().getTotalPriceInvoice());
        this.discountPrice = listDetails.getFirst().getTotalPriceInvoice().subtract(this.price);
        if (!isValid(listDetails)) {
            NotificationUtils.showErrorAlert("Vui lòng mua thêm tối thiểu " + ValidationUtils.getInstance().formatCurrency(discountPrice) + " Đ để dùng voucher này!", "Thông báo");
            return;
        }
        setDiscountPrice(listDetails);
        isSaved = true;
        handleClose();
    }

    private ArrayList<DetailDiscountDTO> getDetails(String discountID) {
        return DetailDiscountBUS.getInstance().getAllDetailDiscountByDiscountIdLocal(discountID);
    }

    private boolean isValid(ArrayList<DetailDiscountDTO> details) {
        boolean temp = false;
        for(DetailDiscountDTO detail : details) {
            temp = detail.getTotalPriceInvoice().compareTo(this.price) <= 0;
            if(temp) return temp;
        }
        return false;
    }

    private void setDiscountPrice(ArrayList<DetailDiscountDTO> details) {
        int length = details.size();
        for(int i = length - 1; i >= 0; i--) {
            if (this.price.compareTo(details.get(i).getTotalPriceInvoice()) >= 0)
                if(selectedDiscount.getType() == 0) {
                    this.discountPrice = this.price.multiply(
                            (BigDecimal.valueOf(100).subtract(details.get(i).getDiscountAmount()))
                                    .divide(BigDecimal.valueOf(100), BigDecimal.ROUND_CEILING)
                    );
                    this.discountPercent = details.get(i).getDiscountAmount();
                    this.totalPriceInvoice = details.get(i).getTotalPriceInvoice();
                    return;
                }
                else
                {
                    this.discountPrice = this.price.subtract(details.get(i).getDiscountAmount());
                    this.totalPriceInvoice = details.get(i).getDiscountAmount();
                    return;
                }
        }
    }
}
