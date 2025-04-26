package GUI;

import BUS.*;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import SERVICE.SessionManagerService;
import UTILS.UiUtils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.jboss.jandex.Main;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

@Slf4j
public class MainController {
    @FXML
    private Button closeBtn, minimizeBtn, logoutBtn;
    @FXML
    private Pane mainContent;
    @FXML
    private Label employeeLoginFullName, employeeRoleName;
    @FXML
    private VBox groupBtn;
    static boolean isLoaded = false;
    private Button selectedButton = null;
    private static final MainController INSTANCE = new MainController();

    public MainController() {}
    public static MainController getInstance() {
        return INSTANCE;
    }

    @FXML
    public void initialize() {
        loadSessionData();
        setupEventHandlers();
        loadAllLocalData();
        loadAllowedModules();
    }

    public void loadSessionData() {
        EmployeeDTO currEmployee = SessionManagerService.getInstance().currEmployee();
        employeeLoginFullName.setText(currEmployee.getFirstName() + " " + currEmployee.getLastName());
        employeeRoleName.setText(RoleBUS.getInstance().getByIdLocal(currEmployee.getRoleId()).getName());
    }

    public void setupEventHandlers() {
        logoutBtn.setOnMouseClicked(e -> {
            if (!UiUtils.gI().showConfirmAlert("Bạn chắc muốn thoát?", "Thông báo xác nhận")) return;
//            SessionManagerService.getInstance().logout();
            ParallelTransition animation = UiUtils.gI().createButtonAnimation(logoutBtn);
            animation.setOnFinished(event -> logout());
            animation.play();
        });

        closeBtn.setOnMouseClicked(e -> close());
        minimizeBtn.setOnMouseClicked(this::minimize);
    }

    public void loadAllLocalData() {
        if (!isLoaded) {
            EmployeeBUS.getInstance().loadLocal();
            RoleBUS.getInstance().loadLocal();
            RolePermissionBUS.getInstance().loadLocal();
            ModuleBUS.getInstance().loadLocal();
            PermissionBUS.getInstance().loadLocal();
            isLoaded = true;
        }
    }

    public void minimize(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    public void close() {
        System.exit(0);
    }

    public void logout() {
        UiUtils.gI().openStage("/GUI/NavigatePermission.fxml", "Danh sách chức năng");
        handleClose();
    }

    public void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane newContent = loader.load();
            mainContent.getChildren().setAll(newContent);
        } catch (IOException e) {
            log.error("Lỗi tải FXML: " + fxmlFile, e);
        }
    }

    private void loadAllowedModules() {
        List<Integer> orderedModules = Arrays.asList(3, 1, 2, 4, 7, 8, 5, 6, 9, 10, 11);

        Map<Integer, String> moduleMap = new HashMap<>();
        moduleMap.put(1, "Nhân viên");
        moduleMap.put(2, "Khách hàng");
        moduleMap.put(3, "Sản phẩm");
        moduleMap.put(4, "Nhà cung cấp");
        moduleMap.put(5, "Hóa đơn");
        moduleMap.put(6, "Phiếu nhập");
        moduleMap.put(7, "Thể loại");
        moduleMap.put(8, "Khuyến mãi");
        moduleMap.put(9, "Chức vụ");
        moduleMap.put(10, "Tài khoản");
        moduleMap.put(11, "Thống kê");

        Map<String, String> moduleIcons = new HashMap<>();
        moduleIcons.put("Nhân viên", "employee.png");
        moduleIcons.put("Khách hàng", "customer.png");
        moduleIcons.put("Sản phẩm", "product.png");
        moduleIcons.put("Nhà cung cấp", "supplier.png");
        moduleIcons.put("Hóa đơn", "invoice.png");
        moduleIcons.put("Phiếu nhập", "import.png");
        moduleIcons.put("Thể loại", "category.png");
        moduleIcons.put("Khuyến mãi", "discount.png");
        moduleIcons.put("Chức vụ", "role.png");
        moduleIcons.put("Tài khoản", "account.png");
        moduleIcons.put("Thống kê", "statistical.png");

        groupBtn.getChildren().clear();
        List<Button> buttons = new ArrayList<>();

        // Thêm các module bình thường
        for (Integer moduleId : orderedModules) {
            if (SessionManagerService.getInstance().hasModuleAccess(moduleId)) {
                String moduleName = moduleMap.get(moduleId);
                buttons.add(createModuleButton(moduleName, moduleIcons.getOrDefault(moduleName, "default.png"),
                        () -> handleModuleClick(moduleId, moduleName)));
            }
        }

        groupBtn.getChildren().addAll(buttons);

        if (!buttons.isEmpty()) {
            buttons.get(0).fire();
        }
    }

    private Button createModuleButton(String text, String iconPath, Runnable action) {
        Button btn = new Button("   " + text);
        btn.setPrefSize(174, 34);
        btn.getStyleClass().add("nav-btn");

        // Kiểm tra CSS trước khi thêm
        URL cssUrl = getClass().getResource("/css/main.css");
        if (cssUrl != null) {
            btn.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("⚠ Lỗi: Không tìm thấy file CSS '/css/main.css'");
        }

        // Kiểm tra và tải icon
        String imagePath = "/images/icon/" + iconPath;
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image;

        if (imageStream != null) {
            image = new Image(imageStream);
        } else {
            System.err.println("⚠ Lỗi: Không tìm thấy ảnh " + imagePath + ", thử dùng ảnh mặc định.");
            InputStream defaultStream = getClass().getResourceAsStream("/images/icon/default.png");
            if (defaultStream != null) {
                image = new Image(defaultStream);
            } else {
                System.err.println("⚠ Lỗi nghiêm trọng: Không tìm thấy ảnh mặc định!");
                image = new Image("https://via.placeholder.com/25"); // Sử dụng ảnh tạm thời từ Internet
            }
        }

        ImageView icon = new ImageView(image);
        icon.setFitWidth(25);
        icon.setFitHeight(25);
        btn.setGraphic(icon);

        btn.setOnAction(event -> {
            UiUtils.gI().applyButtonAnimation(btn);
            action.run();
        });

        return btn;
    }

    private void handleModuleClick(int moduleId, String moduleName) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("nav-btn-active");
        }

        for (Node node : groupBtn.getChildren()) {
            if (node instanceof Button btn && btn.getText().trim().equals(moduleName.trim())) {
                btn.getStyleClass().add("nav-btn-active");
                selectedButton = btn;
                break;
            }
        }

        switch (moduleId) {
            case 1 -> loadFXML("/GUI/EmployeeUI.fxml");
            case 2 -> loadFXML("/GUI/CustomerUI.fxml");
            case 3 -> loadFXML("/GUI/ProductUI.fxml");
            case 4 -> loadFXML("/GUI/SupplierUI.fxml");
            case 5 -> loadFXML("/GUI/InvoiceUI.fxml");
            case 6 -> loadFXML("/GUI/ImportUI.fxml");
            case 7 -> loadFXML("/GUI/CategoryUI.fxml");
            case 8 -> loadFXML("/GUI/DiscountUI.fxml");
            case 9 -> loadFXML("/GUI/RoleUI.fxml");
            case 10 -> loadFXML("/GUI/AccountUI.fxml");
            case 11 -> loadFXML("/GUI/StatisticUI.fxml");
        }
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }

    // Navigate to others stage
//    public void openStage(String fxmlFile) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource(fxmlFile));
//            Parent root = fxmlLoader.load(); // Gọi .load() để lấy root từ FXML
//
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

