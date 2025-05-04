package GUI;

import BUS.CategoryBUS;
import BUS.CustomerBUS;
import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.CustomerDTO;
import INTERFACE.IController;
import SERVICE.ExcelService;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

public class CustomerController implements IController {
    // FXML Controls - Table
    @FXML private TableView<CustomerDTO> tblCustomer;
    @FXML private TableColumn<CustomerDTO, Integer> tlb_col_id;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_firstName;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_lastName;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_dateOfBirth;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_phone;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_address;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_status;
    @FXML private HBox functionBtns;
    // FXML Controls - Buttons and Filters
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn, exportExcel;
    @FXML private TextField txtSearch;
    @FXML private CheckBox ckbStatusFilter;
    @FXML private ComboBox<String> cbSearchBy;

    // Filter state
    private String searchBy = "Mã khách hàng";
    private String keyword = "";
    private int statusFilter = 1;
    private CustomerDTO selectedCustomer;

    @FXML
    public void initialize() {
        if (CustomerBUS.getInstance().isLocalEmpty()) CustomerBUS.getInstance().loadLocal();
        tblCustomer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        Platform.runLater(() -> tblCustomer.getSelectionModel().clearSelection());

        loadComboBox();
        setupListeners();

        hideButtonWithoutPermission();
        loadTable();
        applyFilters();
    }

    @Override
    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        CustomerBUS customerBus = CustomerBUS.getInstance();

        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData -> formatCell(validationUtils.formatDateTime(cellData.getValue().getDateOfBirth())));

        tlb_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tlb_col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));

        UiUtils.gI().addTooltipToColumn(tlb_col_address, 10);
        tblCustomer.setItems(FXCollections.observableArrayList(customerBus.getAllLocal()));
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll(
                "Mã khách hàng",
                "Họ đệm",
                "Tên",
                "Số điện thoại"
        );

        //default selection
        cbSearchBy.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false);
    }

    @Override
    public void setupListeners() {
        //Search and Filter Controls
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        ckbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });

        addBtn.setOnAction(event -> handleAddBtn());
        editBtn.setOnAction(event -> handleEditBtn());
        deleteBtn.setOnAction(event -> handleDeleteBtn());
    }

    private void handleStatusFilterChange() {
        statusFilter = ckbStatusFilter.isSelected() ? -1 : 1;
        applyFilters();
    }

    //searchbar
    private void handleSearchByChange() {
        searchBy = cbSearchBy.getValue();
        applyFilters();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    @Override
    public void applyFilters() {
        CustomerBUS customerBUS = CustomerBUS.getInstance();
        tblCustomer.setItems(FXCollections.observableArrayList(
                customerBUS.filterCustomers(searchBy, keyword, statusFilter)
        ));
        tblCustomer.getSelectionModel().clearSelection();
    }
    //searchbar

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst();//Chon gia tri dau tien
        ckbStatusFilter.setSelected(false);// Uncheck filter checkbox
        txtSearch.clear();//Clear search text field

        searchBy = "Mã khách hàng";
        keyword = "";
        statusFilter = 1;//chon active status

        applyFilters();//Reset filter
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(1);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(3);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(2);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    private void handleAddBtn() {
        CustomerModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CustomerModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm khách hàng"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm khách hàng thành công", "Thông báo");
            applyFilters();
        }
    }

    private void handleDeleteBtn() {
        if (isNotSelectedCustomer()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn khách hàng cần xóa", "Lỗi");
            return;
        }

        if (selectedCustomer.getId() == 1) {
            NotificationUtils.showErrorAlert("Bạn không thể xóa khách hàng vãng lai.", "Thông báo");
            return;
        }

        int deleteResult = CustomerBUS.getInstance().delete(
                selectedCustomer.getId(),
                SessionManagerService.getInstance().employeeRoleId(),
                SessionManagerService.getInstance().employeeLoginId()
        );

        switch (deleteResult) {
            case 1 -> {
                NotificationUtils.showInfoAlert("Xóa khách hàng thành công", "Thông báo");
                resetFilters();
            }
            case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi xóa khách hàng. Vui lòng thử lại.", "Thông báo");
            case 3 -> NotificationUtils.showErrorAlert("Bạn không thể xóa khách hàng vãng lai.", "Thông báo");
            case 4 -> NotificationUtils.showErrorAlert("Bạn không có quyền xóa khách hàng", "Thông báo");
            case 5 -> NotificationUtils.showErrorAlert("Khách hàng không tồn tại hoặc đã bị xóa", "Thông báo");
            case 6 -> NotificationUtils.showErrorAlert("Xoá khách hàng thất bại. Vui lòng thử lại.", "Thông báo");
            default -> NotificationUtils.showErrorAlert("Lỗi không xác định", "Thông báo");
        }
    }

    private void handleEditBtn() {
        CustomerDTO selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            NotificationUtils.showErrorAlert("Vui lòng chọn khách hàng cần sửa", "Lỗi");
            return;
        }

        CustomerModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CustomerModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setCustomer(selectedCustomer);
                },
                "Sửa khách hàng"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Sửa khách hàng thành công", "Thông báo");
            applyFilters();
        }
    }

    //done
    private boolean isNotSelectedCustomer() {
        selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
        return selectedCustomer == null;
    }
}
