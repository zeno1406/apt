package GUI;

import BUS.SupplierBUS;
import DTO.SupplierDTO;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class SupplierController implements IController {
    @FXML
    private TableView<SupplierDTO> tblSupplier;
    @FXML
    private TableColumn<SupplierDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_name;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_phone;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_address;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_status;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private CheckBox ckbStatusFilter;
    @FXML
    private ComboBox<String> cbSearchBy;
    private String keyword = "";
    private String searchBy = "Mã nhà cung cấp";
    private int statusFilter = 1;

    @FXML
    public void initialize() {
        if (SupplierBUS.getInstance().isLocalEmpty()) SupplierBUS.getInstance().loadLocal();
        tblSupplier.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        Platform.runLater(() -> tblSupplier.getSelectionModel().clearSelection());

        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();

        loadTable();
        applyFilters();
    }

    @Override
    public void loadTable() {
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tlb_col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));
        UiUtils.gI().addTooltipToColumn(tlb_col_name, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_address, 10);
        tblSupplier.setItems(FXCollections.observableArrayList(SupplierBUS.getInstance().getAllLocal()));
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã nhà cung cấp", "Tên nhà cung cấp");
        cbSearchBy.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false);
    }

    @Override
    public void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        ckbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> resetFilters());
    }

    private void handleSearchByChange() {
        searchBy = cbSearchBy.getValue();
        applyFilters();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    private void handleStatusFilterChange() {
        statusFilter = ckbStatusFilter.isSelected() ? -1 : 1;
        applyFilters();
    }


    @Override
    public void applyFilters() {
        tblSupplier.setItems(FXCollections.observableArrayList(
                SupplierBUS.getInstance().filterSuppliers(searchBy, keyword, statusFilter)
        ));
        tblSupplier.getSelectionModel().clearSelection();
    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false); // Mặc định lọc Active
        txtSearch.clear();

        searchBy = "Mã khách hàng";
        keyword = "";
        statusFilter = 1;
        applyFilters();

        NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(10);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(12);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(11);

        addBtn.setVisible(canAdd);
        addBtn.setManaged(canAdd);

        editBtn.setVisible(canEdit);
        editBtn.setManaged(canEdit);

        deleteBtn.setVisible(canDelete);
        deleteBtn.setManaged(canDelete);
    }
}
