package GUI;

import BUS.ModuleBUS;
import BUS.PermissionBUS;
import BUS.RolePermissionBUS;
import DTO.*;
import INTERFACE.IModal;
import UTILS.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

import java.net.URL;
import java.util.ArrayList;

public class AuthorizeModalController implements IModal {
    @Getter
    private boolean isSaved;
    @FXML
    private GridPane permissionGridPane;
    @FXML
    private Button saveBtn,closeBtn;
    @FXML
    private Label roleName;
    private RoleDTO role;
    @Getter
    private ArrayList<RolePermissionDTO> allRolePermissionByRoleId;

    @FXML
    public void initialize() {
        loadCss();
        loadRoleData();
        setupListeners();
    }

    @Override
    public void loadCss() {
        URL cssUrl = getClass().getResource("/css/base.css");
        if (cssUrl != null) {
            permissionGridPane.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("⚠ Lỗi: Không tìm thấy file CSS '/css/base.css'");
        }
    }

    private void loadRoleData() {
        if (role != null) {
            roleName.setText(role.getName());
        }
    }

    @Override
    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    public void setRole(RoleDTO role) {
        this.role = role;
//        System.out.println("Đã nhận role: " + role.getName());
        loadRolePermissions();
        addCheckboxesToGrid();
    }

    private void loadRolePermissions() {
        if (role == null) return;
        roleName.setText(role.getName());
        RolePermissionBUS rolePermissionBus = RolePermissionBUS.getInstance();
        if (rolePermissionBus.isLocalEmpty()) rolePermissionBus.loadLocal();

        // Lấy danh sách quyền của role (bao gồm cả quyền có status = 0 và status = 1)
        allRolePermissionByRoleId = rolePermissionBus.getAllRolePermissionByRoleIdLocal(role.getId());
    }

    private void addCheckboxesToGrid() {
        ModuleBUS moduleBus = ModuleBUS.getInstance();
        PermissionBUS permissionBus = PermissionBUS.getInstance();

        if (moduleBus.getAllLocal().isEmpty()) moduleBus.loadLocal();
        if (permissionBus.getAllLocal().isEmpty()) permissionBus.loadLocal();

        ArrayList<ModuleDTO> arrModule = moduleBus.getAllLocal();
        ArrayList<PermissionDTO> arrPermission = permissionBus.getAllLocal();

        permissionGridPane.getChildren().clear(); // Xóa nội dung cũ để load lại

        int rowIndex = 0;
        int colIndex = 0;
        // Duyệt qua các module
        for (ModuleDTO module : arrModule) {
            VBox moduleBox = new VBox();
            moduleBox.setSpacing(5.0);

            // Tạo checkbox đại diện cho module
            CheckBox checkModuleBox = new CheckBox(module.getName());
            checkModuleBox.getStyleClass().add("check-box-s");

            // Kiểm tra xem tất cả các permission của module có được chọn không
            boolean allPermissionsActive = true;
            VBox permissionsBox = new VBox();
            permissionsBox.setSpacing(5.0);
            permissionsBox.setPadding(new Insets(0, 0, 0, 15));

            // Duyệt qua các quyền con của module
            for (PermissionDTO permission : arrPermission) {
                if (permission.getModule_id() == module.getId()) {
                    CheckBox checkBox = new CheckBox(permission.getName());
                    checkBox.setUserData(permission.getId());
                    // Duyệt qua danh sách rolePermissions để kiểm tra xem quyền này có trong phân quyền của role không

                    for (RolePermissionDTO rp : allRolePermissionByRoleId) {

                        if (rp.getPermissionId() == permission.getId()) {
                            // Nếu status = 1 thì đánh dấu checkbox là chọn
                            checkBox.setSelected(rp.isStatus());

                            // Nếu quyền có status = 0, đánh dấu moduleBtn là false
                            if (!rp.isStatus()) {
                                allPermissionsActive = false;
                            }
                            break;
                        }
                    }

                    checkBox.getStyleClass().add("check-box-m");
                    checkBox.setOnAction(e -> {
                        boolean isSelected = checkBox.isSelected();

                        // Cập nhật trạng thái cho permission tương ứng
                        int permissionId = (int) checkBox.getUserData();
                        for (RolePermissionDTO rp : allRolePermissionByRoleId) {
                            if (rp.getPermissionId() == permissionId) {
                                rp.setStatus(isSelected);
                                break;
                            }
                        }

                        // Cập nhật trạng thái cho module checkbox
                        updateModuleCheckboxState(moduleBox, permissionsBox);
                    });
                    permissionsBox.getChildren().add(checkBox);
                }
            }

            // Nếu tất cả quyền con của module đều được chọn, đánh dấu moduleBtn là true
            checkModuleBox.setSelected(allPermissionsActive);

            // Thêm sự kiện thay đổi trạng thái của module checkbox
            checkModuleBox.setOnAction(e -> {
                // Nếu module checkbox được tích, tất cả các permission checkbox con sẽ được tích
                boolean isSelected = checkModuleBox.isSelected();
                for (int i = 0; i < permissionsBox.getChildren().size(); i++) {
                    CheckBox permissionCheckBox = (CheckBox) permissionsBox.getChildren().get(i);
                    permissionCheckBox.setSelected(isSelected);

                    // Cập nhật trạng thái cho mỗi permission
                    int permissionId = (int) permissionCheckBox.getUserData();
                    for (RolePermissionDTO rp : allRolePermissionByRoleId) {
                        if (rp.getPermissionId() == permissionId) {
                            rp.setStatus(isSelected); // Cập nhật trạng thái permission
                            break;
                        }
                    }
                }
            });

            moduleBox.getChildren().add(checkModuleBox);
            moduleBox.getChildren().add(permissionsBox);
            permissionGridPane.add(moduleBox, colIndex++, rowIndex);
            if (colIndex == 4) {
                rowIndex++;
                colIndex = 0;
            }
        }
    }


    private void updateModuleCheckboxState(VBox moduleBox, VBox permissionsBox) {
        CheckBox checkModuleBox = (CheckBox) moduleBox.getChildren().get(0);

        boolean allSelected = true;
        for (int i = 0; i < permissionsBox.getChildren().size(); i++) {
            CheckBox permissionCheckBox = (CheckBox) permissionsBox.getChildren().get(i);
            if (!permissionCheckBox.isSelected()) {
                allSelected = false;
                break;
            }
        }

        checkModuleBox.setSelected(allSelected);
    }

    private void handleSave() {
        isSaved = UiUtils.gI().showConfirmAlert("Bạn chắc chắn lưu phân quyền này?", "Thông báo xác nhận");
        if (isSaved) {
            handleClose();
        }
    }

    private void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
