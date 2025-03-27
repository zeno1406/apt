package com.example.shop;

import BUS.AccountBUS;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Button btnAccount;
    @FXML
    private TextField txtSearchProduct;
    @FXML
    private Pane pMainContentPane;
    @FXML
    private VBox vbMainMenu;
    @FXML
    private AnchorPane apTopMainMenu;

    @FXML
    protected void onBtnAccountClicked(MouseEvent event) {
        txtSearchProduct.setText("Welcome to JavaFX Application!");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        double parentWidth = ((AnchorPane) pMainContentPane.getParent()).getPrefWidth();
        double parentHeight = ((AnchorPane) pMainContentPane.getParent()).getPrefHeight();
        double vBoxMainMenuWidth = vbMainMenu.getPrefWidth();
        double acTopMainMenuHeight = apTopMainMenu.getPrefHeight();
        pMainContentPane.setPrefWidth(parentWidth - vBoxMainMenuWidth);
        pMainContentPane.setPrefHeight(parentHeight - acTopMainMenuHeight);
        txtSearchProduct.setText(String.valueOf(parentWidth));

    }
}