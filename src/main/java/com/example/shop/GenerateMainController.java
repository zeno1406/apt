package com.example.shop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GenerateMainController {
    //    call for rebuild main page
    public void generateMainPage(Stage stage, String resource, String accountName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            (new ISetPositionScreen()).setPositionScreen(stage, scene);
            stage.show();
            HelloController rootNodes =  fxmlLoader.getController();
            changeMainLabelContent(rootNodes.getLbAccountName(), accountName);
            buildMainButtonStaff(rootNodes.getVbMainMenu());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //    change label below main icons
    public void changeMainLabelContent(Label label, String accountName) {
        label.setVisible(true);
        label.setText(accountName.toUpperCase());
    }

//  build main buttons
    public void buildMainButtonStaff(VBox mainMenu) {
        MainPageContainers mainPageContainers = new MainPageContainers();
        Pane containerProduct = mainPageContainers.generateProductButton();
        Pane containerBill = mainPageContainers.generateBillButton();
        Pane containerReceivedNote = mainPageContainers.generateGrnButton();
        Pane containerSaleEvent = mainPageContainers.generateSaleEventButton();
        Pane containerType = mainPageContainers.generateTypeButton();
        Pane containerCustomer = mainPageContainers.generateCustomerButton();
        ArrayList<Pane> mainContainerButtons = new ArrayList<Pane>();
        mainContainerButtons.add(containerProduct);
        mainContainerButtons.add(containerType);
        mainContainerButtons.add(containerBill);
        mainContainerButtons.add(containerCustomer);
        mainContainerButtons.add(containerReceivedNote);
        mainContainerButtons.add(containerSaleEvent);
        for(Pane container : mainContainerButtons)
            mainMenu.getChildren().add(container);
    }
}
