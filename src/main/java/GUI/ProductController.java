package GUI;

import BUS.CategoryBUS;
import BUS.ProductBUS;
import DTO.*;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashMap;

public class ProductController implements IController {
    @FXML
    private TableView<ProductDTO> tblProduct;
    @FXML
    private TableColumn<ProductDTO, String> tlb_col_id;
    @FXML
    private TableColumn<ProductDTO, String> tlb_col_name;
    @FXML
    private TableColumn<ProductDTO, ImageView> tlb_col_imageUrl;
    @FXML
    private TableColumn<ProductDTO, String> tlb_col_description;
    @FXML
    private TableColumn<ProductDTO, String> tlb_col_categoryName;
    @FXML
    private TableColumn<ProductDTO, Integer> tlb_col_stockQuantity;
    @FXML
    private TableColumn<ProductDTO, String> tlb_col_sellingPrice;
    @FXML
    private TableColumn<ProductDTO, String> tlb_col_status;
    @FXML
    private HBox functionBtns;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch, startPrice, endPrice;
    @FXML
    private CheckBox ckbStatusFilter;
    @FXML
    private ComboBox<String> cbSearchBy;
    @FXML
    private ComboBox<String> cbCategoryFilter;
    private String searchBy = "Mã sản phẩm";
    private String keyword = "";
    private int categoryId = -1;
    private int statusFilter = 1;
    private final HashMap<String, Integer> categoryMap = new HashMap<>();

    @FXML
    public void initialize() {
        if (ProductBUS.getInstance().isLocalEmpty()) ProductBUS.getInstance().loadLocal();
        if (CategoryBUS.getInstance().isLocalEmpty()) CategoryBUS.getInstance().loadLocal();
        tblProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated
        Platform.runLater(() -> tblProduct.getSelectionModel().clearSelection());


        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();

        loadTable();
        applyFilters();
//        testImageLoading();
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã sản phẩm", "Tên sản phẩm");

        CategoryBUS cateBUS = CategoryBUS.getInstance();
        categoryMap.clear();

        cbCategoryFilter.getItems().add("Tất cả");
        categoryMap.put("Tất cả", -1);

        for (CategoryDTO cate : cateBUS.getAllLocal()) {
            cbCategoryFilter.getItems().add(cate.getName());
            categoryMap.put(cate.getName(), cate.getId());
        }

        cbSearchBy.getSelectionModel().selectFirst();
        cbCategoryFilter.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false);
    }

    @Override
    public void loadTable() {
        CategoryBUS cateBUS = CategoryBUS.getInstance();

        // Cập nhật dữ liệu vào bảng
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_imageUrl.setCellValueFactory(cellData -> {
            String imageUrl = cellData.getValue().getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                URL resource = ProductController.class.getResource(imageUrl);
                if (resource != null) {
                    ImageView imageView = new ImageView(new Image(resource.toExternalForm()));
                    imageView.setFitWidth(70);
                    imageView.setFitHeight(70);
                    return new SimpleObjectProperty<>(imageView);
                }
            }
            return new SimpleObjectProperty<>(null);
        });
        tlb_col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        tlb_col_categoryName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cateBUS.getByIdLocal(cellData.getValue().getCategoryId()).getName())
        );
        tlb_col_sellingPrice.setCellValueFactory(cellData -> new SimpleStringProperty(ValidationUtils.getInstance().formatCurrency(cellData.getValue().getSellingPrice())));
        tlb_col_stockQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        tlb_col_status.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động")
        );
        UiUtils.gI().addTooltipToColumn(tlb_col_name, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_description, 10);

    }



    @Override
    public void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        cbCategoryFilter.setOnAction(event -> handleCategoryFilterChange());
        ckbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });

//        addBtn.setOnAction(event -> handleAddBtn());
    }

    private void handleSearchByChange() {
        searchBy = cbSearchBy.getValue();
        applyFilters();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    private void handleCategoryFilterChange() {
        categoryId = getSelectedRoleId();
        applyFilters();
    }

    private int getSelectedRoleId() {
        return categoryMap.getOrDefault(cbCategoryFilter.getValue(), -1);
    }

    private void handleStatusFilterChange() {
        statusFilter = ckbStatusFilter.isSelected() ? -1 : 1;
        applyFilters();
    }

    @Override
    public void applyFilters() {
        tblProduct.setItems(FXCollections.observableArrayList(
                ProductBUS.getInstance().filterProducts(searchBy, keyword, categoryId, statusFilter)
        ));
        tblProduct.getSelectionModel().clearSelection();
    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst(); // Chọn giá trị đầu tiên
        cbCategoryFilter.getSelectionModel().select("Tất cả");
        ckbStatusFilter.setSelected(false); // Mặc định lọc Active
        txtSearch.clear();

        // Cập nhật lại các biến bộ lọc
        searchBy = "Mã nhân viên";
        keyword = "";
        categoryId = -1;
        statusFilter = 1; // Chỉ Active
        applyFilters(); // Áp dụng lại bộ lọc
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(7);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(9);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(8);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }
}
