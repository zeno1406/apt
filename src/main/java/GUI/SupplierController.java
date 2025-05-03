package GUI;

import BUS.CategoryBUS;
import BUS.SupplierBUS;
import DTO.CategoryDTO;
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
import javafx.scene.layout.HBox;

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
    private HBox functionBtns;
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
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });

        addBtn.setOnAction(event -> handleAddBtn());
        editBtn.setOnAction(event -> handleEditBtn());
        deleteBtn.setOnAction(event -> handleDeleteBtn());
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
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(10);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(12);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(11);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    private void handleAddBtn() {
        SupplierModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/SupplierModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm nhà cung cấp "
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm nhà cung cấp thành công", "Thông báo");
            resetFilters();
        }
    }

    private void handleDeleteBtn() {
        // Get selected supplier
        SupplierDTO selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            NotificationUtils.showErrorAlert("Vui lòng chọn một nhà cung cấp để xóa!", "Thông báo");
            return;
        }

        int deleteResult = SupplierBUS.getInstance().delete(
                selectedSupplier.getId(),
                SessionManagerService.getInstance().employeeRoleId(),
                SessionManagerService.getInstance().employeeLoginId());

        switch (deleteResult) {
            case 1 -> {
                NotificationUtils.showInfoAlert("Xóa nhà cung cấp thành công!", "Thông báo");
                resetFilters();
            }
            case 2  -> NotificationUtils.showErrorAlert("Lỗi xoá nhà cung cấp không thành công. Vui lòng thử lại", "Thông báo");
            case 4  -> NotificationUtils.showErrorAlert("Không có quyền xóa nhà cung cấp.", "Thông báo");
            case 6  -> NotificationUtils.showErrorAlert("Không thể xoá nhà cung cấp ở CSDL.", "Thông báo");
            case 5  -> NotificationUtils.showErrorAlert("Nhà cung cấp không tồn tại hoặc đã bị xoá.", "Thông báo");
            default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Xóa nhà cung cấp thất bại.", "Thông báo");
        }

        applyFilters();

    }

    private void handleEditBtn() {
        // Get selected supplier
        SupplierDTO selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            NotificationUtils.showErrorAlert("Vui lòng chọn một nhà cung cấp để chỉnh sửa!", "Thông báo");
            return;
        }

        SupplierModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/SupplierModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setSupplier(selectedSupplier);
                },
                "Sửa nhà cung cấp"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Sửa nhà cung cấp thành công", "Thông báo");
            applyFilters();
        }
    }
}
