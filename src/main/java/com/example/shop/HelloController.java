package com.example.shop;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
    private AnchorPane acTopMainMenu;
    @FXML
    private Pane pFormLoginRegis;
    @FXML
    private Button btnExitAccountForm;

    @FXML
    protected void onBtnAccountClicked(MouseEvent event) {
        txtSearchProduct.setText("Welcome to JavaFX Application!");
        pFormLoginRegis.setVisible(true);

    }

    @FXML
    protected void onBtnExitAccountFormClicked(MouseEvent event) {
        pFormLoginRegis.setVisible(false);
        txtSearchProduct.setText("Hide");
    }

    public void initialize() {
        double parentWidth = ((AnchorPane) pMainContentPane.getParent()).getPrefWidth();
        double parentHeight = ((AnchorPane) pMainContentPane.getParent()).getPrefHeight();
        double vBoxMainMenuWidth = vbMainMenu.getPrefWidth();
        double acTopMainMenuHeight = acTopMainMenu.getPrefHeight();
        pMainContentPane.setPrefWidth(parentWidth - vBoxMainMenuWidth);
        pMainContentPane.setPrefHeight(parentHeight - acTopMainMenuHeight);
        txtSearchProduct.setText(String.valueOf(parentWidth));

    }
}