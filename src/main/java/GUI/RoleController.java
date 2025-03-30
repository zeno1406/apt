package GUI;

import BUS.AccountBUS;
import BUS.RoleBUS;
import DTO.RoleDTO;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoleController implements IController {
    @FXML
    private TableView<RoleDTO> tblRole;
    @FXML
    private TableColumn<RoleDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<RoleDTO, String> tlb_col_name;
    @FXML
    private TableColumn<RoleDTO, String> tlb_col_description;
    @FXML
    private TableColumn<RoleDTO, String> tlb_col_salaryCoefficient;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbSearchBy;
    private String searchBy = "Mã chức vụ";
    private String keyword = "";
    @FXML
    public void initialize() {
        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();
        tblRole.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated
        Platform.runLater(() -> tblRole.getSelectionModel().clearSelection());
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
        tlb_col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        tlb_col_salaryCoefficient.setCellValueFactory(new PropertyValueFactory<>("salaryCoefficient"));
        tblRole.setItems(FXCollections.observableArrayList(RoleBUS.getInstance().getAllLocal()));
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã chức vụ", "Tên chức vụ");
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
        tblRole.setItems(FXCollections.observableArrayList(
                RoleBUS.getInstance().filterRoles(searchBy, keyword)
        ));
        tblRole.getSelectionModel().clearSelection();

    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst();
        txtSearch.clear();

        searchBy = "Mã chức vụ";
        keyword = "";
        applyFilters(); // Áp dụng lại bộ lọc

        NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(23);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(25);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(24);

        addBtn.setVisible(canAdd);
        addBtn.setManaged(canAdd);

        editBtn.setVisible(canEdit);
        editBtn.setManaged(canEdit);

        deleteBtn.setVisible(canDelete);
        deleteBtn.setManaged(canDelete);
    }
}
