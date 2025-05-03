package GUI;

import BUS.CategoryBUS;
import DTO.CategoryDTO;
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

public class CategoryController implements IController {
    @FXML private TableView<CategoryDTO> tblCategory;
    @FXML private TableColumn<CategoryDTO, Integer> tlb_col_id;
    @FXML private TableColumn<CategoryDTO, String> tlb_col_name;
    @FXML private TableColumn<CategoryDTO, String> tlb_col_status;
    @FXML private HBox functionBtns;
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML private TextField txtSearch;
    @FXML private CheckBox ckbStatusFilter;
    @FXML private ComboBox<String> cbSearchBy;

    // Filter state
    private String searchBy = "Mã thể loại";
    private String keyword = "";
    private int statusFilter = 1;
    private CategoryDTO selectedCategory;

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
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        CategoryBUS categoryBus = CategoryBUS.getInstance();

        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));
//        tlb_col_status.setCellValueFactory(cellData ->
//                new SimpleStringProperty(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));

        tblCategory.setItems(FXCollections.observableArrayList(categoryBus.getAllLocal()));
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã thể loại", "Tên thể loại");

        //default selection
        cbSearchBy.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false);
    }

    @Override
    public void setupListeners() {
        //Search and Filters Controls
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        ckbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công","Thông báo");
        });

        addBtn.setOnAction(event -> handleAddBtn());
        editBtn.setOnAction(event -> handleEditBtn());
        deleteBtn.setOnAction(event -> handleDeleteBtn());
        //them excel o duoi nay
    }

    private void handleStatusFilterChange() {
        statusFilter = ckbStatusFilter.isSelected() ? -1 : 1;
        applyFilters();
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
        CategoryBUS categoryBUS = CategoryBUS.getInstance();
        tblCategory.setItems(FXCollections.observableArrayList(
                categoryBUS.filterCategories(searchBy, keyword, statusFilter)
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


    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(17);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(19);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(18);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    private void handleAddBtn() {
        CategoryModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CategoryModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm thể loại"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm thể loại thành công", "Thông báo");
            applyFilters();
        }
    }

    private void handleDeleteBtn() {
        // Get selected category

        CategoryDTO selectedCategory = tblCategory.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            NotificationUtils.showErrorAlert("Vui lòng chọn một thể loại để xóa!", "Thông báo");
            return;
        }

        if(selectedCategory.getId() == 1)
        {
            NotificationUtils.showErrorAlert("Không thể xóa thể loại gốc!", "Thông báo");
            return;
        }

        int deleteResult = CategoryBUS.getInstance().delete(
                selectedCategory.getId(),
                SessionManagerService.getInstance().employeeRoleId(),
                SessionManagerService.getInstance().employeeLoginId());

        switch (deleteResult) {
            case 1 -> {
                NotificationUtils.showInfoAlert("Xóa thể loại thành công!", "Thông báo");
                applyFilters();
            }
            case 2  -> NotificationUtils.showErrorAlert("Lỗi xoá thể loại không thành công. Vui lòng thử lại", "Thông báo");
            case 4  -> NotificationUtils.showErrorAlert("Không có quyền xóa thể loại.", "Thông báo");
            case 3  -> NotificationUtils.showErrorAlert("Không thể xoá thể loại gốc. Vui lòng thử lại.", "Thông báo");
            case 6  -> NotificationUtils.showErrorAlert("Không thể xoá thể loại ở CSDL.", "Thông báo");
            case 5  -> NotificationUtils.showErrorAlert("Thể loại không tồn tại hoặc đã bị xoá.", "Thông báo");
            default -> NotificationUtils.showErrorAlert("Lỗi không xác định. Xóa thể loại thất bại.", "Thông báo");
        }
    }

    private void handleEditBtn() {
        // Get selected category
        CategoryDTO selectedCategory = tblCategory.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            NotificationUtils.showErrorAlert("Vui lòng chọn một thể loại để chỉnh sửa!", "Thông báo");
            return;
        }

        if(selectedCategory.getId() == 1)
        {
            NotificationUtils.showErrorAlert("Không thể sửa thể loại gốc!", "Thông báo");
            return;
        }

        CategoryModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CategoryModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setCategory(selectedCategory);
                },
                "Sửa thể loại"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Sửa thể loại thành công", "Thông báo");
            applyFilters();
        }
    }
}
