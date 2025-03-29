package com.example.shop;

import BUS.AccountBUS;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class HelloController {
    @Getter
    @FXML
    private Pane pMainContentPane;
    @Getter
    @FXML
    private VBox vbMainMenu;
    @Getter
    @FXML
    private Label lbAccountName ;
    @FXML
    private Pane pLogoutButtonContainer;
    @FXML
    private Button btnProducts;

    @FXML
    protected void onBtnAccountClicked(MouseEvent event) {
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
        double parentHeight = ((AnchorPane) pMainContentPane.getParent()).getPrefHeight();
        (new MainPageContainers()).setMainContent(pMainContentPane, vbMainMenu);
        pLogoutButtonContainer.setLayoutY(parentHeight - 50);
        pLogoutButtonContainer.setLayoutX(0);
    }
}