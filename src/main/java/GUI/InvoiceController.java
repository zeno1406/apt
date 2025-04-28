package GUI;

import BUS.*;
import DTO.DetailDiscountDTO;
import DTO.DetailInvoiceDTO;
import DTO.DiscountDTO;
import DTO.InvoiceDTO;
import INTERFACE.IController;
import SERVICE.PrintService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;

public class InvoiceController implements IController  {
    @FXML
    private TableView<InvoiceDTO> tblInvoice;
    @FXML
    private TableColumn<InvoiceDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<InvoiceDTO, String> tlb_col_createDate;
    @FXML
    private TableColumn<InvoiceDTO, Integer> tlb_col_employeeId;
    @FXML
    private TableColumn<InvoiceDTO, Integer> tlb_col_customerId;
    @FXML
    private TableColumn<InvoiceDTO, String> tlb_col_discountCode;
    @FXML
    private TableColumn<InvoiceDTO, String> tlb_col_discountAmount;
    @FXML
    private TableColumn<InvoiceDTO, String> tlb_col_totalPrice;
    @FXML
    private TableView<DetailInvoiceDTO> tblDetailInvoice;
    @FXML
    private TableColumn<DetailInvoiceDTO, String> tlb_col_productId;
    @FXML
    private TableColumn<DetailInvoiceDTO, String> tlb_col_quantity;
    @FXML
    private TableColumn<DetailInvoiceDTO, String> tlb_col_price;
    @FXML
    private TableColumn<DetailInvoiceDTO, String> tlb_col_totalPriceP;
    @FXML
    private Label id;

    @FXML
    private Label createDate;

    @FXML
    private Label employeeId;

    @FXML
    private Label customerId;

    @FXML
    private Label discountCode;

    @FXML
    private Label discountAmount;

    @FXML
    private Label totalPrice;
    @FXML
    private Button exportPdf;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button advanceSearchBtn;
    @FXML
    private TextField txtSearch;
    private String keyword = "";
    private InvoiceDTO selectedInvoice;

    @FXML
    public void initialize() {
        if (InvoiceBUS.getInstance().isLocalEmpty()) InvoiceBUS.getInstance().loadLocal();
        if (DetailInvoiceBUS.getInstance().isLocalEmpty()) DetailInvoiceBUS.getInstance().loadLocal();
        tblInvoice.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
//        tblDetailInvoice.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        Platform.runLater(() -> tblInvoice.getSelectionModel().clearSelection());
        Platform.runLater(() -> tblDetailInvoice.getSelectionModel().clearSelection());


        hideButtonWithoutPermission();
        setupListeners();

        loadTable();
    }
    @Override
    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_createDate.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTimeWithHour(cellData.getValue().getCreateDate())));
        tlb_col_employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        tlb_col_customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tlb_col_discountCode.setCellValueFactory(new PropertyValueFactory<>("discountCode"));
        tlb_col_discountAmount.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getDiscountAmount())));
        tlb_col_totalPrice.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getTotalPrice())));
        UiUtils.gI().addTooltipToColumn(tlb_col_createDate, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_discountAmount, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_totalPrice, 10);
        tblInvoice.setItems(FXCollections.observableArrayList(InvoiceBUS.getInstance().getAllLocal()));
    }

    public void loadSubTable(int invoiceId) {
        if (invoiceId <= 0) return;
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        this.id.setText(String.valueOf(selectedInvoice.getId()));
        this.createDate.setText(validationUtils.formatDateTime(selectedInvoice.getCreateDate()));
        this.employeeId.setText(String.valueOf(selectedInvoice.getEmployeeId()));
        this.customerId.setText(String.valueOf(selectedInvoice.getCustomerId()));
        this.discountCode.setText(selectedInvoice.getDiscountCode() != null ? selectedInvoice.getDiscountCode() : "Không có");
        this.discountAmount.setText(validationUtils.formatCurrency(selectedInvoice.getDiscountAmount()));
        this.totalPrice.setText(validationUtils.formatCurrency(selectedInvoice.getTotalPrice()));

        tlb_col_productId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        tlb_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tlb_col_price.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getPrice())));
        tlb_col_totalPriceP.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getTotalPrice())));

        UiUtils.gI().addTooltipToColumn(tlb_col_price, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_totalPriceP, 10);
        tblDetailInvoice.setItems(FXCollections.observableArrayList(DetailInvoiceBUS.getInstance().getAllDetailInvoiceByInvoiceIdLocal(invoiceId)));
        tblDetailInvoice.getSelectionModel().clearSelection();
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    @Override
    public void setupListeners() {
        tblInvoice.setOnMouseClicked(event -> {
            selectedInvoice = tblInvoice.getSelectionModel().getSelectedItem();
            if (isSelectedInvoice()) {
                loadSubTable(selectedInvoice.getId());
            } else {
                tblDetailInvoice.getItems().clear();
            }
        });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("L+�m m�+�i th+�nh c+�ng", "Th+�ng b+�o");
        });
        exportPdf.setOnAction(e -> handleExportPDF());
        advanceSearchBtn.setOnAction(e -> handleAdvanceSearch());
    }

    private void clearSubTable() {
        this.id.setText("");
        this.createDate.setText("");
        this.employeeId.setText("");
        this.customerId.setText("");
        this.discountCode.setText("");
        this.discountAmount.setText("");
        this.totalPrice.setText("");
        tblDetailInvoice.setItems(FXCollections.observableArrayList());
        tblDetailInvoice.getSelectionModel().clearSelection();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    @Override
    public void applyFilters() {
        InvoiceBUS invoiceBUS = InvoiceBUS.getInstance();
        clearSubTable();
        if (keyword.isEmpty()) {
            // Nߦ+u keyword r�+�ng, lߦ�y tߦ�t cߦ� h+�a -��n
            tblInvoice.setItems(FXCollections.observableArrayList(invoiceBUS.getAllLocal()));
        } else {
            try {
                int id = Integer.parseInt(keyword);
                tblInvoice.setItems(FXCollections.observableArrayList(
                        invoiceBUS.getByIdLocal(id)
                ));
            } catch (NumberFormatException e) {
                // X�+� l++ tr���+�ng h�+�p kh+�ng phߦ�i s�+�
                tblInvoice.setItems(FXCollections.observableArrayList());
            }
        }
        tblInvoice.getSelectionModel().clearSelection();
    }

    @Override
    public void resetFilters() {
        txtSearch.clear();
        clearSubTable();
        applyFilters();
    }

    @Override
    public void hideButtonWithoutPermission() {

    }

    private void handleAdvanceSearch() {
        InvoiceAdvanceSearchModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/InvoiceAdvanceSearchModal.fxml",
                null,
                "Tìm kiếm nâng cao"
        );
        if (modalController != null && modalController.isSaved()) {
            tblInvoice.setItems(FXCollections.observableArrayList(modalController.getFilteredInvoices()));
            tblInvoice.getSelectionModel().clearSelection();
            clearSubTable();
        }
    }

    private void handleExportPDF() {
        if (!isSelectedInvoice()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn hóa đơn.", "Thông báo");
            return;
        }
        PrintService.getInstance().printInvoiceForm(selectedInvoice.getId());
    }

    private boolean isSelectedInvoice() {
        selectedInvoice = tblInvoice.getSelectionModel().getSelectedItem();
        return selectedInvoice != null;
    }
}