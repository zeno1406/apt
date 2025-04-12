package GUI;

import BUS.CategoryBUS;
import DTO.CategoryDTO;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoryController implements IController {
    @FXML
    private TableView<CategoryDTO> tblCategory;
    @FXML
    private TableColumn<CategoryDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<CategoryDTO, String> tlb_col_name;
    @FXML
    private TableColumn<CategoryDTO, String> tlb_col_status;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbSearchBy;
    @FXML
    private CheckBox ckbStatusFilter;
    private String keyword = "";
    private String searchBy = "Mã thể loại";
    private int statusFilter = 1;

    @FXML
    public void initialize() {
        if (CategoryBUS.getInstance().isLocalEmpty()) CategoryBUS.getInstance().loadLocal();
        tblCategory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated
        Platform.runLater(() -> tblCategory.getSelectionModel().clearSelection());


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
        tlb_col_status.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));

        tblCategory.setItems(FXCollections.observableArrayList(CategoryBUS.getInstance().getAllLocal()));
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã thể loại", "Tên thể loại");
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
        CategoryBUS cateBUS = CategoryBUS.getInstance();
        tblCategory.setItems(FXCollections.observableArrayList(
                cateBUS.filterCategories(searchBy, keyword, statusFilter)
        ));
        tblCategory.getSelectionModel().clearSelection();
    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false); // Mặc định lọc Active
        txtSearch.clear();

        searchBy = "Mã thể loại";
        keyword = "";
        statusFilter = 1;
        applyFilters();

        NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(17);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(19);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(18);

        addBtn.setVisible(canAdd);
        addBtn.setManaged(canAdd);

        editBtn.setVisible(canEdit);
        editBtn.setManaged(canEdit);

        deleteBtn.setVisible(canDelete);
        deleteBtn.setManaged(canDelete);
    }
}
