package GUI;

import BUS.CategoryBUS;
import BUS.ProductBUS;
import DTO.*;
import INTERFACE.IController;
import SERVICE.ExcelService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
    private Button addBtn, editBtn, deleteBtn, refreshBtn, btnImportExcel;
    @FXML
    private TextField txtSearch;
    @FXML
    private TextField txtStartPrice;
    @FXML
    private TextField txtEndPrice;
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
    private BigDecimal startPrice = null;
    private BigDecimal endPrice = null;
    private final HashMap<String, Integer> categoryMap = new HashMap<>();
    private ProductDTO selectedProduct;

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
            File imageFile = null;
            Image image = null;

            // Kiểm tra nếu có ảnh sản phẩm
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageFile = new File(imageUrl);  // Đường dẫn ảnh người dùng nhập
//                System.out.println("Đường dẫn ảnh: " + imageFile.getAbsolutePath());
            }

            if (imageFile != null && imageFile.exists()) {
                try {
                    image = new Image(imageFile.toURI().toString(),  200, 200, true, true);
                } catch (Exception e) {
//                    System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
                }
            } else {
                URL defaultImageUrl = getClass().getResource("/images/default/default.png");
                if (defaultImageUrl != null) {
                    image = new Image(defaultImageUrl.toExternalForm());
                } else {
//                    System.err.println("Ảnh mặc định không tìm thấy trong resources.");
                }
            }

            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(70);
                imageView.setFitHeight(70);
                return new SimpleObjectProperty<>(imageView);
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        tlb_col_description.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription() == null ? "" : cellData.getValue().getDescription()));
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
        UiUtils.gI().addTooltipToColumn(tlb_col_categoryName, 10);
    }

    @Override
    public void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        cbCategoryFilter.setOnAction(event -> handleCategoryFilterChange());
        ckbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        txtStartPrice.textProperty().addListener((observable, oldValue, newValue) -> handlePriceChange());
        txtEndPrice.textProperty().addListener((observable, oldValue, newValue) -> handlePriceChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });

        addBtn.setOnAction(e -> handleAdd());
        editBtn.setOnAction(e -> handleEdit());
        deleteBtn.setOnAction(e -> handleDelete());
        btnImportExcel.setOnMouseClicked(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            importProductExcel(stage);
        });

    }

    private void handlePriceChange() {
        try {
            String startText = txtStartPrice.getText().trim();
            if (!startText.isEmpty()) {
                startPrice = new BigDecimal(startText);
            } else {
                startPrice = null;
            }
//            System.out.println(startPrice);

            String endText = txtEndPrice.getText().trim();
            if (!endText.isEmpty()) {
                endPrice = new BigDecimal(endText);
            } else {
                endPrice = null;
            }
//            System.out.println(endPrice);

            applyFilters();
        } catch (NumberFormatException e) {
//            System.out.println("Giá không hợp lệ: " + e.getMessage());
        }
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
                ProductBUS.getInstance().filterProducts(searchBy, keyword, categoryId, statusFilter, startPrice, endPrice)
        ));
        tblProduct.getSelectionModel().clearSelection();
    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst(); // Chọn giá trị đầu tiên
        cbCategoryFilter.getSelectionModel().select("Tất cả");
        ckbStatusFilter.setSelected(false); // Mặc định lọc Active
        txtSearch.clear();
        txtStartPrice.clear();
        txtEndPrice.clear();

        // Cập nhật lại các biến bộ lọc
        searchBy = "Mã nhân viên";
        keyword = "";
        categoryId = -1;
        statusFilter = 1; // Chỉ Active
        startPrice = null;
        endPrice = null;
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

    private void handleDelete() {
        if (isNotSelectedProduct()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn sản phẩm", "Thông báo");
            return;
        }

        if (ProductBUS.getInstance().getByIdLocal(selectedProduct.getId()).getStockQuantity() != 0) {
            NotificationUtils.showErrorAlert("Sản sản phẩm còn hàng tồn, không thể xóa!", "Thông báo");
            return;
        }

        if (!UiUtils.gI().showConfirmAlert("Bạn chắc muốn xóa sản phẩm này?", "Thông báo xác nhận")) return;

        int deleteResult = ProductBUS.getInstance().delete(selectedProduct.getId(),SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
        switch (deleteResult) {
            case 1 ->
            {
                NotificationUtils.showInfoAlert("Xóa sản phẩm thành công.", "Thông báo");
                resetFilters();
            }
            case 2 ->
                    NotificationUtils.showErrorAlert("Có lỗi khi xóa sản phẩm. Vui lòng thử lại.", "Thông báo");
            case 3 ->
                    NotificationUtils.showErrorAlert("Bạn không có quyền \"Xóa sản phẩm\" để thực hiện thao tác này.", "Thông báo");
            case 4 ->
                    NotificationUtils.showErrorAlert("Xóa sản phẩm thất bại. Vui lòng thử lại sau.", "Thông báo");
            case 5 ->
                    NotificationUtils.showErrorAlert("Sản sản phẩm còn hàng tồn, không thể xóa!", "Thông báo");
            default ->
                    NotificationUtils.showErrorAlert("Lỗi không xác định. Vui lòng thử lại sau.", "Thông báo");
        }
    }

    private void handleAdd() {
        ProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ProductModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm sản phẩm"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm sản phẩm thành công", "Thông báo");
            resetFilters();
        }
    }

    private void handleEdit() {
        if (isNotSelectedProduct()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn sản phẩm", "Thông báo");
            return;
        }
        ProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ProductModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setProduct(selectedProduct);
                },
                "Sửa sản phẩm"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Sửa sản phẩm thành công", "Thông báo");
            applyFilters();
        }
        tblProduct.refresh();
    }

    private boolean isNotSelectedProduct() {
        selectedProduct = tblProduct.getSelectionModel().getSelectedItem();
        return selectedProduct == null;
    }

    public void importProductExcel(Stage stage) {
        try {
            ExcelService.getInstance().ImportSheet("products", stage);
            applyFilters();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
