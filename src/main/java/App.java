
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
//        System.out.println(PasswordUtils.getInstance().hashPassword("TLANG1234"));
        //        URL resource = App.class.getResource("/images/icon/product.png");
//        System.out.println("Image Resource: " + (resource != null ? resource.toExternalForm() : "Không tìm thấy ảnh!"));

//        EmployeeBUS.getInstance().loadLocal();
//        EmployeeDTO e = EmployeeBUS.getInstance().getByIdLocal(1);
//        SessionManagerService.getInstance().setLoggedInEmployee(e);
//        System.out.println(SessionManagerService.getInstance().getAllowedModules());
        URL resource = App.class.getResource("/images/product/lego-minifigure/mini-01.png");
//        System.out.println("Image Resource: " + (resource != null ? resource.toExternalForm() : "Không tìm thấy ảnh!"));

//        RoleBUS.getInstance().loadLocal();
//        RoleDAL.getInstance().update(new RoleDTO(1, "ADmin", " asd", new BigDecimal(2.3)));
    }
}

