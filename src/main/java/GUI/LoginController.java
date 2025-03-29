package GUI;

import BUS.EmployeeBUS;
import DTO.AccountDTO;
import SERVICE.LoginService;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

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

    private double x = 0;
    private double y = 0;

    @FXML
    public void initialize() {
        txtUsername.setText("huyhoang119763");
        txtPassword.setText("huyhoang123");
        closeBtn.setOnMouseClicked(e -> close());
        loginBtn.setOnMouseClicked(e -> handleLogin());
        txtUsername.setOnAction(e -> handleLogin());
        txtPassword.setOnAction(e -> handleLogin());
    }

    public void close() {
        System.exit(0);
    }

    public void handleLogin() {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            NotificationUtils.showErrorAlert("Vui lòng điền tài khoản và mật khẩu", "Thông báo");
        } else {
            AccountDTO account = new AccountDTO(0, txtUsername.getText(), txtPassword.getText());
            int employeeLoginId = LoginService.getInstance().checkLogin(account);
            if(employeeLoginId != -1) {
                NotificationUtils.showInfoAlert("Đăng nhập thành công!", "Thông báo");
                if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();
                SessionManagerService.getInstance().setLoggedInEmployee(EmployeeBUS.getInstance().getByIdLocal(employeeLoginId));
                // Tắt giao diện login
                loginBtn.getScene().getWindow().hide();
                openStage("/GUI/MainUI.fxml");
            } else {
                NotificationUtils.showErrorAlert("Đăng nhập thất bại!", "Thông báo");
            }
        }
    }

    public void openStage(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load(); // Gọi .load() để lấy root từ FXML


            Stage stage = new Stage();
            Scene scene = new Scene(root);

            root.setOnMousePressed((MouseEvent e) -> {
                x = e.getSceneX();
                y = e.getSceneY();
            });

            root.setOnMouseDragged((MouseEvent e) -> {
                if (e.getScreenX() != x || e.getScreenY() != y) {
                    stage.setX(e.getScreenX() - x);
                    stage.setY(e.getScreenY() - y);
                    stage.setOpacity(.8);
                }
            });

            root.setOnMouseReleased((MouseEvent e) -> {
                stage.setOpacity(1);
            });
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle("Lego Store");
            stage.setScene(scene);

            stage.show();
            stage.requestFocus();

        } catch (IOException e) {
            log.error("error", e);
        }
    }

}
