package GUI;

import BUS.EmployeeBUS;
import DTO.AccountDTO;
import SERVICE.LoginService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import java.util.prefs.Preferences;


import java.io.IOException;

@Slf4j
public class LoginController {
    @FXML
    private Button closeBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private AnchorPane main_form;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUsername;
    @FXML
    private CheckBox ckbRememberMe;
    private Preferences prefs;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(LoginController.class);

        // Kiểm tra xem rememberMe có được bật không
        boolean rememberMe = prefs.getBoolean("rememberMe", false);
        ckbRememberMe.setSelected(rememberMe);

        if (rememberMe) {
            txtUsername.setText(prefs.get("savedUsername", ""));
            txtPassword.setText(prefs.get("savedPassword", ""));
        }

        closeBtn.setOnMouseClicked(e -> close());
        loginBtn.setOnMouseClicked(e -> handleLogin());
        txtUsername.setOnAction(e -> handleLogin());
        txtPassword.setOnAction(e -> handleLogin());
        loginBtn.setDefaultButton(true);
    }

    public void close() {
        System.exit(0);
    }

    public void handleLogin() {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            NotificationUtils.showErrorAlert("Vui lòng điền tài khoản và mật khẩu", "Thông báo");
        } else {
            AccountDTO account = new AccountDTO(0, txtUsername.getText(), txtPassword.getText());
            if(LoginService.getInstance().checkLogin(account)) {
                NotificationUtils.showInfoAlert("Đăng nhập thành công!", "Thông báo");

                // Lưu hoặc xóa thông tin tài khoản
                prefs.putBoolean("rememberMe", ckbRememberMe.isSelected()); // Lưu trạng thái checkbox
                if (ckbRememberMe.isSelected()) {
                    prefs.put("savedUsername", txtUsername.getText());
                    prefs.put("savedPassword", txtPassword.getText());
                } else {
                    prefs.remove("savedUsername");
                    prefs.remove("savedPassword");
                }

                // Tắt giao diện login (mở giao diện chọn chức năng)
                loginBtn.getScene().getWindow().hide();
                UiUtils.gI().openStage("/GUI/NavigatePermission.fxml", "Danh sách chức năng");
//                openStage("/GUI/NavigatePermission.fxml");
            } else {
                NotificationUtils.showErrorAlert("Đăng nhập thất bại!", "Thông báo");
            }
        }
    }

//    public void openStage(String fxmlFile) {
//        try {
//            System.out.println(fxmlFile);
//            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource(fxmlFile));
//            Parent root = fxmlLoader.load(); // Gọi .load() để lấy root từ FXML
//
//            Stage stage = new Stage();
//            Scene scene = new Scene(root);
//
//            UiUtils.gI().makeWindowDraggable(root, stage);
//            stage.initStyle(StageStyle.TRANSPARENT);
//
//            stage.setTitle("Lego Store");
//            stage.setScene(scene);
//
//            stage.show();
//            stage.requestFocus();
//
//        } catch (IOException e) {
//            log.error("error", e);
//        }
//    }

}
