package GUI;

import BUS.*;
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
import javafx.geometry.Pos;
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
    private Button btnExitImportingForm, btnImportListProductEdit, btnSearchProduct, btnImportListProductClear, btnImportListProductRemove, btnSubmitImport, btnGetSupInfo;
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
    private TextField txtProductNameSearch;
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
        if (CategoryBUS.getInstance().isLocalEmpty()) CategoryBUS.getInstance().loadLocal();
        if (ImportBUS.getInstance().isLocalEmpty()) ImportBUS.getInstance().loadLocal();
        if (ProductBUS.getInstance().isLocalEmpty()) ProductBUS.getInstance().loadLocal();
        arrTempDetailImport.clear();
        loadProductWrapper();
        changeLabelContent();
        setOnMouseClicked();
    }

    private void loadProductWrapper() {
        clearGrid(gpShowProductWrapper);
        addConstraintRow(gpShowProductWrapper, ProductBUS.getInstance().filterProducts("", "", -1, 1, null,null), 140);
        addEventClickForProduct(tbvDetailImportProduct, gpShowProductWrapper);
    }

    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();

        // Tạo số thứ tự
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

        // Đổ dữ liệu các cột
        tlb_col_productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_quantity.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        tlb_col_price.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getPrice())));
        tlb_col_sellingPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getSellingPrice())));
        tlb_col_totalPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getTotalPrice())));

        // Set Items
        tbvDetailImportProduct.setItems(FXCollections.observableArrayList(arrTempDetailImport));
        tbvDetailImportProduct.getSelectionModel().clearSelection();

        tlb_col_index.setPrefWidth(50);          // STT
        tlb_col_productName.setPrefWidth(150);    // Tên sản phẩm
        tlb_col_quantity.setPrefWidth(80);        // Số lượng
        tlb_col_price.setPrefWidth(100);          // Giá nhập
        tlb_col_sellingPrice.setPrefWidth(100);   // Giá bán
        tlb_col_totalPrice.setPrefWidth(120);     // Thành tiền
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
        addConstraintRow(gpShowProductWrapper, list, 140);
        addEventClickForProduct(tbvDetailImportProduct, gpShowProductWrapper);
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
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "Danh sách chức năng");
        btnExitImportingForm.getScene().getWindow().hide();
    }

    private void changeLabelContent() {
        SessionManagerService ses = SessionManagerService.getInstance();
        txtImportId.setText(String.valueOf(ImportBUS.getInstance().getAllLocal().size()+1));
        txtEmployeeId.setText(String.valueOf(ses.employeeLoginId()));
        txtEmployeeFullName.setText(EmployeeBUS.getInstance().getByIdLocal(ses.employeeLoginId()).getFullName());
        txtCreateDate.setText(ValidationUtils.getInstance().formatDateTime(LocalDateTime.now()));
    }

    private void addConstraintRow(GridPane gridPane, ArrayList<ProductDTO> products, double height) {
        boolean row_col = true, wait = false;
        int quantity = products.size();
        int numRows = (int) Math.ceil(quantity / 2.0);

        // Cấu hình gridPane cho khoảng cách đều
        gridPane.getRowConstraints().clear();
        gridPane.setVgap(2); // khoảng cách giữa các hàng

        // Thiết lập chiều cao cho các hàng
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(height);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        int prevRow = 0;
        CategoryBUS cateB = CategoryBUS.getInstance();

        for (int i = 0; i < quantity; i++) {
            ProductDTO product = products.get(i);
            String imageUrl = product.getImageUrl();
            File imageFile = null;
            Image image;

            // Kiểm tra đường dẫn ảnh
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageFile = new File(imageUrl);
            }

            // Load ảnh sản phẩm hoặc ảnh mặc định
            if (imageFile != null && imageFile.exists()) {
                try {
                    image = new Image(imageFile.toURI().toString(), 80, 80, false, true);
                } catch (Exception e) {
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
                    cateB.getByIdLocal(product.getCategoryId()).getName(),
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
            // Resize về đúng kích thước khung ảnh 80x100, không giữ tỷ lệ
            return new Image(defaultImageUrl.toExternalForm(), 80, 80, false, true);
        } else {
            System.err.println("Ảnh mặc định không tìm thấy.");
            return null;
        }
    }

    // add container
    private void addProductToGrid(GridPane gridPane, int row, int col, Image image, String name, String categoryName, int quantity, BigDecimal price, String id) {
        // ImageView
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(false); // Không giữ tỷ lệ

        // Labels
        Label nameLabel = new Label("Tên sản phẩm :");
        Label categoryLabel = new Label("Thể loại :");
        Label quantityLabel = new Label("Số lượng :");
        Label priceLabel = new Label("Giá bán :");

        Label nameValue = new Label(name);
        Label categoryValue = new Label(categoryName);
        Label quantityValue = new Label(String.valueOf(quantity));
        Label priceValue = new Label(ValidationUtils.getInstance().formatCurrency(price) + " VNĐ");

        // Set style cho các label gốc (in đậm)
        Label[] headerLabels = {nameLabel, categoryLabel, quantityLabel, priceLabel};
        for (Label label : headerLabels) {
            label.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold;");
        }

        // Set style cho các label giá trị (có cắt chữ nếu dài)
        Label[] valueLabels = {nameValue, categoryValue, quantityValue, priceValue};
        for (Label label : valueLabels) {
            label.setStyle("-fx-font-family: Arial; -fx-font-size: 14px;");
            label.setMaxWidth(150); // Giới hạn chiều rộng
            label.setEllipsisString("..."); // (tùy phiên bản JavaFX)
            label.setTooltip(new Tooltip(label.getText())); // Tooltip khi hover
        }
        // VBox chứa info
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
                createInfoRow(nameLabel, nameValue),
                createInfoRow(categoryLabel, categoryValue),
                createInfoRow(quantityLabel, quantityValue),
                createInfoRow(priceLabel, priceValue)
        );
        infoBox.setPrefHeight(150);

        // HBox chứa ảnh và info
        HBox productBox = new HBox(10);
        productBox.getChildren().addAll(imageView, infoBox);
        productBox.setPadding(new Insets(5));
        productBox.setStyle("-fx-background-color: #e0ffff; -fx-border-color: #ccc;");
        productBox.setPrefHeight(150);
        productBox.setMinWidth(300);
        productBox.setPrefWidth(300);

        // Đặt id để tìm kiếm nhanh
        productBox.setId(id);

        // Thêm vào GridPane
        gridPane.add(productBox, col, row);
    }

    private void onMouseClickedEdit() {
        if(tbvDetailImportProduct.isMouseTransparent()) {
            NotificationUtils.showErrorAlert("Vui lòng bấm nhập phiếu mới @_@!", "Thông báo");
            return;
        }

        if (isNotSelectedTempDetailImport()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn chi tiết phiếu nhập.", "Thông báo");
            return;
        }

        ImportProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ImportProductModal.fxml",
                controller -> {
                    controller.setTempDetailImport(selectedTempDetailImport);
                    controller.setTypeModal(1);
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
        if(tbvDetailImportProduct.isMouseTransparent()) {
            NotificationUtils.showErrorAlert("Vui lòng bấm nhập phiếu mới @_@!", "Thông báo");
            return;
        }

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
        loadProductWrapper();
        changeLabelContent();
        loadTable();
        NotificationUtils.showInfoAlert("Xóa toàn bộ chi tiết phiếu nhập thành công.", "Thông báo");
        selectedSupplier = null;
        txtSupplierId.setText("");
        txtSupplierName.setText("");
        makeEditable(btnSubmitImport);
        makeEditable(btnGetSupInfo);
        makeEditable(btnImportListProductEdit);
        makeEditable(btnImportListProductRemove);
        ObservableList<Node> listNode = gpShowProductWrapper.getChildren();
        for (Node node : listNode) {
            makeEditable(node);
        }
    }

    private HBox createInfoRow(Label label, Label value) {
        HBox row = new HBox(5);
        row.getChildren().addAll(label, value);
        return row;
    }

    private void clearGrid(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
    }

    private void addEventClickForProduct(TableView<TempDetailImportDTO> tableView, GridPane gridPane) {
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
        if (tbvDetailImportProduct.isMouseTransparent())
            return;
        ImportProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ImportProductModal.fxml",
                controller -> {
                    controller.setProduct(product);
                    controller.setTypeModal(0);
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
        if (tbvDetailImportProduct.isMouseTransparent())
            return;
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
        String extra = "Tổng tiền nhập: " + ValidationUtils.getInstance().formatCurrency(totalImportPrice) + " Đ";
        boolean submit =  NotificationUtils.showConfirmAlert("Xác nhận phiếu nhập", arrTempDetailImport, "Thông Báo", extra);
        // khong xac nhan khong nhap

        if (!submit) return;

        boolean result = ImportService.getInstance().createImportWithDetailImport(temp, ses.employeeRoleId(),list , ses.employeeLoginId());

        // Sau đó tăng số lượng sản phẩm và set lại giá
        if (result && ProductBUS.getInstance().updateQuantitySellingPriceListProduct(listProduct, true)) {
            NotificationUtils.showInfoAlert("Nhập hàng thành công.", "Thông báo");
            loadProductWrapper();
            makeReadOnly(btnSubmitImport);
            makeReadOnly(btnGetSupInfo);
            makeReadOnly(btnImportListProductEdit);
            makeReadOnly(btnImportListProductRemove);
            ObservableList<Node> listNode = gpShowProductWrapper.getChildren();
            for (Node node : listNode) {
                makeReadOnly(node);
            }
        }
    }

    private void makeReadOnly(Node node) {
        node.setDisable(false); // Cho phép hiện bình thường
        node.setMouseTransparent(true); // Không cho tương tác
        node.setFocusTraversable(false); // Không cho focus

        if (node instanceof TextInputControl textInput) {
            textInput.setEditable(false);
        }

        if (node instanceof ComboBox<?> comboBox) {
            comboBox.setEditable(false);

            Platform.runLater(() -> {
                Node arrow = comboBox.lookup(".arrow-button");
                if (arrow != null) {
                    arrow.setStyle("-fx-background-color: #999999; -fx-opacity: 0.75;");
                }

                Node arrowIcon = comboBox.lookup(".arrow");
                if (arrowIcon != null) {
                    arrowIcon.setStyle("-fx-shape: ''; -fx-background-color: transparent;");
                }
            });
        }

        if (node instanceof Button button) {
            button.setDisable(true); // Disable nút luôn
            button.setStyle("-fx-background-color: #999999; -fx-opacity: 0.75;");
        }
    }

    // Hàm mới để "mở khóa" button
    private void makeEditable(Node node) {
        node.setDisable(false);
        node.setMouseTransparent(false);
        node.setFocusTraversable(true);

        if (node instanceof Button) {
            node.setStyle(""); // Chỉ reset style nếu là Button
        }
    }


}
