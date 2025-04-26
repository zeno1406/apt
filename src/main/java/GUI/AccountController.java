package GUI;

import BUS.AccountBUS;
import BUS.EmployeeBUS;
import DTO.AccountDTO;
import INTERFACE.IController;
import SERVICE.RolePermissionService;
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

public class AccountController implements IController {
    @FXML
    private TableView<AccountDTO> tblAccount;
    @FXML
    private TableColumn<AccountDTO, Integer> tlb_col_employeeId;
    @FXML
    private TableColumn<AccountDTO, String> tlb_col_fullName;
    @FXML
    private TableColumn<AccountDTO, String> tlb_col_username;
    @FXML
    private TableColumn<AccountDTO, String> tlb_col_password;
    @FXML
    private HBox functionBtns;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbSearchBy;
    private String searchBy = "Mã nhân viên";
    private String keyword = "";
    private AccountDTO selectedAccount;
    @FXML
    public void initialize() {
        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();
        tblAccount.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated
        Platform.runLater(() -> tblAccount.getSelectionModel().clearSelection());
        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();

        loadTable();
        applyFilters();
    }

    @Override
    public void loadTable() {
        AccountBUS accountBUS = AccountBUS.getInstance();

        tlb_col_employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        tlb_col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        tlb_col_fullName.setCellValueFactory(cellData -> new SimpleStringProperty(EmployeeBUS.getInstance().getByIdLocal(cellData.getValue().getEmployeeId()).getFirstName() + " " + EmployeeBUS.getInstance().getByIdLocal(cellData.getValue().getEmployeeId()).getLastName())
        );
        tlb_col_password.setCellValueFactory(cellData -> new SimpleStringProperty("********"));
        tblAccount.setItems(FXCollections.observableArrayList(accountBUS.getAllLocal()));
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã nhân viên", "Tài khoản");
        cbSearchBy.getSelectionModel().selectFirst();
    }


    @Override
    public void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });

        addBtn.setOnAction(e -> handleAdd());
        editBtn.setOnAction(e -> handleEdit());
        deleteBtn.setOnAction(e -> handleDelete());
    }

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
        AccountBUS accBUS = AccountBUS.getInstance();
        tblAccount.setItems(FXCollections.observableArrayList(
                accBUS.filterAccounts(searchBy, keyword)
        ));
        tblAccount.getSelectionModel().clearSelection();

    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst();
        txtSearch.clear();

        searchBy = "Mã nhân viên";
        keyword = "";
        applyFilters();
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(27);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(29);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(28);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    private void handleDelete() {
        if (isNotSelectedAccount()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn tài khoản", "Thông báo");
            return;
        }
        if (selectedAccount.getEmployeeId() == SessionManagerService.getInstance().employeeLoginId()) {
            NotificationUtils.showErrorAlert("Bạn không thể xóa tài khoản của chính mình.", "Thông báo");
            return;
        }
        if (!UiUtils.gI().showConfirmAlert("Bạn chắc muốn xóa tài khoản này?", "Thông báo xác nhận")) return;

        int deleteResult = AccountBUS.getInstance().delete(selectedAccount.getEmployeeId(),SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
        switch (deleteResult) {
            case 1 ->
            {
                NotificationUtils.showInfoAlert("Xóa tài khoản thành công.", "Thông báo");
                resetFilters();
            }
            case 2 ->
                    NotificationUtils.showErrorAlert("Có lỗi khi xóa tài khoản. Vui lòng thử lại.", "Thông báo");
            case 3 ->
                    NotificationUtils.showErrorAlert("Không thể xóa tài khoản gốc.", "Thông báo");
            case 4 ->
                    NotificationUtils.showErrorAlert("Bạn không thể xóa tài khoản của chính mình.", "Thông báo");
            case 5 ->
                    NotificationUtils.showErrorAlert("Bạn không có quyền \"Xóa tài khoản\" để thực hiện thao tác này.", "Thông báo");
            case 6 ->
                    NotificationUtils.showErrorAlert("Bạn không thể xóa chức vụ ngang quyền", "Thông báo");
            case 7 ->
                    NotificationUtils.showErrorAlert("Xóa tài khoản thất bại. Vui lòng thử lại sau.", "Thông báo");
            default ->
                    NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
        }
    }

    private void handleAdd() {
        AccountModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/AccountModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm tài khoản"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm tài khoản thành công", "Thông báo");
            resetFilters();
        }
    }

    private void handleEdit() {
        if (isNotSelectedAccount()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn tài khoản", "Thông báo");
            return;
        }
        AccountModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/AccountModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setAccount(selectedAccount);
                },
                "Sửa tài khoản"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Sửa tài khoản thành công", "Thông báo");
            applyFilters();
        }
    }

    private boolean isNotSelectedAccount() {
        selectedAccount = tblAccount.getSelectionModel().getSelectedItem();
        return selectedAccount == null;
    }
}
