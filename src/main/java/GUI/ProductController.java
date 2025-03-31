package GUI;

import BUS.DetailProductBUS;
import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.RoleBUS;
import DTO.*;
import INTERFACE.IController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ProductController implements IController {
    @FXML
    private TableView<CombinedProductDTO> tblProduct;
    @FXML
    private TableColumn<CombinedProductDTO, String> tlb_col_id;
    @FXML
    private TableColumn<CombinedProductDTO, String> tlb_col_name;
    @FXML
    private TableColumn<CombinedProductDTO, ImageView> tlb_col_imageUrl;
    @FXML
    private TableColumn<CombinedProductDTO, String> tlb_col_description;
    @FXML
    private TableColumn<CombinedProductDTO, String> tlb_col_categoryId;
    @FXML
    private TableColumn<CombinedProductDTO, Integer> tlb_col_stockQuantity;
    @FXML
    private TableColumn<CombinedProductDTO, BigDecimal> tlb_col_sellingPrice;
    @FXML
    private TableColumn<CombinedProductDTO, String> tlb_col_status;

    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
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
    private BigDecimal startPrice = new BigDecimal(0);
    private final HashMap<String, Integer> categoryMap = new HashMap<>();

    @FXML
    public void initialize() {
        if (ProductBUS.getInstance().isLocalEmpty()) ProductBUS.getInstance().loadLocal();
        if (DetailProductBUS.getInstance().isLocalEmpty()) DetailProductBUS.getInstance().loadLocal();
//        if (DetailProductBus.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();
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
//        cbSearchBy.getItems().addAll("Mã nhân viên", "Họ đệm", "Tên");
//
//        RoleBUS roleBUS = RoleBUS.getInstance();
//        roleMap.clear();
//
//        cbRoleFilter.getItems().add("Tất cả");
//        roleMap.put("Tất cả", -1);
//
//        for (RoleDTO role : roleBUS.getAllLocal()) {
//            cbRoleFilter.getItems().add(role.getName());
//            roleMap.put(role.getName(), role.getId());
//        }
//
//        cbSearchBy.getSelectionModel().selectFirst();
//        cbRoleFilter.getSelectionModel().selectFirst();
//        ckbStatusFilter.setSelected(false);
    }

    @Override
    public void loadTable() {
        ProductBUS productBUS = ProductBUS.getInstance();
        DetailProductBUS detailProductBUS = DetailProductBUS.getInstance();

        // Lấy dữ liệu một lần thay vì gọi nhiều lần
        ArrayList<ProductDTO> productList = productBUS.getAllLocal();
        ArrayList<DetailProductDTO> detailList = detailProductBUS.getAllLocal();

        // Dùng HashMap để tra cứu nhanh DetailProductDTO theo productId
        HashMap<String, DetailProductDTO> detailMap = new HashMap<>();
        for (DetailProductDTO detail : detailList) {
            detailMap.put(detail.getProductId(), detail);
        }

        // Kết hợp dữ liệu
        ArrayList<CombinedProductDTO> combinedList = new ArrayList<>();
        for (ProductDTO product : productList) {
            DetailProductDTO detail = detailMap.get(product.getId()); // Tra cứu nhanh
            combinedList.add(new CombinedProductDTO(product, detail));
        }

        // Cập nhật dữ liệu vào bảng
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("productId"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_stockQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        tlb_col_sellingPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        tlb_col_status.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động")
        );

        tlb_col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        tlb_col_categoryId.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getCategoryId()))
        );

        // Hiển thị hình ảnh từ URL
        tlb_col_imageUrl.setCellValueFactory(cellData -> {
            String imageUrl = cellData.getValue().getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                URL resource = ProductController.class.getResource("/images/icon/" + new File(imageUrl).getName());
                if (resource != null) {
                    ImageView imageView = new ImageView(new Image(resource.toExternalForm()));
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    return new SimpleObjectProperty<>(imageView);
                }
            }
            return new SimpleObjectProperty<>(null);
        });


        // Cập nhật dữ liệu cho bảng
        tblProduct.setItems(FXCollections.observableArrayList(combinedList));

        // Cập nhật giao diện
        tblProduct.refresh();
    }


    private void testImageLoading() {
        ArrayList<ProductDTO> productList = ProductBUS.getInstance().getAllLocal();

        for (ProductDTO product : productList) {
            DetailProductDTO dproduct = DetailProductBUS.getInstance().getByIdLocal(product.getId());
            String imageUrl = (dproduct != null) ? dproduct.getImageUrl() : "N/A";
//            String imageUrl = "./images/icon/product.png";
            System.out.println("Testing Image for Product ID: " + product.getId());
            System.out.println("Image URL: " + imageUrl);

            try {
                if (imageUrl == null || imageUrl.isEmpty()) {
                    System.err.println("⚠️ Image URL is null or empty!");
                } else {
                    Image testImage = new Image(imageUrl, true);
                    testImage.progressProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal.doubleValue() == 1.0) {
                            System.out.println("✅ Image loaded successfully: " + imageUrl);
                        }
                    });

                    testImage.errorProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal) {
                            System.err.println("❌ Failed to load image: " + imageUrl);
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("🚨 Exception while loading image: " + e.getMessage());
            }
        }
    }


    @Override
    public void setupListeners() {

    }

    @Override
    public void applyFilters() {

    }

    @Override
    public void resetFilters() {

    }

    @Override
    public void hideButtonWithoutPermission() {

    }
}
