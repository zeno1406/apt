package GUI;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;

@Slf4j
public class MainController {
    @FXML
    private Button closeBtn;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Button logoutBtn;

    private Stage stage;

    @FXML
    private Pane mainContent;

    @FXML
    public void initialize() {


        loadFXML("/GUI/EmployeeUI.fxml");
        logoutBtn.setOnMouseClicked(e -> logout());
        closeBtn.setOnMouseClicked(e -> close());
        minimizeBtn.setOnMouseClicked(e -> minimize(e));
    }

    public void minimize(MouseEvent event) {
        // Lấy Stage từ bất kỳ node nào (ở đây là minimizeBtn)
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true); // Thu nhỏ cửa sổ
    }

    public void close() {
        System.exit(0);
    }

    public void logout() {
        // Lấy cửa sổ hiện tại (MainUI)
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close(); // Đóng MainUI

        // Mở lại giao diện đăng nhập
        openLoginStage("/GUI/LoginUI.fxml");
    }

    public void openLoginStage(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("Đăng nhập");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFXML(String fxmlFile) {
        try {
            // Create a new FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            // Set the URL for the FXMLLoader to point to the new FXML file
            loader.setLocation(getClass().getResource(fxmlFile));
            // Load the FXML content
            Pane newContent = loader.load();
            // Clear the main pane and add the new content
            mainContent.getChildren().clear();
            mainContent.getChildren().add(newContent);
        } catch (IOException e) {
            log.error("error", e);
        }catch (ClassCastException e) {
            System.err.println("ClassCastException: " + e.getMessage());
        }
    }

}
