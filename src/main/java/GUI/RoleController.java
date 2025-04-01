package GUI;

import BUS.AccountBUS;
import BUS.RoleBUS;
import BUS.RolePermissionBUS;
import DTO.RoleDTO;
import DTO.RolePermissionDTO;
import INTERFACE.IController;
import SERVICE.RolePermissionService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class RoleController implements IController {
    @FXML
    private TableView<RoleDTO> tblRole;
    @FXML
    private TableColumn<RoleDTO, Integer> tlb_col_id;
    @FXML
    private TableColumn<RoleDTO, String> tlb_col_name;
    @FXML
    private TableColumn<RoleDTO, String> tlb_col_description;
    @FXML
    private TableColumn<RoleDTO, String> tlb_col_salaryCoefficient;
    @FXML
    private HBox functionBtns;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn, authorizeBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbSearchBy;
    private String searchBy = "Mã chức vụ";
    private String keyword = "";
    private RoleDTO selectedRole;
    @FXML
    public void initialize() {
        if (AccountBUS.getInstance().isLocalEmpty()) AccountBUS.getInstance().loadLocal();
        tblRole.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated
        Platform.runLater(() -> tblRole.getSelectionModel().clearSelection());
        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();

        loadTable();
        applyFilters();
    }

    @Override
    public void loadTable() {
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tlb_col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        tlb_col_salaryCoefficient.setCellValueFactory(new PropertyValueFactory<>("salaryCoefficient"));
        tblRole.setItems(FXCollections.observableArrayList(RoleBUS.getInstance().getAllLocal()));
    }

    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã chức vụ", "Tên chức vụ");
        cbSearchBy.getSelectionModel().selectFirst();
    }


    @Override
    public void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });
        authorizeBtn.setOnAction(event -> handleAuthorize());
        deleteBtn.setOnAction(e -> handleDelete());
    }

    private void handleSearchByChange() {
        searchBy = cbSearchBy.getValue();
        applyFilters();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    @Override
    public void applyFilters() {
        tblRole.setItems(FXCollections.observableArrayList(
                RoleBUS.getInstance().filterRoles(searchBy, keyword)
        ));
        tblRole.getSelectionModel().clearSelection();

    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst();
        txtSearch.clear();

        searchBy = "Mã chức vụ";
        keyword = "";
        applyFilters();
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(23);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(25);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(24);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    private void handleAuthorize() {
        if (isNotSelectedRole()) return;
        if (selectedRole.getId() == SessionManagerService.getInstance().employeeRoleId()) {
            NotificationUtils.showErrorAlert("Bạn không thể cập nhật phân quyền của chính mình.", "Thông báo");
            return;
        }
        AuthorizeModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/AuthorizeModal.fxml",
                controller -> controller.setRole(selectedRole),
                "Phân quyền"
        );

        RolePermissionBUS rpBus = RolePermissionBUS.getInstance();
        if (modalController != null && modalController.isSaved()) {
            boolean result = true;
            for (RolePermissionDTO rp : modalController.getAllRolePermissionByRoleId()) {
                int updateResult = rpBus.update(rp, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());

                switch (updateResult) {
                    case 1:
                        // Thành công, tiếp tục cập nhật các phân quyền
                        break;
                    case 2:
                        NotificationUtils.showErrorAlert("Có lỗi khi cập nhật phân quyền. Vui lòng thử lại.", "Thông báo");
                        result = false;
                        break;
                    case 3:
                        NotificationUtils.showErrorAlert("Bạn không thể cập nhật phân quyền của chính mình.", "Thông báo");
                        result = false;
                        break;
                    case 4:
                        NotificationUtils.showErrorAlert("Bạn không thể cập nhật phân quyền của chức vụ ngang quyền.", "Thông báo");
                        result = false;
                        break;
                    case 5:
                        NotificationUtils.showErrorAlert("Bạn không có quyền \"Sửa phân quyền\" để thực hiện thao tác này.", "Thông báo");
                        result = false;
                        break;
                    case 6:
                        NotificationUtils.showErrorAlert("Cập nhật phân quyền thất bại. Vui lòng thử lại sau.", "Thông báo");
                        result = false;
                        break;
                    default:
                        NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
                        result = false;
                }

                if (!result) {
                    break;
                }
            }

            if (result) {
                NotificationUtils.showInfoAlert("Cập nhật phân quyền thành công", "Thông báo");
                resetFilters();
            }
        }
    }

    private void handleDelete() {
        if (isNotSelectedRole()) return;
        if (selectedRole.getId() == SessionManagerService.getInstance().employeeRoleId()) {
            NotificationUtils.showErrorAlert("Bạn không thể xóa chức vụ của chính mình.", "Thông báo");
            return;
        }

        int deleteResult = RolePermissionService.getInstance().deleteRoleWithPermissions(selectedRole.getId(),SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());

        switch (deleteResult) {
            case 1 ->
                // Thành công
                    {
                        NotificationUtils.showInfoAlert("Xóa chức vụ thành công.", "Thông báo");
                        resetFilters();
                    }
            case 2 ->
                // Không hợp lệ, roleId không đúng hoặc không có quyền
                    NotificationUtils.showErrorAlert("Có lỗi khi xóa chức vụ. Vui lòng thử lại.", "Thông báo");
            case 3 ->
                // Không thể tự xóa chức vụ của chính mình
                    NotificationUtils.showErrorAlert("Bạn không thể xóa chức vụ của chính mình.", "Thông báo");
            case 4 ->
                // Người thực hiện không có quyền 24
                    NotificationUtils.showErrorAlert("Bạn không có quyền \"Xóa chức vụ\" để thực hiện thao tác này.", "Thông báo");
            case 5 ->
                // Role có quyền 24, chỉ role 1 mới có thể xóa
                    NotificationUtils.showErrorAlert("Bạn không thể xóa chức vụ ngang quyền", "Thông báo");
            case 6 ->
                // Lỗi khi xóa phân quyền
                    NotificationUtils.showErrorAlert("Xóa chức vụ thất bại. Vui lòng thử lại sau.", "Thông báo");
            case 7 ->
                // Role không hợp lệ
                    NotificationUtils.showErrorAlert("Chức vụ không hợp lệ hoặc đã bị xóa.", "Thông báo");
            default ->
                // Trường hợp không xác định
                    NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
        }

    }

    private void handleAdd() {
//        int addResult = RolePermissionService.getInstance().createRoleWithPermissions(selectedRole.getId(),SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
//
//        switch (updateResult) {
//            case 1 ->
//            // Thành công
//            {
//                NotificationUtils.showInfoAlert("Xóa chức vụ thành công.", "Thông báo");
//                resetFilters();
//            }
//            case 2 ->
//                // Không hợp lệ, roleId không đúng hoặc không có quyền
//                    NotificationUtils.showErrorAlert("Có lỗi khi xóa chức vụ. Vui lòng thử lại.", "Thông báo");
//            case 3 ->
//                // Không thể tự xóa chức vụ của chính mình
//                    NotificationUtils.showErrorAlert("Bạn không thể xóa chức vụ của chính mình.", "Thông báo");
//            case 4 ->
//                // Người thực hiện không có quyền 24
//                    NotificationUtils.showErrorAlert("Bạn không có quyền \"Xóa chức vụ\" để thực hiện thao tác này.", "Thông báo");
//            case 5 ->
//                // Role có quyền 24, chỉ role 1 mới có thể xóa
//                    NotificationUtils.showErrorAlert("Bạn không thể xóa chức vụ ngang quyền", "Thông báo");
//            case 6 ->
//                // Lỗi khi xóa phân quyền
//                    NotificationUtils.showErrorAlert("Xóa chức vụ thất bại. Vui lòng thử lại sau.", "Thông báo");
//            case 7 ->
//                // Role không hợp lệ
//                    NotificationUtils.showErrorAlert("Chức vụ không hợp lệ hoặc đã bị xóa.", "Thông báo");
//            default ->
//                // Trường hợp không xác định
//                    NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
//        }
    }

    private boolean isNotSelectedRole() {
        selectedRole = tblRole.getSelectionModel().getSelectedItem();
        if (selectedRole == null) {
            NotificationUtils.showErrorAlert("Vui lòng chọn chức vụ", "Thông báo");
            return true;
        }
        return false;
    }
}
