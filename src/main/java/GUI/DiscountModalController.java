package GUI;

import BUS.DetailDiscountBUS;
import BUS.DiscountBUS;
import DTO.*;
import INTERFACE.ServiceAccessCode;
import SERVICE.DiscountService;
import SERVICE.RolePermissionService;
import SERVICE.SessionManagerService;
import UTILS.AvailableUtils;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DiscountModalController {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtDiscountCode;
    @FXML
    private TextField txtDiscountName;
    @FXML
    private ComboBox<String> cbTypeDiscount;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private Button saveBtn,closeBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TableView<DetailDiscountDTO> tblDetailDiscount;
    @FXML
    private TableColumn<DetailDiscountDTO, String> tlb_col_totalPriceInvoice;
    @FXML
    private TableColumn<DetailDiscountDTO, String> tlb_col_discountAmount;
    private DetailDiscountDTO selectedDetailDiscount;
    private ArrayList<DetailDiscountDTO> arrDetailDiscount = new ArrayList<>();
    private DiscountDTO discount;

    @Getter
    private boolean isSaved;
    private int typeModal;

    @FXML
    public void initialize() {
        arrDetailDiscount.clear();
        loadComboBox();
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
        addBtn.setOnAction(event -> handleAddBtn());
        deleteBtn.setOnAction(e -> handleDeleteBtn());
        editBtn.setOnAction(e -> handleEditBtn());
    }

    private void loadComboBox() {
        cbTypeDiscount.getItems().addAll("Phần trăm", "Giảm cứng");
        cbTypeDiscount.getSelectionModel().selectFirst();
    }

    public void setDiscount(DiscountDTO discount) {
        this.discount = discount;
        txtDiscountCode.setText(discount.getCode());
        txtDiscountName.setText(discount.getName());
        cbTypeDiscount.getSelectionModel().select(discount.getType() == 0 ? "Phần trăm" : "Giảm cứng");
        LocalDateTime startDate = discount.getStartDate();
        LocalDateTime endDate = discount.getEndDate();

        dpStartDate.setValue(startDate != null ? startDate.toLocalDate() : null);
        dpEndDate.setValue(endDate != null ? endDate.toLocalDate() : null);

    }

    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        tlb_col_totalPriceInvoice.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getTotalPriceInvoice())));
        tlb_col_discountAmount.setCellValueFactory(cellData ->
                new SimpleStringProperty(validationUtils.formatCurrency(cellData.getValue().getDiscountAmount())));

        UiUtils.gI().addTooltipToColumn(tlb_col_totalPriceInvoice, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_discountAmount, 10);
        tblDetailDiscount.setItems(FXCollections.observableArrayList(arrDetailDiscount));
        tblDetailDiscount.getSelectionModel().clearSelection();
    }

    private boolean isValidInput() {
        boolean isValid = true;
        String discountCode = txtDiscountCode.getText().trim();
        String discountName = txtDiscountName.getText().trim();
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        ValidationUtils validator = ValidationUtils.getInstance();

        // Kiểm tra mã khuyến mãi
        if (discountCode.isEmpty()) {
            NotificationUtils.showErrorAlert("Mã khuyến mãi không được để trống.", "Thông báo");
            clearAndFocus(txtDiscountCode);
            isValid = false;
        } else if (!validator.validateDiscountCode(discountCode, 4, 50)) {
            NotificationUtils.showErrorAlert("Mã khuyến mãi không hợp lệ (Tối thiểu 4 và tối đa 50 ký tự, chỉ chữ và số, không được chứa khoảng trắng).", "Thông báo");
            clearAndFocus(txtDiscountCode);
            isValid = false;
        }

        // Kiểm tra tên khuyến mãi
        if (isValid && discountName.isEmpty()) {
            NotificationUtils.showErrorAlert("Tên khuyến mãi không được để trống ", "Thông báo");
            clearAndFocus(txtDiscountName);
            isValid = false;
        } else if (isValid && !validator.validateVietnameseText100(discountName)) {
            NotificationUtils.showErrorAlert("Tên khuyến mãi không hợp lệ (Tối đa 100 ký tự, chỉ chữ và số, \"_\", \"-\", \"/\").", "Thông báo");
            clearAndFocus(txtDiscountName);
            isValid = false;
        }

        // Kiểm tra ngày bắt đầu/kết thúc
        if (isValid && (startDate == null || endDate == null)) {
            NotificationUtils.showErrorAlert("Ngày bắt đầu và ngày kết thúc không được bỏ trống.", "Thông báo");
            isValid = false;
        } else if (isValid) {
            LocalDate today = LocalDate.now();

            if (startDate.isAfter(endDate)) {
                NotificationUtils.showErrorAlert("Ngày bắt đầu không được lớn hơn ngày kết thúc.", "Thông báo");
                isValid = false;
            } else if (startDate.isBefore(today)) {
                NotificationUtils.showErrorAlert("Ngày bắt đầu phải là hôm nay hoặc sau hôm nay.", "Thông báo");
                isValid = false;
            } else if (endDate.isBefore(today)) {
                NotificationUtils.showErrorAlert("Ngày kết thúc phải là hôm nay hoặc sau hôm nay.", "Thông báo");
                isValid = false;
            }
        }

        // Kiểm tra chi tiết khuyến mãi
        if (isValid && arrDetailDiscount.size() == 0) {
            NotificationUtils.showErrorAlert("Vui lòng thêm ít nhất 1 chi tiết khuyến mãi.", "Thông báo");
            isValid = false;
        }

        return isValid;
    }

    private void handleSave() {
        if (typeModal == 0) {
            insertDiscount();

        } else {
            updateDiscount();
        }
    }

    public void setTypeModal(int type) {
        if (type != 0 && type != 1) handleClose();
        typeModal = type;
        if (typeModal == 0) {
            modalName.setText("Thêm khuyến mãi");
        } else {
            if (discount == null) handleClose();
            modalName.setText("Sửa khuyến mãi");
            if (!AvailableUtils.getInstance().isNotUsedDiscount(discount.getCode())) makeReadOnly(dpStartDate);
            makeReadOnly(cbTypeDiscount);
            makeReadOnly(txtDiscountCode);
            arrDetailDiscount.clear();
            arrDetailDiscount.addAll(DetailDiscountBUS.getInstance().getAllDetailDiscountByDiscountIdLocal(discount.getCode()));
            loadTable();
        }
    }

    private void handleAddBtn() {
        DetailDiscountModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/DetailDiscountModal.fxml",
                controller -> controller.setTypeModal(0, cbTypeDiscount.getValue().equals("Phần trăm")),
                "Thêm chi tiết khuyến mãi"
        );
        if (modalController != null && modalController.isSaved()) {
            DetailDiscountDTO temp = new DetailDiscountDTO(modalController.getDetailDiscount());
            if (isDuplicateDetailDiscount(temp)) {
                NotificationUtils.showErrorAlert("Mốc tổng tiền hóa đơn tối thiểu này đã tồn tại trong chương trình khuyến mãi.", "Thông báo");
                return;
            }
            arrDetailDiscount.add(temp);
            makeReadOnly(cbTypeDiscount);
            loadTable();
            NotificationUtils.showInfoAlert("Thêm chi tiết khuyến mãi thành công.", "Thông báo");
        }
    }

    private void updateDiscount() {
        if (arrDetailDiscount.isEmpty()) {
            NotificationUtils.showErrorAlert("Vui lòng thêm ít nhất một chi tiết khuyến mãi.", "Thông báo");
            return;
        }

        if (!isValidInput()) return;
        DiscountDTO temp = new DiscountDTO(txtDiscountCode.getText().trim(), txtDiscountName.getText().trim(),
                cbTypeDiscount.getValue().equals("Phần trăm") ? 0 : 1,
                dpStartDate.getValue().atStartOfDay(), dpEndDate.getValue().atStartOfDay());
        int updateResult = DiscountBUS.getInstance().update(temp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
        switch (updateResult) {
            case 1 -> {
                DetailDiscountBUS.getInstance().delete(txtDiscountCode.getText().trim(), 1, ServiceAccessCode.DISCOUNT_DETAILDISCOUNT_SERVICE, 1);
                DetailDiscountBUS.getInstance().createDetailDiscountByDiscountCode(txtDiscountCode.getText().trim(), 1, arrDetailDiscount, ServiceAccessCode.DISCOUNT_DETAILDISCOUNT_SERVICE, 1);

                isSaved = true;
                handleClose();
            }
            case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi sửa khuyến mãi. Vui lòng thử lại.", "Thông báo");
            case 3 ->
                    NotificationUtils.showErrorAlert("Bạn không có quyền \"Sửa khuyến mãi\" để thực hiện thao tác này.", "Thông báo");
            case 4 ->
                    NotificationUtils.showErrorAlert("Dữ liệu đầu vào không hợp lệ.", "Thông báo");
            case 6 -> NotificationUtils.showErrorAlert("Sửa khuyến mãi thất bại. Vui lòng thử lại sau.", "Thông báo");
            default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
        }
    }

    private void insertDiscount() {
        if (arrDetailDiscount.isEmpty()) {
            NotificationUtils.showErrorAlert("Vui lòng thêm ít nhất một chi tiết khuyến mãi.", "Thông báo");
            return;
        }

        DiscountService disService = DiscountService.getInstance();
        if (isValidInput()) {

            DiscountDTO temp = new DiscountDTO(txtDiscountCode.getText().trim(), txtDiscountName.getText().trim(),
                                    cbTypeDiscount.getValue().equals("Phần trăm") ? 0 : 1,
                                    dpStartDate.getValue().atStartOfDay(), dpEndDate.getValue().atStartOfDay());
            ArrayList<DetailDiscountDTO> list = new ArrayList<>();
            for (DetailDiscountDTO dto : arrDetailDiscount) {
                list.add(new DetailDiscountDTO(dto)); // giả sử có constructor sao chép
            }
            int insertResult = disService.createDiscountWithDetailDiscount(temp, SessionManagerService.getInstance().employeeRoleId(), list, SessionManagerService.getInstance().employeeLoginId());
            switch (insertResult) {
                case 1 -> {
                    isSaved = true;
                    handleClose();
                }
                case 2 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm khuyến mãi. Vui lòng thử lại.", "Thông báo");
                case 3 ->
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Thêm khuyến mãi\" để thực hiện thao tác này.", "Thông báo");
                case 4 -> {
                    NotificationUtils.showErrorAlert("Mã khuyến mãi đã tồn tại trong hệ thống.", "Thông báo");
                    clearAndFocus(txtDiscountCode);
                }
                case 5 -> NotificationUtils.showErrorAlert("Thêm khuyến mãi thất bại. Vui lòng thử lại sau.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }

    }

    private boolean isDuplicateDetailDiscount(DetailDiscountDTO obj) {
        for (DetailDiscountDTO dc : arrDetailDiscount) {
            // So sánh mốc hóa đơn đã có với mốc mới, bỏ qua chính nó nếu đang sửa
            if (dc.getTotalPriceInvoice().compareTo(obj.getTotalPriceInvoice()) == 0
                    && !dc.getDiscountCode().equalsIgnoreCase(obj.getDiscountCode())) {
                return true;
            }
        }
        return false;
    }


    private void handleEditBtn() {
        if (isNotSelectedDetailDiscount()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn chi tiết khuyến mãi cần xóa.", "Thông báo");
            return;
        }
        DetailDiscountDTO oldDC = new DetailDiscountDTO(selectedDetailDiscount);
        DetailDiscountModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/DetailDiscountModal.fxml",
                controller -> {
                    controller.setTypeModal(1, cbTypeDiscount.getValue().equals("Phần trăm"));
                    controller.setDetailDiscount(selectedDetailDiscount);
                },
                "Sửa chi tiết khuyến mãi"
        );
        if (modalController != null && modalController.isSaved()) {
            if (isDuplicateDetailDiscount(modalController.getDetailDiscount())) {
                NotificationUtils.showErrorAlert("Mốc tổng tiền hóa đơn tối thiểu này đã tồn tại trong chương trình khuyến mãi.", "Thông báo");
                modalController.getDetailDiscount().setTotalPriceInvoice(oldDC.getTotalPriceInvoice());
                modalController.getDetailDiscount().setDiscountAmount(oldDC.getDiscountAmount());
                return;
            }
            loadTable();
            NotificationUtils.showInfoAlert("Sửa chi tiết khuyến mãi thành công.", "Thông báo");
        }
    }

    private void handleDeleteBtn() {
        if (!isNotSelectedDetailDiscount()) {
            // Nếu đang ở chế độ sửa và chỉ còn một chi tiết khuyến mãi, không cho phép xóa
            arrDetailDiscount.remove(selectedDetailDiscount);
            loadTable();
            NotificationUtils.showInfoAlert("Xóa chi tiết khuyến mãi thành công.", "Thông báo");

            // Nếu không còn chi tiết nào, mở lại comboBox
            if (arrDetailDiscount.isEmpty()) {
                makeEditableForComboBox(cbTypeDiscount);
            }
        } else {
            NotificationUtils.showErrorAlert("Vui lòng chọn chi tiết khuyến mãi cần xóa.", "Thông báo");
        }
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void clearAndFocus(TextField textField) {
        textField.requestFocus();
    }

    private void makeReadOnly(Node node) {
        node.setDisable(false); // Không làm xám hoàn toàn
        node.setMouseTransparent(true); // Không nhận tương tác chuột
        node.setFocusTraversable(false); // Không tab vào được
        node.setStyle("-fx-opacity: 0.75;"); // Làm mờ nhẹ

        // Nếu là ComboBox, chỉnh mũi tên
        if (node instanceof ComboBox) {
            Platform.runLater(() -> {
                Node arrow = ((ComboBox<?>) node).lookup(".arrow-button");
                if (arrow != null) {
                    arrow.setStyle("-fx-background-color: #cccccc; -fx-opacity: 0.75;");
                }
            });
        }
    }


    private void makeEditableForComboBox(ComboBox<?> comboBox) {
        comboBox.setMouseTransparent(false);
        comboBox.setFocusTraversable(true);
        comboBox.setEditable(false); // Giữ nguyên là không gõ tay (nếu muốn)

        comboBox.setStyle(""); // Reset style

        Platform.runLater(() -> {
            Node arrow = comboBox.lookup(".arrow-button");
            if (arrow != null) {
                arrow.setStyle("");
            }
        });
    }

    private boolean isNotSelectedDetailDiscount() {
        selectedDetailDiscount = tblDetailDiscount.getSelectionModel().getSelectedItem();
        return selectedDetailDiscount == null;
    }


}
