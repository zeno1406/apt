package GUI;

import BUS.ModuleBUS;
import DTO.ModuleDTO;
import SERVICE.SessionManagerService;
import UTILS.UiUtils;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.common.util.impl.Log;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NavigatePermissionController {
    @FXML
    private Pane pItemPermissionSelling, pItemPermissionImporting, pItemPermissionAuth;
    @FXML
    private Button btnExitNavPermissionForm;
    private boolean isDisable;

    // Init
    @FXML
    public void initialize () {
        isDisable = false;
        CheckPermission();
        setupEventHandlers();
    }

    // Validate if this employee has specific perms or not !!!
    private void CheckPermission() {
        // lấy tài khoản đã đăng nhập
        SessionManagerService temp = SessionManagerService.getInstance();
        ArrayList<ModuleDTO> list = ModuleBUS.getInstance().getAll();
        System.out.println(temp.getAllowedModules());
        HashSet<Integer> allowModule = temp.getAllowedModules();

        // có perms bán hàng, nhập kho ko (4 bán hàng, 5 nhập hàng) ?
        if (allowModule.contains(4))
            System.out.println(list.get(4).getName());
        else
            disableFunc(4);
        if (allowModule.contains(5))
            System.out.println(list.get(5).getName());
        else
            disableFunc(5);

        //kiểm tra xem còn module nào khác không
        List<Integer> tempList =  allowModule.stream().filter(module -> module != 4 && module !=5).toList();
        if (tempList.isEmpty())
            disableFunc(0);
    }

    // Exit form
    private void setupEventHandlers() {
        btnExitNavPermissionForm.setOnMouseClicked(e -> {
            ParallelTransition animation = UiUtils.gI().createButtonAnimation(btnExitNavPermissionForm);
            animation.setOnFinished(event -> ExitForm());
            animation.play();
        });
    }

    private void ExitForm() {
        (MainController.getInstance()).openStage("/GUI/LoginUI.fxml");
        Platform.runLater(() -> ((Stage) btnExitNavPermissionForm.getScene().getWindow()).close());
    }

    // check and disable func
    private boolean isDisableFunc() {
        return false;
    }

    private void disableFunc(int moduleID) {
        if (moduleID == 4)
            pItemPermissionSelling.setDisable(true);
        if (moduleID == 5)
            pItemPermissionImporting.setDisable(true);
        if (moduleID == 0)
            pItemPermissionAuth.setDisable(true);
    }

    // Set click Event
    @FXML
    private void onMouseClickedPItemSelling(MouseEvent e) {
        (MainController.getInstance()).openStage("/GUI/Selling.fxml");
        btnExitNavPermissionForm.getScene().getWindow().hide();
    }

    @FXML
    private void onMouseClickedPItemImporting(MouseEvent e) {
        (MainController.getInstance()).openStage("/GUI/ImportProduct.fxml");
        btnExitNavPermissionForm.getScene().getWindow().hide();
    }

    @FXML
    private void onMouseClickedPItemPermissionAuth(MouseEvent e) {
        (MainController.getInstance()).openStage("/GUI/MainUI.fxml");
        btnExitNavPermissionForm.getScene().getWindow().hide();
    }

}
