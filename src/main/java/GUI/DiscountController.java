
    package GUI;

    import BUS.DetailDiscountBUS;
    import BUS.DiscountBUS;
    import DTO.DetailDiscountDTO;
    import DTO.DiscountDTO;
    import INTERFACE.IController;
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

public class DiscountController implements IController {
    @FXML
    private TableView<DiscountDTO> tblDiscount;
    @FXML
    private TableColumn<DiscountDTO, Integer> tlb_col_code;
    @FXML
    private TableColumn<DiscountDTO, String> tlb_col_name;
    @FXML
    private TableColumn<DiscountDTO, String> tlb_col_type;
    @FXML
    private TableColumn<DiscountDTO, String> tlb_col_startDate;
    @FXML
    private TableColumn<DiscountDTO, String> tlb_col_endDate;
    @FXML
    private TableView<DetailDiscountDTO> tblDetailDiscount;
    @FXML
    private TableColumn<DetailDiscountDTO, String> tlb_col_totalPriceInvoice;
    @FXML
    private TableColumn<DetailDiscountDTO, String> tlb_col_discountAmount;
    @FXML
    private Label code, name, type, startDate, endDate;
    @FXML
    private HBox functionBtns;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn, advanceSearchBtn;
    @FXML
    private TextField txtSearch;
    private String keyword = "";
    private DiscountDTO selectedDiscount;

    @FXML
    public void initialize() {
        if (DiscountBUS.getInstance().isLocalEmpty()) DiscountBUS.getInstance().loadLocal();
        if (DetailDiscountBUS.getInstance().isLocalEmpty()) DetailDiscountBUS.getInstance().loadLocal();
        tblDiscount.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tblDetailDiscount.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        Platform.runLater(() -> tblDiscount.getSelectionModel().clearSelection());
        Platform.runLater(() -> tblDetailDiscount.getSelectionModel().clearSelection());


        hideButtonWithoutPermission();
//        loadComboBox();
        setupListeners();

        loadTable();
//        applyFilters();
    }
    @Override
    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        tlb_col_code.setCellValueFactory(new PropertyValueFactory<>("code"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_type.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().getType() == 0 ? "Phần trăm" : "Giảm cứng"));
        tlb_col_startDate.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTime(cellData.getValue().getStartDate())));
        tlb_col_endDate.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTime(cellData.getValue().getEndDate())));
        UiUtils.gI().addTooltipToColumn(tlb_col_name, 10);
        tblDiscount.setItems(FXCollections.observableArrayList(DiscountBUS.getInstance().getAllLocal()));
    }

    public void loadSubTable(String discountCode) {
        if (discountCode == null || discountCode.isEmpty()) return;
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        this.code.setText(selectedDiscount.getCode());
        this.name.setText(selectedDiscount.getName());
        this.type.setText(selectedDiscount.getType() == 0 ? "Phần trăm" : "Giảm cứng");
        this.startDate.setText(validationUtils.formatDateTime(selectedDiscount.getStartDate()));
        this.endDate.setText(validationUtils.formatDateTime(selectedDiscount.getEndDate()));

        tlb_col_totalPriceInvoice.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getTotalPriceInvoice())));
        tlb_col_discountAmount.setCellValueFactory(cellData -> {
            BigDecimal discountValue = cellData.getValue().getDiscountAmount();
            if (isSelectedDiscount() && selectedDiscount.getType() == 0) {
                // Type 0: Giߦ�m theo phߦ�n tr-�m (%)
                return formatCell(validationUtils.formatCurrency(discountValue) + " %");
            } else {
                // Type 1: Giߦ�m tr�+�c tiߦ+p bߦ�ng s�+� ti�+�n
                return formatCell(validationUtils.formatCurrency(discountValue));
            }
        });
        UiUtils.gI().addTooltipToColumn(tlb_col_discountAmount, 10);
        tblDetailDiscount.setItems(FXCollections.observableArrayList(DetailDiscountBUS.getInstance().getAllDetailDiscountByDiscountIdLocal(discountCode)));
        tblDetailDiscount.getSelectionModel().clearSelection();
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    @Override
    public void setupListeners() {
        tblDiscount.setOnMouseClicked(event -> {
            selectedDiscount = tblDiscount.getSelectionModel().getSelectedItem(); // Cߦ�p nhߦ�t selectedDiscount
            if (isSelectedDiscount()) {
                loadSubTable(selectedDiscount.getCode());
            } else {
                tblDetailDiscount.getItems().clear();
            }
        });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công.", "Thông báo");
        });
        advanceSearchBtn.setOnAction(e -> handleAdvanceSearch());
        addBtn.setOnAction(event -> handleAddBtn());
        deleteBtn.setOnAction(e -> handleDeleteBtn());
        editBtn.setOnAction(e -> handleEditBtn());
    }

    private void clearSubTable() {
        this.code.setText("");
        this.name.setText("");
        this.type.setText("");
        this.startDate.setText("");
        this.endDate.setText("");
        tblDetailDiscount.setItems(FXCollections.observableArrayList());
        tblDetailDiscount.getSelectionModel().clearSelection();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    @Override
    public void applyFilters() {
        DiscountBUS disBUS = DiscountBUS.getInstance();
        clearSubTable();
        if (keyword.isEmpty()) {
            // Nߦ+u keyword r�+�ng, lߦ�y tߦ�t cߦ� h+�a -��n
            tblDiscount.setItems(FXCollections.observableArrayList(disBUS.getAllLocal()));
        } else {
            tblDiscount.setItems(FXCollections.observableArrayList(disBUS.searchByCodeLocal(keyword)));

        }
        tblDiscount.getSelectionModel().clearSelection();
    }

    @Override
    public void resetFilters() {
        txtSearch.clear();
        clearSubTable();
        applyFilters();
    }

    private void handleAdvanceSearch() {
        DiscountAdvanceSearchModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/DiscountAdvanceSearchModal.fxml",
                null,
                "Tiềm kiếm nâng cao"
        );
        if (modalController != null && modalController.isSaved()) {
            tblDiscount.setItems(FXCollections.observableArrayList(modalController.getFilteredDiscounts()));
            tblDiscount.getSelectionModel().clearSelection();
            clearSubTable();
        }
    }

    private void handleAddBtn() {
        DiscountModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/DiscountModal.fxml",
                null,
                "Thêm khuyến mãi"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm khuyến mãi thành công.", "Thông báo");
            resetFilters();
        }
    }

    private void handleEditBtn() {
//        DiscountAdvanceSearchModalController modalController = UiUtils.gI().openStageWithController(
//                "/GUI/DiscountAdvanceSearchModal.fxml",
//                null,
//                "Tiềm kiếm nâng cao"
//        );
//        if (modalController != null && modalController.isSaved()) {
//            tblDiscount.setItems(FXCollections.observableArrayList(modalController.getFilteredDiscounts()));
//            tblDiscount.getSelectionModel().clearSelection();
//            clearSubTable();
//        }
    }

    private void handleDeleteBtn() {
//        DiscountAdvanceSearchModalController modalController = UiUtils.gI().openStageWithController(
//                "/GUI/DiscountAdvanceSearchModal.fxml",
//                null,
//                "Tiềm kiếm nâng cao"
//        );
//        if (modalController != null && modalController.isSaved()) {
//            tblDiscount.setItems(FXCollections.observableArrayList(modalController.getFilteredDiscounts()));
//            tblDiscount.getSelectionModel().clearSelection();
//            clearSubTable();
//        }
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(20);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(22);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(21);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    private boolean isSelectedDiscount() {
        selectedDiscount = tblDiscount.getSelectionModel().getSelectedItem();
        return selectedDiscount != null;
    }

}