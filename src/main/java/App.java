
import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DAL.RoleDAL;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import SERVICE.SessionManagerService;
import UTILS.PasswordUtils;
import UTILS.UiUtils;
import BUS.AccountBUS;
import DTO.AccountDTO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("GUI/LoginUI.fxml"));
        Parent root = fxmlLoader.load(); // Gọi .load() để lấy root từ FXML

        UiUtils.gI().makeWindowDraggable(root, stage);

        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Đăng nhập");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

