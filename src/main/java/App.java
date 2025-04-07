
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
//        System.out.println(PasswordUtils.getInstance().hashPassword("TLANG1234"));
        String imagePath = "images/product/sp00001.png";  // Đảm bảo ảnh nằm ngoài thư mục resources

        // Tạo đường dẫn file từ thư mục gốc dự án
        File file = new File(imagePath);

        // Kiểm tra xem ảnh có tồn tại hay không
        if (file.exists()) {
            System.out.println("Ảnh tồn tại tại đường dẫn: " + file.getAbsolutePath());
        } else {
            System.out.println("Không tìm thấy ảnh tại đường dẫn: " + file.getAbsolutePath());
        }

        // Kiểm tra tài nguyên bên trong resources (chỉ áp dụng với tài nguyên trong thư mục resources)
        URL resource = App.class.getResource("/" + imagePath);  // Đảm bảo sử dụng dấu '/' trước đường dẫn

        if (resource != null) {
            System.out.println("Tài nguyên trong resources: " + resource.toExternalForm());
        } else {
            System.out.println("Không tìm thấy tài nguyên trong resources.");
        }
    }
}

