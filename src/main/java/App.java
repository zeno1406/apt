import BUS.EmployeeBUS;
import BUS.RolePermissionBUS;
import DTO.AccountDTO;
import DTO.EmployeeDTO;
import DTO.RolePermissionDTO;
import SERVICE.RolePermissionService;
import SERVICE.SessionManagerService;
import UTILS.PasswordUtils;
import UTILS.UiUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

//        URL resource = App.class.getResource("/images/product/lego-minifigure/mini-01.png");
//        System.out.println("Image Resource: " + (resource != null ? resource.toExternalForm() : "Không tìm thấy ảnh!"));

//        EmployeeBUS.getInstance().loadLocal();
//        EmployeeDTO e = EmployeeBUS.getInstance().getByIdLocal(1);
//        SessionManagerService.getInstance().setLoggedInEmployee(e);
//        System.out.println(SessionManagerService.getInstance().getAllowedModules());

//        AccountDTO a = new AccountDTO(1, "huyhoang119763", "huyhoang123");
//        System.out.println(PasswordUtils.getInstance().hashPassword("huyhoang123"));

//        RolePermissionService.getInstance().printPermissionsGroupedByModule();
//        RolePermissionBUS.getInstance().loadLocal();
//        RolePermissionBUS.getInstance().update(new RolePermissionDTO(2, 1, true), 1, 1);
    }
}
