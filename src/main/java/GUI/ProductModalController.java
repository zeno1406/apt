package GUI;

import BUS.CategoryBUS;
import BUS.ProductBUS;
import DTO.CategoryDTO;
import DTO.ProductDTO;
import SERVICE.ImageService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import UTILS.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;

public class ProductModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtProductId;
    @FXML
    private TextField txtProductName;
    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtSellingPrice;
    @FXML
    private Button saveBtn,closeBtn,addCategorySubBtn;
    @FXML
    private Button choseImg;
    @FXML
    private ImageView imageView;
    private String imageUrl = null;
    @FXML
    private ComboBox<String> cbSelectCategory;
    @FXML
    private ComboBox<String> cbSelectStatus;
    @Getter
    private boolean isSaved;
    private int typeModal;
    private ProductDTO product;
    private final HashMap<String, Integer> categoryMap = new HashMap<>();
    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> {
            try {
                handleSave();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        closeBtn.setOnAction(e -> handleClose());
        addCategorySubBtn.setOnAction(e -> handleAddCategorySub());
        choseImg.setOnAction(e -> handleChoseImg());
    }

    private void loadComboBox() {
        cbSelectStatus.getItems().addAll("Hoạt động", "Ngưng hoạt động");

        CategoryBUS cateBUS = CategoryBUS.getInstance();
        categoryMap.clear();
        String selectedCategory = cbSelectCategory.getSelectionModel().getSelectedItem();
        cbSelectCategory.getItems().clear();
        for (CategoryDTO cate : cateBUS.getAllLocal()) {
            cbSelectCategory.getItems().add(cate.getName());
            categoryMap.put(cate.getName(), cate.getId());
        }
        if (selectedCategory != null && categoryMap.containsKey(selectedCategory)) {
            cbSelectCategory.getSelectionModel().select(selectedCategory);
        } else {
            cbSelectCategory.getSelectionModel().selectFirst();
        }
        cbSelectStatus.getSelectionModel().selectFirst();
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm sản phẩm");
            txtProductId.setText(ProductBUS.getInstance().autoId());
        } else {
            if (product == null) handleClose();
            modalName.setText("Sửa sản phẩm");
        }
    }

    private void handleSave() throws IOException {
        if (typeModal == 0) {
            insertProduct();
        } else {
            updateProduct();
        }
    }

    private int getSelectedCategory() {
        return categoryMap.getOrDefault(cbSelectCategory.getValue(), -1);
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
        txtProductName.setText(product.getName());
        CategoryDTO selectedCategory = CategoryBUS.getInstance().getByIdLocal(product.getCategoryId());
        if (selectedCategory != null) {
            cbSelectCategory.getSelectionModel().select(selectedCategory.getName());
        }
        txtProductId.setText(product.getId());
        cbSelectStatus.getSelectionModel().select(product.isStatus() ? "Hoạt động" : "Ngưng hoạt động");
        txtDescription.setText(product.getDescription() == null ? "" : product.getDescription());
        txtSellingPrice.setText(product.getSellingPrice().toString());
        if (SessionManagerService.getInstance().employeeRoleId() == 1) {
            txtSellingPrice.setMouseTransparent(false);
            txtSellingPrice.setFocusTraversable(true);
            txtSellingPrice.setStyle("-fx-background-color: linear-gradient(to bottom, #efefef, #eeeeee)");
        }

        this.imageUrl = product.getImageUrl();

        File imageFile = null;
        Image image = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageFile = new File(imageUrl);
        }

        if (imageFile != null && imageFile.exists()) {
            image = new Image(imageFile.toURI().toString());
        } else {
            URL resource = getClass().getResource("/images/default/default.png");
            if (resource != null) {
                image = new Image(resource.toExternalForm());
            } else {
                System.err.println("Resource not found: /images/default/default.png");
            }
        }

        if (image != null) {
            imageView.setImage(image);
            imageView.setFitWidth(217.5);
            imageView.setFitHeight(217.5);
        }

    }

    private void handleChoseImg() {
        // Mở hộp thoại chọn ảnh
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

        // Mở hộp thoại và nhận file được chọn
        File file = fileChooser.showOpenDialog(null);  // Mở hộp thoại chọn file

        if (file != null) {
            // Hiển thị ảnh trong ImageView (chỉ hiển thị ảnh mới chọn)
            Image image = new Image(file.toURI().toString());  // Dùng URI từ hệ thống tệp
            imageView.setImage(image);
            imageView.setFitWidth(217.5);
            imageView.setFitHeight(217.5);

            // Cập nhật imageUrl tạm thời cho modal (chỉ lưu đường dẫn file tạm thời)
            imageUrl = file.toURI().toString();  // Cập nhật đường dẫn tạm thời cho modal
//            System.out.println("Ảnh tạm thời: " + imageUrl);

        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String name = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();

        // Chỉ cần một lần gọi
        ValidationUtils validator = ValidationUtils.getInstance();

        if (name.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên sản phẩm không được để trống.", "Thông báo");
            focus(txtProductName);
            isValid = false;
        } else if (!validator.validateVietnameseText255(name)) {
            NotificationUtils.showErrorAlert("Tên sản phẩm không hợp lệ (Tối đa 50 ký tự, chỉ chữ và số, \"_\", \"-\", \"/\").", "Thông báo");
            focus(txtProductName);
            isValid = false;
        }

        // Kiểm tra mô tả
        if (isValid && !description.isEmpty() && !validator.validateVietnameseText65k4(description)) {
            NotificationUtils.showErrorAlert("Mô tả không hợp lệ (Tối đa 65.400 ký tự, chỉ chữ và số, \"_\", \"-\", \"/\").", "Thông báo");
            focus(txtDescription);
            isValid = false;
        }

        String sellingPrice = txtSellingPrice.getText().trim();
        if (isValid && sellingPrice.isEmpty()) { // Chỉ kiểm tra hệ số lương nếu tên chức vụ hợp lệ
            NotificationUtils.showErrorAlert("Hệ số lương không được để trống.", "Thông báo");
            focus(txtSellingPrice);
            isValid = false;
        } else if (isValid) {
            try {
                BigDecimal sellingPicex = new BigDecimal(sellingPrice);
                if (!validator.validateBigDecimal(sellingPicex, 10, 2, false)) {
                    NotificationUtils.showErrorAlert("Giá bạn không không hợp lệ (tối đa 10 chữ số, 2 số thập phân, không âm hoặc bằng 0).", "Thông báo");
                    focus(txtSellingPrice);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                NotificationUtils.showErrorAlert("Giá bạn phải là số.", "Thông báo");
                focus(txtSellingPrice);
                isValid = false;
            }
        }

        return isValid;
    }

    private void insertProduct() throws IOException {
        ProductBUS proBus = ProductBUS.getInstance();
        if (isValidInput()) {
            String newImgUrl = ImageService.gI().saveProductImage(txtProductId.getText().trim(), imageUrl);
            ProductDTO temp = new ProductDTO(txtProductId.getText().trim(), txtProductName.getText().trim(), 0, new BigDecimal(0),
                                            cbSelectStatus.getValue().equals("Hoạt động"), txtDescription.getText().trim(),
                                            newImgUrl, getSelectedCategory());
            int insertResult = proBus.insert(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm sản phẩm. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Thêm sản phẩm\" để thực hiện thao tác này.", "Thông báo");
                case 4 ->
                        NotificationUtils.showErrorAlert("Thể loại không hợp lệ hoặc đã bị xóa", "Thông báo");
                case 5 -> {
                    NotificationUtils.showErrorAlert("Tên sản phẩm đã tồn tại trong hệ thống.", "Thông báo");
                    focus(txtProductName);
                }
                case 6 -> NotificationUtils.showErrorAlert("Thêm sản phẩm thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }
    }

    private void updateProduct() throws IOException {
        ProductBUS proBus = ProductBUS.getInstance();
        if (isValidInput()) {
            String newImgUrl = product.getImageUrl();
            if (imageUrl != null && !imageUrl.equals(product.getImageUrl())) {
                newImgUrl = ImageService.gI().saveProductImage(txtProductId.getText().trim(), imageUrl);
            }

            ProductDTO temp = new ProductDTO(txtProductId.getText().trim(), txtProductName.getText().trim(), 0, new BigDecimal(txtSellingPrice.getText().trim()),
                    cbSelectStatus.getValue().equals("Hoạt động"), txtDescription.getText().trim(),
                    newImgUrl, getSelectedCategory());
            int updateResult = proBus.update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
            switch (updateResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi cập nhật sản phẩm. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Cập nhật sản phẩm\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Dữ liệu đầu vào không hợp lệ", "Thông báo");
                case 5 -> {
                    NotificationUtils.showErrorAlert("Tên sản phẩm đã tồn tại trong hệ thống.", "Thông báo");
                    focus(txtProductName);
                }
                case 6 -> NotificationUtils.showErrorAlert("Cập nhật sản phẩm thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }
    }

    private void handleAddCategorySub() {
        CategoryModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/CategoryModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm thể loại"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm thể loại thành công", "Thông báo");
            loadComboBox();
        }
    }

    private void focus(TextField textField) {
        textField.requestFocus();
    }

    private void focus(TextArea textArea) {
        textArea.requestFocus();
    }
}
