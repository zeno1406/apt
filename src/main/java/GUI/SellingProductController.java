package GUI;

import BUS.*;
import DTO.*;

import SERVICE.InvoiceService;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SellingProductController {
    @FXML
    private Button btnExitSellingForm;

    @FXML
    private Button btnInvoiceListProductEdit;

    @FXML
    private Button btnSearchProduct;
    @FXML
    private Button btnGetDiscount;

    @FXML
    private Button btnInvoiceListProductClear;

    @FXML
    private Button btnInvoiceListProductRemove;

    @FXML
    private Button btnSubmitInvoice;

    @FXML
    private Button btnGetCusInfo;
    @FXML
    private Button btnClearProduct;
    @FXML
    private TextField txtInvoiceId;
    @FXML
    private TextField txtEmployeeId, txtCodeDiscount;
    @FXML
    private TextField txtEmployeeFullName;
    @FXML
    private TextField txtCreateDate;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private GridPane gpShowProductWrapper;
    @FXML
    private Label lbTotalImportPrice;
    @FXML
    private TextField txtProductNameSearch;
    @FXML
    private Label lbTotalInvoicePrice, lbDiscountPrice;
    @FXML
    private Label lbFinalTotalInvoicePrice;
    @FXML
    private ComboBox<String> cbCategoryFilter;
    private final HashMap<String, Integer> categoryMap = new HashMap<>();
    @FXML
    private TableView<TempDetailInvoiceDTO> tbvDetailInvoiceProduct;
    @FXML
    private TableColumn<TempDetailInvoiceDTO, String> tlb_col_index;
    @FXML
    private TableColumn<TempDetailInvoiceDTO, String> tlb_col_productName;
    @FXML
    private TableColumn<TempDetailInvoiceDTO, String> tlb_col_quantity;
    @FXML
    private TableColumn<TempDetailInvoiceDTO, String> tlb_col_price;
    @FXML
    private TableColumn<TempDetailInvoiceDTO, String> tlb_col_totalPrice;

    private ArrayList<TempDetailInvoiceDTO> arrTempDetailInvoice = new ArrayList<>();
    private TempDetailInvoiceDTO selectedTempDetailInvoice;
    private CustomerDTO selectedCustomer = null;
    private DiscountDTO selectedDiscount = null;
    private ArrayList<DetailDiscountDTO> selectedDetailDiscountList = new ArrayList<>();
    private DetailDiscountDTO selectedDetailDiscount = null;
    private BigDecimal totalPriceInvoice = BigDecimal.ZERO;
    private BigDecimal discountPrice = BigDecimal.ZERO;
    private BigDecimal finalTotalPriceInvoice = BigDecimal.ZERO;

    @FXML
    public void initialize()
    {
        if (CategoryBUS.getInstance().isLocalEmpty()) CategoryBUS.getInstance().loadLocal();
        if (ProductBUS.getInstance().isLocalEmpty()) ProductBUS.getInstance().loadLocal();
        if (InvoiceBUS.getInstance().isLocalEmpty()) InvoiceBUS.getInstance().loadLocal();
        arrTempDetailInvoice.clear();
        loadProductWrapper();
        changeLabelContent();
        setOnMouseClicked();
        loadComboBox();
    }

    private void loadProductWrapper() {
        clearGrid(gpShowProductWrapper);
        addConstraintRow(gpShowProductWrapper, ProductBUS.getInstance().filterProducts("", "", -1, 1, null,null, true), 140);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
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
        tlb_col_totalPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getTotalPrice())));

        tbvDetailInvoiceProduct.setItems(FXCollections.observableArrayList(arrTempDetailInvoice));
        tbvDetailInvoiceProduct.getSelectionModel().clearSelection();

        tlb_col_index.setPrefWidth(50);
        tlb_col_productName.setPrefWidth(150);
        tlb_col_quantity.setPrefWidth(80);
        tlb_col_price.setPrefWidth(100);
        tlb_col_totalPrice.setPrefWidth(120);
    }

    // Set click Event
    private void setOnMouseClicked() {
        btnExitSellingForm.setOnMouseClicked(event -> onMouseClickedExitSellingForm());
        btnSearchProduct.setOnMouseClicked(event -> onMousedClickSearchProduct());
        cbCategoryFilter.setOnAction(e -> handleCategoryFilterChange());
        btnGetCusInfo.setOnMouseClicked(event -> onMouseClickedShowCustomerContainer());
        btnInvoiceListProductRemove.setOnMouseClicked(e -> onMouseClickedRemove());
        btnInvoiceListProductClear.setOnMouseClicked(e -> onMouseClickedClear());
        btnClearProduct.setOnMouseClicked(e -> {
            loadProductWrapper();
            cbCategoryFilter.getSelectionModel().select("Tất cả");
            txtProductNameSearch.setText("");
        });
        btnInvoiceListProductEdit.setOnMouseClicked(event -> onMouseClickedEdit());
        btnSubmitInvoice.setOnMouseClicked(event -> handleInvoice());
        btnGetDiscount.setOnMouseClicked(event -> onMouseClickedShowDiscountContainer());
    }


    // search
    private void onMousedClickSearchProduct() {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().filterProducts("Tên sản phẩm", txtProductNameSearch.getText(), -1, 1, null, null);
        clearGrid(gpShowProductWrapper);
        if (list.isEmpty()) {
            return;
        }
        ArrayList<ProductDTO> finalFilter = list.stream().filter(product -> product.getStockQuantity() > 0).collect(Collectors.toCollection(ArrayList::new));
        addConstraintRow(gpShowProductWrapper, finalFilter, 140);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
    }

    // show select supplier
    private void onMouseClickedShowCustomerContainer() {
        CusForSellingModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CusForSellingModal.fxml",
                null,
                "Danh sách khách hàng"
        );

        if (modalController != null && modalController.isSaved()) {
            selectedCustomer = new CustomerDTO(modalController.getSelectedCustomer());
            txtCustomerId.setText(String.valueOf(selectedCustomer.getId()));
            txtCustomerName.setText(selectedCustomer.getFullName());
            NotificationUtils.showInfoAlert("Chọn khách hàng thành công.", "Thông báo");
        }
    }

    // close
    private void onMouseClickedExitSellingForm() {
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "Danh sách chức năng");
        btnExitSellingForm.getScene().getWindow().hide();
    }


    private void changeLabelContent() {
        SessionManagerService ses = SessionManagerService.getInstance();
        txtInvoiceId.setText(String.valueOf(InvoiceBUS.getInstance().getAllLocal().size()+1));
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
        if(tbvDetailInvoiceProduct.isMouseTransparent()) {
            NotificationUtils.showErrorAlert("Vui lòng bấm nhập phiếu mới @_@!", "Thông báo");
            return;
        }

        if (isNotSelectedTempDetailInvoice()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn sản phẩm.", "Thông báo");
            return;
        }

        SellingProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/SellingProductModal.fxml",
                controller -> {
                        controller.setTempDetailInvoice(selectedTempDetailInvoice);
                        controller.setTypeModal(1);
                },
                "Sửa chi tiết hóa đơn"
        );
        if (modalController != null && modalController.isSaved()) {
            this.arrTempDetailInvoice.set(selectedTempDetailInvoice.getInvoiceId(), modalController.getTempDetailInvoice());
            loadCaculatedTotalInvoicePrice();
            loadTable();

            NotificationUtils.showInfoAlert("Sửa thành công.", "Thông báo");
        }
    }

    private void onMouseClickedRemove() {
        if(tbvDetailInvoiceProduct.isMouseTransparent()) {
            NotificationUtils.showErrorAlert("Vui lòng bấm nhập phiếu mới @_@!", "Thông báo");
            return;
        }

        if (isNotSelectedTempDetailInvoice()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn sản phẩm.", "Thông báo");
            return;
        }
        arrTempDetailInvoice.remove(selectedTempDetailInvoice);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
        loadCaculatedTotalInvoicePrice();
        loadTable();

        NotificationUtils.showInfoAlert("Xóa sản phẩm khỏi đơn hàng thành công.", "Thông báo");
    }

    private void onMouseClickedClear() {
        arrTempDetailInvoice.clear();
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
        loadProductWrapper();
        changeLabelContent();
        loadTable();
        NotificationUtils.showInfoAlert("Xóa toàn bộ thành công.", "Thông báo");
        selectedDiscount = null;
        selectedCustomer = null;
        selectedDetailDiscount = null;
        selectedDetailDiscountList.clear();
        discountPrice = BigDecimal.ZERO;
        txtCodeDiscount.setText("");
        txtCustomerId.setText("");
        txtCustomerName.setText("");
        loadCaculatedTotalInvoicePrice();
        makeEditable(btnSubmitInvoice);
        makeEditable(btnGetCusInfo);
        makeEditable(btnGetDiscount);
        makeEditable(btnInvoiceListProductEdit);
        makeEditable(btnInvoiceListProductRemove);
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

    private void addEventClickForProduct(TableView<TempDetailInvoiceDTO> tableView, GridPane gridPane) {
        ObservableList<Node> listNode = gridPane.getChildren();
        for (Node node : listNode) {
            String productID = node.getId();
            ProductDTO product = ProductBUS.getInstance().getByIdLocal(productID);
            if (product == null) continue;

            // Xóa sự kiện cũ nếu có
            node.setOnMouseClicked(null);

            boolean isAlreadyInList = arrTempDetailInvoice.stream()
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
        if(tbvDetailInvoiceProduct.isMouseTransparent()) return;

        SellingProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/SellingProductModal.fxml",
                controller -> {
                    controller.setProduct(product);
                    controller.setTypeModal(0);
                },
                "Thêm Sản Phẩm"
        );
        if (modalController != null && modalController.isSaved()) {
            arrTempDetailInvoice.add(modalController.getTempDetailInvoice());
            loadCaculatedTotalInvoicePrice();
            loadTable();
            NotificationUtils.showInfoAlert("Thêm sản phẩm thành công.", "Thông báo");
        }
    }

    private void onMouseClickedShowDiscountContainer() {
        DiscountForSellingModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/DiscountForSellingModal.fxml",
                discountForSellingModalController -> discountForSellingModalController.setPrice(totalPriceInvoice),
                "Danh sách khuyến mãi"
        );

        if (modalController != null && modalController.isSaved()) {
            selectedDiscount = new DiscountDTO(modalController.getSelectedDiscount());
            txtCodeDiscount.setText(String.valueOf(selectedDiscount.getCode()));
            selectedDetailDiscountList = modalController.getDetailDiscountList();
            setDiscountPrice();
            loadCaculatedTotalInvoicePrice();
            NotificationUtils.showInfoAlert("Chọn  khuyến mãi thành công.", "Thông báo");
        }
    }

    private void setDiscountPrice() {
        int length = selectedDetailDiscountList.size();
        for (int i = length - 1; i >= 0; i--) {
            // Kiểm tra xem totalPriceInvoice có lớn hơn hoặc bằng mốc giảm giá hiện tại không
            if (totalPriceInvoice.compareTo(selectedDetailDiscountList.get(i).getTotalPriceInvoice()) >= 0) {
                if (selectedDiscount.getType() == 0) {  // Giảm theo tỷ lệ phần trăm
                    // Tính toán giá sau khi giảm theo tỷ lệ phần trăm
                    discountPrice = totalPriceInvoice.multiply(
                            (selectedDetailDiscountList.get(i).getDiscountAmount())
                                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP) // Sử dụng setScale và ROUND_HALF_UP thay cho ROUND_CEILING
                    );
                } else {  // Giảm theo giá trị cố định
                    // Tính toán giá sau khi giảm theo giá trị cố định
                    discountPrice = selectedDetailDiscountList.get(i).getDiscountAmount();
                }
                // Lưu thông tin chi tiết giảm giá đã chọn
                selectedDetailDiscount = new DetailDiscountDTO(selectedDetailDiscountList.get(i));
                return;  // Nếu chỉ muốn trả về sau khi tìm thấy một mốc phù hợp, thì vẫn giữ return
            }
        }
    }

    private void loadCaculatedTotalInvoicePrice() {
        BigDecimal totalImportPrice = BigDecimal.ZERO;
        ValidationUtils validate = ValidationUtils.getInstance();
        for (TempDetailInvoiceDTO detail : arrTempDetailInvoice) {
            totalImportPrice = totalImportPrice.add(detail.getTotalPrice());
        }
        totalPriceInvoice = totalImportPrice;
        finalTotalPriceInvoice = totalPriceInvoice.subtract(discountPrice);
        lbTotalInvoicePrice.setText(ValidationUtils.getInstance().formatCurrency(totalPriceInvoice) + " Đ");
        lbDiscountPrice.setText(validate.formatCurrency(discountPrice) + " Đ");
        lbFinalTotalInvoicePrice.setText(validate.formatCurrency(finalTotalPriceInvoice) + " Đ");
    }

    private void addProductToTable(String productId) {
        if(tbvDetailInvoiceProduct.isMouseTransparent()) return;

        ProductDTO product = ProductBUS.getInstance().getByIdLocal(productId);
        if (product == null) return;

        for (TempDetailInvoiceDTO temp : arrTempDetailInvoice) {
            if (temp.getProductId().equals(productId)) {
                int quantity = temp.getQuantity() + 1;
                if (quantity > product.getStockQuantity()) {
                    NotificationUtils.showErrorAlert("Vượt quá số lượng tồn kho!", "Thông báo");
                    return;
                }
                temp.setQuantity(quantity);
                BigDecimal total = temp.getPrice().multiply(BigDecimal.valueOf(temp.getQuantity()));
                temp.setTotalPrice(total);
                loadCaculatedTotalInvoicePrice();
                loadTable();
                return;
            }
        }
        loadTable();
    }

    private boolean isNotSelectedTempDetailInvoice() {
        selectedTempDetailInvoice = tbvDetailInvoiceProduct.getSelectionModel().getSelectedItem();
        return selectedTempDetailInvoice == null;
    }

    private void loadComboBox() {
        CategoryBUS cateBUS = CategoryBUS.getInstance();
        categoryMap.clear();

        cbCategoryFilter.getItems().add("Tất cả");
        categoryMap.put("Tất cả", -1);

        for (CategoryDTO cate : cateBUS.getAllLocal()) {
            cbCategoryFilter.getItems().add(cate.getName());
            categoryMap.put(cate.getName(), cate.getId());
        }

        cbCategoryFilter.getSelectionModel().selectFirst();
    }

    private void handleCategoryFilterChange() {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().filterProducts("", "",  categoryMap.getOrDefault(cbCategoryFilter.getValue(), -1), 1, null, null, true);
        clearGrid(gpShowProductWrapper);
        if (list.isEmpty()) {
            return;
        }
        addConstraintRow(gpShowProductWrapper, list, 140);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
    }

    private boolean isNotSelectedCustomer() {
        return selectedCustomer == null;
    }

    // submit form
    private void handleInvoice() {
        if (arrTempDetailInvoice.isEmpty()) {
            NotificationUtils.showErrorAlert("Vui lòng thêm ít nhất một sản phẩm.", "Thông báo");
            return;
        }
        if (isNotSelectedCustomer()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn khách hàng để bán hàng.", "Thông báo");
            return;
        }

        // Tạo phiếu nhập kèm chi tiết
        SessionManagerService ses = SessionManagerService.getInstance();
        ArrayList<DetailInvoiceDTO> list = new ArrayList<>();
        ArrayList<ProductDTO> listProduct = new ArrayList<>();

        for (TempDetailInvoiceDTO t : arrTempDetailInvoice) {
            DetailInvoiceDTO di = new DetailInvoiceDTO(t.getInvoiceId(), t.getProductId(), t.getQuantity(), t.getPrice(), t.getTotalPrice());
            ProductDTO p = new ProductDTO(t.getProductId(), t.getName(), t.getQuantity() , t.getPrice(), true, null, null, 0);
            list.add(di);
            listProduct.add(p);
        }
        // Kiem tra lai tranh truong hop ap dung giam dung moc thanh cong nhung sau do xoa bot khien gia k con du ap dung
        if (selectedDiscount != null && selectedDetailDiscount != null) {
            if (totalPriceInvoice.compareTo(selectedDetailDiscount.getTotalPriceInvoice()) < 0) {
                NotificationUtils.showErrorAlert("Giá trị đơn hàng của bạn không còn đủ để áp dụng khuyến mãi này.", "Thông báo");
                selectedDiscount = null;
                selectedDetailDiscount = null;
                txtCodeDiscount.setText("");
                discountPrice = BigDecimal.ZERO;
                loadCaculatedTotalInvoicePrice();
                return;
            }
            // trường hợp hóa đơn tăng lên sau khi áp khuyến mãi trước đó. phải hỏi để chọn lại km để áp mốc cao hơn
            checkDiscount();
        }

        InvoiceDTO temp = new InvoiceDTO(Integer.parseInt(txtInvoiceId.getText().trim()), null, Integer.parseInt(txtEmployeeId.getText().trim()),
                Integer.parseInt(txtCustomerId.getText().trim()), selectedDiscount == null ? null:txtCodeDiscount.getText().trim(), discountPrice != null ? discountPrice : BigDecimal.ZERO, totalPriceInvoice);
        ValidationUtils validate = ValidationUtils.getInstance();
        String discount = "\nGiảm giá: ";
        if (selectedDiscount != null && selectedDetailDiscount != null) {
            if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) != 0 ) {
                discount += validate.formatCurrency(discountPrice);
            } else discount += "0";
            discount += " Đ";
            if (selectedDiscount.getType() == 0) {
                discount += " - " + validate.formatCurrency(selectedDetailDiscount.getDiscountAmount()) + "%";
            }
        }
        String extra =
                "Tổng tiền hóa đơn: " + validate.formatCurrency(totalPriceInvoice) + " Đ" +
                        "\nMã khuyến mãi: " + (selectedDiscount == null ? "Không có" : txtCodeDiscount.getText().trim()) +
                        discount +
                        "\nThành tiền: " + validate.formatCurrency(finalTotalPriceInvoice) + " Đ";

        boolean submit =  NotificationUtils.showConfirmAlert("Xác nhận phiếu bán", arrTempDetailInvoice, "Thông Báo", extra);
        // khong xac nhan khong nhap
        if (!submit) return;
        boolean result = InvoiceService.getInstance().createInvoiceWithDetailInvoice(temp, ses.employeeRoleId(), list, ses.employeeLoginId());
        // Sau đó tăng số lượng sản phẩm và set lại giá
        if (result && ProductBUS.getInstance().updateQuantitySellingPriceListProduct(listProduct, false)) {
            NotificationUtils.showInfoAlert("Tạo hóa đơn thành công.", "Thông báo");
            loadProductWrapper();
            makeReadOnly(btnSubmitInvoice);
            makeReadOnly(btnGetCusInfo);
            makeReadOnly(btnGetDiscount);
            makeReadOnly(btnInvoiceListProductEdit);
            makeReadOnly(btnInvoiceListProductRemove);
            ObservableList<Node> listNode = gpShowProductWrapper.getChildren();
            for (Node node : listNode) {
                makeReadOnly(node);
            }
        }
    }

    private void checkDiscount() {
        int length = selectedDetailDiscountList.size();
        for (int i = length - 1; i >= 0; i--) {
            // Kiểm tra xem totalPriceInvoice có lớn hơn hoặc bằng mốc giảm giá cao nhat không
            if (totalPriceInvoice.compareTo(selectedDetailDiscountList.get(i).getTotalPriceInvoice()) >= 0
                    && selectedDetailDiscountList.get(i).getTotalPriceInvoice().compareTo(selectedDetailDiscount.getTotalPriceInvoice()) > 0) {
                NotificationUtils.showInfoAlert("Hóa đơn của bạn đã đạt đủ điều kiện cho mốc giảm giá tiếp theo.", "Thông báo");
                if (selectedDiscount.getType() == 0) {  // Giảm theo tỷ lệ phần trăm
                    // Tính toán giá sau khi giảm theo tỷ lệ phần trăm
                    discountPrice = totalPriceInvoice.multiply(
                            (selectedDetailDiscountList.get(i).getDiscountAmount())
                                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP) // Sử dụng setScale và ROUND_HALF_UP thay cho ROUND_CEILING
                    );
                } else {  // Giảm theo giá trị cố định
                    // Tính toán giá sau khi giảm theo giá trị cố định
                    discountPrice = selectedDetailDiscountList.get(i).getDiscountAmount();
                }
                // Lưu thông tin chi tiết giảm giá đã chọn
                selectedDetailDiscount = new DetailDiscountDTO(selectedDetailDiscountList.get(i));
                loadCaculatedTotalInvoicePrice();
                NotificationUtils.showInfoAlert("Áp dụng mốc khuyến mãi mới thành công.", "Thông báo");
                return;
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
