package GUI;

import INTERFACE.IModal;
import UTILS.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RoleModalController implements IModal {
    @FXML
    private Label modalName;
    @FXML
    private TextField txtRoleName;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtSalaryCoefficient;
    @FXML
    private Button saveBtn,closeBtn;

    @FXML
    public void initialize() {
        loadCss();
//        loadRoleData();
        setupListeners();
    }


    @Override
    public void loadCss() {

    }

    @Override
    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void handleSave() {
//        isSaved = UiUtils.gI().showConfirmAlert("Bạn chắc chắn lưu phân quyền này?", "Thông báo xác nhận");
//        if (isSaved) {
//            handleClose();
//        }
    }

    private void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
