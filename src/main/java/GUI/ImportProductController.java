package GUI;

import BUS.EmployeeBUS;
import BUS.ImportBUS;
import BUS.InvoiceBUS;
import BUS.ProductBUS;
import DTO.*;
import SERVICE.ImportService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ImportProductController {
    @FXML
    private Button btnExitImportingForm, btnImportListProductEdit, btnSearchProduct, btnImportListProductClear, btnImportListProductAdd, btnImportListProductRemove, btnSubmitImport, btnGetSupInfo;
    @FXML
    private Button btnClearProduct;
    @FXML
    private TextField txtImportId;
    @FXML
    private TextField txtEmployeeId;
    @FXML
    private TextField txtEmployeeFullName;
    @FXML
    private TextField txtCreateDate;
    @FXML
    private TextField txtSupplierId;
    @FXML
    private TextField txtSupplierName;
    @FXML
    private GridPane gpShowProductWrapper;
    @FXML
    private Label lbTotalImportPrice;
    @FXML
    private TextField txtFieldProductImage, txtFieldProductName, txtFieldProductQuantity, txtProductPrice, txtProductNameSearch;
    @FXML
    private ComboBox<CategoryDTO> cbxFieldProductCategory;
    @FXML
    private ComboBox<SupplierDTO> txtFieldProductSupplier;
    @FXML
    private TableView<TempDetailImportDTO> tbvDetailImportProduct;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tlb_col_index;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tlb_col_productName;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tlb_col_quantity;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tlb_col_price;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tlb_col_sellingPrice;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tlb_col_totalPrice;

    private ArrayList<TempDetailImportDTO> arrTempDetailImport = new ArrayList<>();
    private TempDetailImportDTO selectedTempDetailImport;
    private SupplierDTO selectedSupplier = null;

    @FXML
    public void initialize()
    {
        if (ImportBUS.getInstance().isLocalEmpty()) ImportBUS.getInstance().loadLocal();
        if (ProductBUS.getInstance().isLocalEmpty()) ProductBUS.getInstance().loadLocal();
        arrTempDetailImport.clear();
        loadProductWrapper();
        changeLabelContent();
        setOnMouseClicked();
    }

    private void loadProductWrapper() {
        clearGrid(gpShowProductWrapper);
        addConstraintRow(gpShowProductWrapper, ProductBUS.getInstance().getAllLocal(), 80.0);
        addEventClickForProduct(tbvDetailImportProduct, gpShowProductWrapper);
    }

    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        tlb_col_index.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        tlb_col_productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_quantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        tlb_col_price.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getPrice())));
        tlb_col_sellingPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getSellingPrice())));
        tlb_col_totalPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getTotalPrice())));
        tbvDetailImportProduct.setItems(FXCollections.observableArrayList(arrTempDetailImport));
        tbvDetailImportProduct.getSelectionModel().clearSelection();

        UiUtils.gI().addTooltipToColumn(tlb_col_productName, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_quantity, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_price, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_sellingPrice, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_totalPrice, 10);
    }

    // Set click Event
    private void setOnMouseClicked() {
        btnExitImportingForm.setOnMouseClicked(event -> onMouseClickedExitImportingForm());
        btnSearchProduct.setOnMouseClicked(event -> onMousedClickSearchProduct());
        btnGetSupInfo.setOnMouseClicked(event -> onMouseClickedShowSupplierContainer());
        btnImportListProductEdit.setOnMouseClicked(e -> onMouseClickedEdit());
        btnImportListProductRemove.setOnMouseClicked(e -> onMouseClickedRemove());
        btnImportListProductClear.setOnMouseClicked(e -> onMouseClickedClear());
        btnClearProduct.setOnMouseClicked(e -> {
            loadProductWrapper();
            txtProductNameSearch.setText("");
        });
        btnSubmitImport.setOnAction(e -> handleImport());
    }

    // search
    private void onMousedClickSearchProduct() {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().filterProducts("Tên sản phẩm", txtProductNameSearch.getText(), -1, 1, null, null);
        clearGrid(gpShowProductWrapper);
        if (list.isEmpty()) {
            return;
        }
        addConstraintRow(gpShowProductWrapper, list, 80.0);
    }

    // show select supplier
    private void onMouseClickedShowSupplierContainer() {
            SupForImportModalController modalController = UiUtils.gI().openStageWithController(
                    "/GUI/SupForImportModal.fxml",
                    null,
                    "Danh sách nhà cung cấp"
            );

            if (modalController != null && modalController.isSaved()) {
                selectedSupplier = new SupplierDTO(modalController.getSelectedSupplier());
                txtSupplierId.setText(String.valueOf(selectedSupplier.getId()));
                txtSupplierName.setText(selectedSupplier.getName());
                NotificationUtils.showInfoAlert("Chọn nhà cung cấp thành công.", "Thông báo");
            }
        }

    // close
    private void onMouseClickedExitImportingForm() {
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "danh sách chức năng");
        btnExitImportingForm.getScene().getWindow().hide();
    }

    private void changeLabelContent() {
        SessionManagerService ses = SessionManagerService.getInstance();
        txtImportId.setText(String.valueOf(ImportBUS.getInstance().getAllLocal().size()+1));
        txtEmployeeId.setText(String.valueOf(ses.employeeLoginId()));
        txtEmployeeFullName.setText(EmployeeBUS.getInstance().getByIdLocal(ses.employeeLoginId()).getFullName());
        txtCreateDate.setText(ValidationUtils.getInstance().formatDateTime(LocalDateTime.now()));
    }

    public void addConstraintRow(GridPane gridPane, ArrayList<ProductDTO> products, double height) {
        boolean row_col = true, wait = false;
        int quantity = products.size();
        int numRows = (int) Math.ceil(quantity / 2.0);

        // Thiết lập chiều cao cho các hàng
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(height);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        int prevRow = 0;
        for (int i = 0; i < quantity; i++) {
            ProductDTO product = products.get(i);
            String imageUrl = product.getImageUrl();
            File imageFile = null;
            Image image = null;

            // Kiểm tra đường dẫn ảnh
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageFile = new File(imageUrl);
            }

            // Load ảnh sản phẩm hoặc ảnh mặc định
            if (imageFile != null && imageFile.exists()) {
                try {
                    image = new Image(imageFile.toURI().toString());
                } catch (Exception e) {
                    // Nếu lỗi khi load ảnh -> fallback ảnh mặc định
                    image = loadDefaultImage();
                }
            } else {
                image = loadDefaultImage();
            }

            row_col = !row_col;
            int row = wait ? prevRow : i;
            int col = row_col ? 1 : 0;

            addProductToGrid(
                    gridPane,
                    row,
                    col,
                    image,
                    product.getName(),
                    product.getStockQuantity(),
                    product.getSellingPrice(),
                    product.getId()
            );

            wait = !wait;
            prevRow = i;
        }
    }

    private Image loadDefaultImage() {
            URL defaultImageUrl = getClass().getResource("/images/default/default.png");
            if (defaultImageUrl != null) {
                return new Image(defaultImageUrl.toExternalForm(), 100, 100, true, true);
            } else {
                System.err.println("Ảnh mặc định không tìm thấy.");
                return null;
            }
        }

    // add container
    public void addProductToGrid(GridPane gridPane, int row, int col, Image image, String name, int quantity, BigDecimal price, String id) {
        // ImageView
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        // Labels
        Label nameLabel = new Label("Name :");
        Label quantityLabel = new Label("Quantity :");
        Label priceLabel = new Label("Price :");

        Label nameValue = new Label(name);
        Label quantityValue = new Label(String.valueOf(quantity));
        Label priceValue = new Label(String.format("%.0f ₫", price));

        // VBox
        VBox infoBox = new VBox(10); // khoảng cách giữa các dòng
        infoBox.getChildren().addAll(
                createInfoRow(nameLabel, nameValue),
                createInfoRow(quantityLabel, quantityValue),
                createInfoRow(priceLabel, priceValue)
        );
        infoBox.setPrefHeight(134);

        // HBox
        HBox productBox = new HBox(10);
        productBox.getChildren().addAll(imageView, infoBox);
        productBox.setPadding(new Insets(5));
        productBox.setStyle("-fx-background-color: #e0ffff; -fx-border-color: #ccc;");
        productBox.setPrefHeight(134);
        //set name as id for quick searching
        productBox.setId(id);

        // Thêm vào grid
        gridPane.add(productBox, col, row);
    }

    private void onMouseClickedEdit() {
        if (isNotSelectedTempDetailImport()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn chi tiết phiếu nhập.", "Thông báo");
            return;
        }

        ImportProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ImportProductModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setTempDetailImport(selectedTempDetailImport);
                },
                "Sửa chi tiết phiếu nhập"
        );
        if (modalController != null && modalController.isSaved()) {
            loadCaculatedTotalImportPrice();
            loadTable();

            NotificationUtils.showInfoAlert("Sửa chi tiết phiếu nhập thành công.", "Thông báo");
        }
    }

    private void onMouseClickedRemove() {
        if (isNotSelectedTempDetailImport()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn chi tiết phiếu nhập.", "Thông báo");
            return;
        }
        arrTempDetailImport.remove(selectedTempDetailImport);
        addEventClickForProduct(tbvDetailImportProduct, gpShowProductWrapper);
        loadCaculatedTotalImportPrice();
        loadTable();

        NotificationUtils.showInfoAlert("Xóa chi tiết phiếu nhập thành công.", "Thông báo");
    }

    private void loadCaculatedTotalImportPrice() {
        BigDecimal totalImportPrice = BigDecimal.ZERO;

        for (TempDetailImportDTO detail : arrTempDetailImport) {
            totalImportPrice = totalImportPrice.add(detail.getTotalPrice());
        }
        lbTotalImportPrice.setText(ValidationUtils.getInstance().formatCurrency(totalImportPrice) + " Đ");
    }

    private void onMouseClickedClear() {
        arrTempDetailImport.clear();
        addEventClickForProduct(tbvDetailImportProduct, gpShowProductWrapper);
        loadCaculatedTotalImportPrice();
        loadTable();
        NotificationUtils.showInfoAlert("Xóa toàn bộ chi tiết phiếu nhập thành công.", "Thông báo");
    }

    private HBox createInfoRow(Label label, Label value) {
        HBox row = new HBox(5);
        row.getChildren().addAll(label, value);
        return row;
    }

    public void clearGrid(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
    }

    public void addEventClickForProduct(TableView<TempDetailImportDTO> tableView, GridPane gridPane) {
        ObservableList<Node> listNode = gridPane.getChildren();
        for (Node node : listNode) {
            String productID = node.getId();
            ProductDTO product = ProductBUS.getInstance().getByIdLocal(productID);
            if (product == null) continue;

            // Xóa sự kiện cũ nếu có
            node.setOnMouseClicked(null);

            boolean isAlreadyInList = arrTempDetailImport.stream()
                    .anyMatch(temp -> temp.getProductId().equals(productID));

            if (isAlreadyInList) {
                node.setOnMouseClicked(event -> addProductToTable(productID));
            } else {
                node.setOnMouseClicked(event -> {
                    handleOpenSubModal(product);

                    // Sau khi mở modal xong, cập nhật lại event click cho node
                    Platform.runLater(() -> addEventClickForProduct(tableView, gridPane));
                });
            }
        }
    }


    private void handleOpenSubModal(ProductDTO product) {
        ImportProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ImportProductModal.fxml",
                controller -> {
                    controller.setTypeModal(0);
                    controller.setProduct(product);
                },
                "Thêm chi tiết phiếu nhập"
        );
        if (modalController != null && modalController.isSaved()) {
            arrTempDetailImport.add(modalController.getTempDetailImport());
            loadCaculatedTotalImportPrice();
            loadTable();
            NotificationUtils.showInfoAlert("Thêm chi tiết phiếu nhập thành công.", "Thông báo");
        }
    }

    public void addProductToTable(String productId) {
        ProductDTO product = ProductBUS.getInstance().getByIdLocal(productId);
        if (product == null) return;

        for (TempDetailImportDTO temp : arrTempDetailImport) {
            if (temp.getProductId().equals(productId)) {
                temp.setQuantity(temp.getQuantity() + 1);
                BigDecimal total = temp.getPrice().multiply(BigDecimal.valueOf(temp.getQuantity()));
                temp.setTotalPrice(total);
                loadCaculatedTotalImportPrice();
                loadTable();
                return;
            }
        }

        // Nếu chưa có sản phẩm trong bảng thì thêm mới
        TempDetailImportDTO tempDetailImport = new TempDetailImportDTO(
                0,
                product.getId(),
                product.getName(),
                1,
                BigDecimal.ZERO, // hoặc product.getImportPrice() nếu bạn muốn
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        arrTempDetailImport.add(tempDetailImport);
        loadTable();
    }

    private boolean isNotSelectedTempDetailImport() {
        selectedTempDetailImport = tbvDetailImportProduct.getSelectionModel().getSelectedItem();
        return selectedTempDetailImport == null;
    }

    private boolean isNotSelectedSupplier() {
        return selectedSupplier == null;
    }

    private void handleImport() {
        if (arrTempDetailImport.isEmpty()) {
            NotificationUtils.showErrorAlert("Vui lòng thêm ít nhất một sản phẩm nhập.", "Thông báo");
            return;
        }
        if (isNotSelectedSupplier()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn nhà cung cấp để nhập hàng.", "Thông báo");
            return;
        }

        // Tạo phiếu nhập kèm chi tiết
        SessionManagerService ses = SessionManagerService.getInstance();
        ArrayList<DetailImportDTO> list = new ArrayList<>();
        ArrayList<ProductDTO> listProduct = new ArrayList<>();
        BigDecimal totalImportPrice = BigDecimal.ZERO;

        for (TempDetailImportDTO t : arrTempDetailImport) {
            DetailImportDTO di = new DetailImportDTO(t.getImportId(), t.getProductId(), t.getQuantity(), t.getPrice(), t.getTotalPrice());
            ProductDTO p = new ProductDTO(t.getProductId(), t.getName(), t.getQuantity() , t.getSellingPrice(), true, null, null, 0);
            list.add(di);
            listProduct.add(p);
            totalImportPrice = totalImportPrice.add(t.getTotalPrice());
        }

        ImportDTO temp = new ImportDTO(Integer.parseInt(txtImportId.getText().trim()), null, Integer.parseInt(txtEmployeeId.getText().trim()),
                Integer.parseInt(txtSupplierId.getText().trim()), totalImportPrice);

        boolean result = ImportService.getInstance().createImportWithDetailImport(temp, ses.employeeRoleId(),list , ses.employeeLoginId());

        // Sau đó tăng số lượng sản phẩm và set lại giá
        if (result && ProductBUS.getInstance().updateQuantitySellingPriceListProduct(listProduct, true)) {
            NotificationUtils.showInfoAlert("Nhập hàng thành công.", "Thông báo");
            arrTempDetailImport.clear();
            loadProductWrapper();
            selectedSupplier =null;
            txtSupplierId.setText("");
            txtSupplierName.setText("");
            loadTable();
            loadCaculatedTotalImportPrice();
        }
    }
}
