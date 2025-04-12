package GUI;

import BUS.CategoryBUS;
import BUS.CustomerBUS;
import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.CustomerDTO;
import DTO.EmployeeDTO;
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

public class CustomerController implements IController {
    @FXML
    private TableView<CustomerDTO> tblCustomer;
    @FXML
    private TableColumn<CustomerDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<CustomerDTO, String> tlb_col_firstName;
    @FXML
    private TableColumn<CustomerDTO, String> tlb_col_lastName;
    @FXML
    private TableColumn<CustomerDTO, String> tlb_col_dateOfBirth;
    @FXML
    private TableColumn<CustomerDTO, String> tlb_col_phone;
    @FXML
    private TableColumn<CustomerDTO, String> tlb_col_address;
    @FXML
    private TableColumn<CustomerDTO, String> tlb_col_status;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private CheckBox ckbStatusFilter;
    @FXML
    private ComboBox<String> cbSearchBy;
    private String keyword = "";
    private String searchBy = "Mã khách hàng";
    private int statusFilter = 1;

    @FXML
    public void initialize() {
        if (CustomerBUS.getInstance().isLocalEmpty()) CustomerBUS.getInstance().loadLocal();
        tblCustomer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        Platform.runLater(() -> tblCustomer.getSelectionModel().clearSelection());

        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();

        loadTable();
        applyFilters();
    }

    @Override
    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        CustomerBUS cusBus = CustomerBUS.getInstance();

        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTime(cellData.getValue().getDateOfBirth())));

        tlb_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tlb_col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));

        UiUtils.gI().addTooltipToColumn(tlb_col_address, 10);
        tblCustomer.setItems(FXCollections.observableArrayList(cusBus.getAllLocal()));
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã khách hàng", "Họ đệm", "Tên");
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
        CustomerBUS cusBus = CustomerBUS.getInstance();
        tblCustomer.setItems(FXCollections.observableArrayList(
                cusBus.filterCustomers(searchBy, keyword, statusFilter)
        ));
        tblCustomer.getSelectionModel().clearSelection();
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
        boolean canAdd = SessionManagerService.getInstance().hasPermission(4);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(6);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(5);

        addBtn.setVisible(canAdd);
        addBtn.setManaged(canAdd);

        editBtn.setVisible(canEdit);
        editBtn.setManaged(canEdit);

        deleteBtn.setVisible(canDelete);
        deleteBtn.setManaged(canDelete);
    }
}
