package GUI;

import BUS.SupplierBUS;
import DTO.SupplierDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;

public class SupForImportModalController {
    @FXML
    private Button btnExitGetSupplier;
    @FXML
    private Button btnSubmitSupplier;
    @FXML
    private Button btnSearchSupplier;
    @FXML
    private Button addBtn;
    @FXML
    private AnchorPane functionBtns;
    @FXML
    private TextField txtSearchSupplier;
    @FXML
    private TableView<SupplierDTO> tblSupplier;
    @FXML
    private TableColumn<SupplierDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_name;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_phone;
    @FXML
    private TableColumn<SupplierDTO, String> tlb_col_address;
    @Getter
    private boolean isSaved;
    @Getter
    private SupplierDTO selectedSupplier;
    @FXML
    public void initialize()
    {
        if  (SupplierBUS.getInstance().isLocalEmpty()) SupplierBUS.getInstance().loadLocal();
        setupListeners();
        hideButtonWithoutPermission();
        loadTable();
    }

    public void loadTable() {
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tlb_col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        UiUtils.gI().addTooltipToColumn(tlb_col_name, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_phone, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_address, 10);

        tblSupplier.setItems(FXCollections.observableArrayList(SupplierBUS.getInstance().filterSuppliers("", "", 1)));
        tblSupplier.getSelectionModel().clearSelection();
    }

    public void setupListeners() {
        btnExitGetSupplier.setOnAction(e -> handleClose());
        btnSubmitSupplier.setOnAction(e -> handleGetSupplier());
        addBtn.setOnAction(e -> handleAddSup());
        txtSearchSupplier.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    private void handleSearch() {
        tblSupplier.setItems(FXCollections.observableArrayList(SupplierBUS.getInstance().searchSupplierByPhone(txtSearchSupplier.getText().trim())));
        tblSupplier.getSelectionModel().clearSelection();
    }

    private void handleGetSupplier() {
        if (isNotSelectedSupplier()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn nhà cung cấp", "Thông báo");
            return;
        }
        isSaved = true;
        handleClose();
    }

    private void handleClose() {
        if (btnExitGetSupplier.getScene() != null && btnExitGetSupplier.getScene().getWindow() != null) {
            Stage stage = (Stage) btnExitGetSupplier.getScene().getWindow();
            stage.close();
        }
    }

    private void handleAddSup() {
        SupplierModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/SupplierModal.fxml",
                controller -> controller.setTypeModal(0),
                "Thêm nhà cung cấp "
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Thêm nhà cung cấp thành công", "Thông báo");
            loadTable();
        }
    }

    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(10);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
    }

    private boolean isNotSelectedSupplier() {
        selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();
        return selectedSupplier == null;
    }
}
