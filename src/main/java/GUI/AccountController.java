package GUI;

import BUS.AccountBUS;
import DTO.AccountDTO;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AccountController implements IController {
    @FXML
    private TableView<AccountDTO> tblAccount;
    @FXML
    private TableColumn<AccountDTO, Integer> tlb_col_employeeId;
    @FXML
    private TableColumn<AccountDTO, String> tlb_col_username;
    @FXML
    private TableColumn<AccountDTO, String> tlb_col_password;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbSearchBy;
    private String searchBy = "Mã nhân viên";
    private String keyword = "";
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
        cbSearchBy.getSelectionModel().selectFirst(); // Chọn giá trị đầu tiên
        txtSearch.clear();

        searchBy = "Mã nhân viên";
        keyword = "";
        applyFilters(); // Áp dụng lại bộ lọc

        NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(27);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(29);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(28);

        addBtn.setVisible(canAdd);
        addBtn.setManaged(canAdd);

        editBtn.setVisible(canEdit);
        editBtn.setManaged(canEdit);

        deleteBtn.setVisible(canDelete);
        deleteBtn.setManaged(canDelete);
    }
}
