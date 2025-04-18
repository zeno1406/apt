package GUI;

import BUS.*;
import DTO.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import jdk.jfr.Category;

import javax.persistence.Table;
import javax.swing.*;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

public class SellingController {
    @FXML
    private Button btnExitSellingForm, btnSubmitIInvoice, btnSearchProduct, btnInvoiceListProductClear, btnInvoiceListProductEdit, btnInvoiceListProductRemove, btnGetCusInfo;
    @FXML
    private TextField txtSellingProductNameSearch, txtFieldShowInvoiceSaleCode, txtFieldShowInvoiceSaleValue, txtFieldInvoiceID, txtFieldEmpID, txtFieldEmpName, txtFieldInvoiceDate, txtFieldCusID, txtFieldCusName;
    @FXML
    private Label lbTotalInvoicePrice;
    @FXML
    private GridPane gpShowProductWrapper;
    @FXML
    private ComboBox<String> cbxListProductFilter;
    @FXML
    private TableView<TempDetailImportDTO> tbvDetailInvoiceProduct;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tbListInvoiceProductIndex;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tbListInvoiceProductName;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tbListInvoiceProductQuantity;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tbListInvoiceProductPrice;
//    @FXML
//    private TableColumn<TempDetailImportDTO, String> tlb_col_sellingPrice;
    @FXML
    private TableColumn<TempDetailImportDTO, String> tbListInvoiceProductTotalPrice;
    private ArrayList<TempDetailImportDTO> arrTempDetailImport = new ArrayList<>();
    private TempDetailImportDTO selectedTempDetailImport;

    @FXML
    public void initialize()
    {
        if (CategoryBUS.getInstance().isLocalEmpty()) CategoryBUS.getInstance().loadLocal();
        if (ProductBUS.getInstance().isLocalEmpty()) ProductBUS.getInstance().loadLocal();
        if (InvoiceBUS.getInstance().isLocalEmpty()) InvoiceBUS.getInstance().loadLocal();
        if (DetailInvoiceBUS.getInstance().isLocalEmpty()) DetailInvoiceBUS.getInstance().loadLocal();
        arrTempDetailImport.clear();
        loadProductWrapper();
        changeLabelContent();
        setOnMouseClicked();
        setListFilterForSearch(cbxListProductFilter, filterSearchProduct());
    }

    // Set click Event
    private void setOnMouseClicked() {
        btnExitSellingForm.setOnMouseClicked(event -> onMouseClickedExitSellingForm());
        btnSearchProduct.setOnMouseClicked(event -> onMousedClickSearchProduct());
        btnGetCusInfo.setOnMouseClicked(event -> onMouseClickedShowCustomerContainer());
        btnInvoiceListProductRemove.setOnMouseClicked(e -> onMouseClickedRemove());
        btnInvoiceListProductClear.setOnMouseClicked(e -> onMouseClickedClear());
        btnInvoiceListProductEdit.setOnMouseClicked(event -> onMouseClickedEdit());
        cbxListProductFilter.setOnAction(event -> setFilterProducts(selectedFilter()));
        btnSubmitIInvoice.setOnMouseClicked(event -> onMouseClickedSubmitInvoice());
    }

    // close
    private void onMouseClickedExitSellingForm() {
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "danh sách chức năng");
        btnExitSellingForm.getScene().getWindow().hide();
    }

    private void loadProductWrapper() {

        clearGrid(gpShowProductWrapper);
        addConstraintRow(gpShowProductWrapper, ProductBUS.getInstance().getProductWithValidQuantity(), 80.0);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
    }

    private void loadProductWrapper(ArrayList<ProductDTO> list) {
        clearGrid(gpShowProductWrapper);
        addConstraintRow(gpShowProductWrapper, list, 80.0);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
    }

    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        tbListInvoiceProductIndex.setCellFactory(column -> new TableCell<>() {
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
        tbListInvoiceProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbListInvoiceProductQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        tbListInvoiceProductPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getPrice())));
        tbListInvoiceProductTotalPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getTotalPrice())));
        tbvDetailInvoiceProduct.setItems(FXCollections.observableArrayList(arrTempDetailImport));
        tbvDetailInvoiceProduct.getSelectionModel().clearSelection();
    }


    // search
    private void onMousedClickSearchProduct() {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().filterProducts("Tên sản phẩm", txtSellingProductNameSearch.getText(), -1, 1, null, null);
        clearGrid(gpShowProductWrapper);
        if (list.isEmpty()) {
            return;
        }
        addConstraintRow(gpShowProductWrapper, list, 80.0);
    }

    // show select supplier
    private void onMouseClickedShowCustomerContainer() {
        CusForSellingModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CusForSellingModal.fxml",
                null,
                "Danh sách khách hàng"
        );

        if (modalController != null && modalController.isSaved()) {
            txtFieldCusID.setText(String.valueOf(modalController.getSelectedCustomer().getId()));
            txtFieldCusName.setText(modalController.getSelectedCustomer().getFirstName() + " " + modalController.getSelectedCustomer().getLastName());
            NotificationUtils.showInfoAlert("Chọn khách hàng thành công.", "Thông báo");
        }
    }


    private void changeLabelContent() {
        SessionManagerService ses = SessionManagerService.getInstance();
        txtFieldInvoiceID.setText(String.valueOf(ImportBUS.getInstance().getAllLocal().size()+1));
        txtFieldEmpID.setText(String.valueOf(ses.employeeLoginId()));
        txtFieldEmpName.setText(EmployeeBUS.getInstance().getByIdLocal(ses.employeeLoginId()).getFullName());
        txtFieldInvoiceDate.setText(ValidationUtils.getInstance().formatDateTime(LocalDateTime.now()));
    }


    //
// Add constraint row
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

    // add / clear container
    public void clearProductGrid(GridPane gridPane) {
        if (!gridPane.getChildren().isEmpty())
            gridPane.getChildren().clear();
    }
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
            NotificationUtils.showErrorAlert("Vui lòng chọn sản phẩm.", "Thông báo");
            return;
        }

        ImportProductModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/ImportProductModal.fxml",
                controller -> {
                    controller.setTempDetailImport(selectedTempDetailImport);
                    controller.setTypeModal(4);
                },
                "Sửa chi tiết phiếu nhập"
        );
        if (modalController != null && modalController.isSaved()) {
            loadCaculatedTotalImportPrice();
            loadTable();

            NotificationUtils.showInfoAlert("Sửa thành công.", "Thông báo");
        }
    }

    private void onMouseClickedRemove() {
        if (isNotSelectedTempDetailImport()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn sản phẩm.", "Thông báo");
            return;
        }
        arrTempDetailImport.remove(selectedTempDetailImport);
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
        loadCaculatedTotalImportPrice();
        loadTable();

        NotificationUtils.showInfoAlert("Xóa thành công.", "Thông báo");
    }

    private void loadCaculatedTotalImportPrice() {
        BigDecimal totalImportPrice = BigDecimal.ZERO;

        for (TempDetailImportDTO detail : arrTempDetailImport) {
            totalImportPrice = totalImportPrice.add(detail.getTotalPrice());
        }
        lbTotalInvoicePrice.setText(ValidationUtils.getInstance().formatCurrency(totalImportPrice) + " Đ");
    }



    private void onMouseClickedClear() {
        arrTempDetailImport.clear();
        addEventClickForProduct(tbvDetailInvoiceProduct, gpShowProductWrapper);
        loadCaculatedTotalImportPrice();
        loadTable();
        NotificationUtils.showInfoAlert("Xóa toàn bộ thành công.", "Thông báo");
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
                    controller.setProduct(product);
                    controller.setTypeModal(2);
                },
                "Thêm Sản Phẩm"
        );
        if (modalController != null && modalController.isSaved()) {
            arrTempDetailImport.add(modalController.getTempDetailImport());
            loadCaculatedTotalImportPrice();
            loadTable();
            NotificationUtils.showInfoAlert("Thêm sản phẩm thành công.", "Thông báo");
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
        selectedTempDetailImport = tbvDetailInvoiceProduct.getSelectionModel().getSelectedItem();
        return selectedTempDetailImport == null;
    }

    private ArrayList<String> filterSearchProduct() {
        ArrayList<CategoryDTO> categories = CategoryBUS.getInstance().getAllLocal();
        ArrayList<String> listNames = new ArrayList<>();
        for(CategoryDTO categoryDTO : categories)
            listNames.add(categoryDTO.getName());
        cbxListProductFilter.getItems().addAll();
        return listNames;
    }

    private void setListFilterForSearch(ComboBox<String> comboBox, ArrayList<String> filters) {
            comboBox.getItems().addAll(filters);
    }

    private void setFilterProducts(String nameFilter) {
        if (nameFilter.isEmpty())
            return;
        if (nameFilter.equalsIgnoreCase("Chưa xác định")) {
            loadProductWrapper();
            return;
        }

        int categoryID = CategoryBUS.getInstance().searchByName(nameFilter);
        if(categoryID == -1)
            return;
        ArrayList<ProductDTO> listProducts = ProductBUS.getInstance().filterProducts("", "", categoryID, 1, null, null);
        loadProductWrapper(ProductBUS.getInstance().getProductWithValidQuantity(listProducts));
    }

    private String selectedFilter() {
        return cbxListProductFilter.getSelectionModel().getSelectedItem();
    }

    // submit form
    private void onMouseClickedSubmitInvoice() {
        // get fields text
        String employeeID = txtFieldEmpID.getText();
        String customerID = txtFieldCusID.getText();
        String discountCode = txtFieldShowInvoiceSaleCode.getText();
        String discountValue = txtFieldShowInvoiceSaleValue.getText();
        LocalDateTime time = LocalDateTime.now();

        // require selected customer!!
        if (customerID.isEmpty()) {
            NotificationUtils.showErrorAlert("not found customer!", "Alert");
            return;
        }

        // get data table text
        ArrayList<TempDetailImportDTO> list = listProductTable(tbvDetailInvoiceProduct);
        System.out.println("list : " + list);
        if (list == null || list.isEmpty()) {
            NotificationUtils.showErrorAlert("not found any product!", "Alert");
            return;
        }

        // submit
        createInvoice(list, time, employeeID, customerID, discountCode, discountValue);
    }

    // get List data table
    private ArrayList<TempDetailImportDTO> listProductTable(TableView<TempDetailImportDTO> listProducts) {
        ArrayList<TempDetailImportDTO> list = new ArrayList<>(listProducts.getItems());
        if (list.getFirst() == null || list.isEmpty())
            return null;
        return list;
    }

    // create invoice
    private void createInvoice(ArrayList<TempDetailImportDTO> list, LocalDateTime time, String empID, String cusID, String discountCode, String discountValue) {
        InvoiceDTO invoice = new InvoiceDTO(InvoiceBUS.getInstance().getAllLocal().getLast().getId() + 1, time, ValidationUtils.getInstance().canParseToInt(empID), ValidationUtils.getInstance().canParseToInt(cusID),
                            discountCode, ValidationUtils.getInstance().canParseToBigDecimal(discountValue), getTotalPrice(list));


    }

    private BigDecimal getTotalPrice(ArrayList<TempDetailImportDTO> list) {
        BigDecimal total = new BigDecimal(0);
        for (TempDetailImportDTO product : list)
            total = total.add(product.getTotalPrice());
        return total;
    }
}
