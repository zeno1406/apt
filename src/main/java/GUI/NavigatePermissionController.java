package GUI;

import SERVICE.SessionManagerService;
import UTILS.UiUtils;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NavigatePermissionController {
    @FXML
    private Pane pItemPermissionSelling, pItemPermissionImporting, pItemPermissionAuth;
    @FXML
    private Button closeBtn;

    // Init
    @FXML
    public void initialize () {
        hideButtonWithoutPermission();
        setupEventHandlers();
    }

    // Validate if this employee has specific perms or not !!!
//    private void CheckPermission() {
//        // lấy tài khoản đã đăng nhập
//        SessionManagerService temp = SessionManagerService.getInstance();
//        ArrayList<ModuleDTO> list = ModuleBUS.getInstance().getAll();
//        System.out.println(temp.getAllowedModules());
//        HashSet<Integer> allowModule = temp.getAllowedModules();
//
//        // có perms bán hàng, nhập kho ko (4 bán hàng, 5 nhập hàng) ?
//        if (allowModule.contains(4))
//            System.out.println(list.get(4).getName());
//        else
//            disableFunc(4);
//        if (allowModule.contains(5))
//            System.out.println(list.get(5).getName());
//        else
//            disableFunc(5);
//
//        //kiểm tra xem còn module nào khác không
//        List<Integer> tempList =  allowModule.stream().filter(module -> module != 4 && module !=5).toList();
//        if (tempList.isEmpty())
//            disableFunc(0);
//    }

    // Exit form
    private void setupEventHandlers() {
        closeBtn.setOnMouseClicked(e -> {
            if (!UiUtils.gI().showConfirmAlert("Bạn chắc muốn đăng xuất?", "Thông báo xác nhận")) return;
            SessionManagerService.getInstance().logout();
            ParallelTransition animation = UiUtils.gI().createButtonAnimation(closeBtn);
            animation.setOnFinished(event -> ExitForm());
            animation.play();
        });
        pItemPermissionSelling.setOnMouseClicked(e -> openSelling());
        pItemPermissionImporting.setOnMouseClicked(e -> openImporting());
        pItemPermissionAuth.setOnMouseClicked(e -> openManage());

    }

    private void ExitForm() {
        UiUtils.gI().openStage("/GUI/LoginUI.fxml", "Đăng nhập");
        Platform.runLater(this::handleClose);
    }

    private void disableFunc(int moduleID) {
        if (moduleID == 4)
            pItemPermissionSelling.setDisable(true);
        if (moduleID == 5)
            pItemPermissionImporting.setDisable(true);
        if (moduleID == 0)
            pItemPermissionAuth.setDisable(true);
    }
    private void openSelling() {
        UiUtils.gI().openStage("/GUI/SellingProduct.fxml", "Bán hàng");
        handleClose();
    }

    @FXML
    private void openImporting() {
        UiUtils.gI().openStage("/GUI/ImportProduct.fxml", "Nhập hàng");
        handleClose();
    }

    @FXML
    private void openManage() {
        UiUtils.gI().openStage("/GUI/MainUI.fxml", "Lego Store");
        handleClose();
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    public void hideButtonWithoutPermission() {
        boolean canSelling = SessionManagerService.getInstance().canSelling();
        boolean canImport = SessionManagerService.getInstance().canImporting();
        boolean canManage = SessionManagerService.getInstance().canManage();

        // Thiết lập trạng thái và độ mờ cho nút quyền bán hàng
        pItemPermissionSelling.setDisable(!canSelling);
        pItemPermissionSelling.setOpacity(canSelling ? 1.0 : 0.3);

        // Thiết lập trạng thái và độ mờ cho nút quyền nhập hàng
        pItemPermissionImporting.setDisable(!canImport);
        pItemPermissionImporting.setOpacity(canImport ? 1.0 : 0.3);

        // Thiết lập trạng thái và độ mờ cho nút quyền quản lý
        pItemPermissionAuth.setDisable(!canManage);
        pItemPermissionAuth.setOpacity(canManage ? 1.0 : 0.3);
    }

}
