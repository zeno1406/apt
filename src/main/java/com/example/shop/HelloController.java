package com.example.shop;

import BUS.AccountBUS;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private TextField txtSearchProduct;
    @FXML
    private Pane pMainContentPane;
    @FXML
    private VBox vbMainMenu;
    @FXML

    protected void onBtnAccountClicked(MouseEvent event) {
        txtSearchProduct.setText("Welcome to JavaFX Application!");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            (new ISetPositionScreen()).setPositionScreen(stage, scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        double parentWidth = ((AnchorPane) pMainContentPane.getParent()).getPrefWidth();
        double parentHeight = ((AnchorPane) pMainContentPane.getParent()).getPrefHeight();
        double vBoxMainMenuWidth = vbMainMenu.getPrefWidth();
        pMainContentPane.setLayoutY(0);
        pMainContentPane.setPrefHeight(parentHeight);
        pMainContentPane.setPrefWidth(parentWidth - vBoxMainMenuWidth);
        txtSearchProduct.setText(String.valueOf(parentWidth));

    }
}