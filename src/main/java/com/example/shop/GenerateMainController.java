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

            if ((accountName).equalsIgnoreCase("STAFF"))
                buildMainButtonStaff(rootNodes.getVbMainMenu());
            else if ((accountName).equalsIgnoreCase(("ADMIN")))
                buildMainButtonAdmin(rootNodes.getVbMainMenu());
            else if ((accountName).equalsIgnoreCase("OWNER"))
                buildMainButtonOwner(rootNodes.getVbMainMenu());
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
        mainContainerButtons.add(containerCustomer);
        mainContainerButtons.add(containerType);
        mainContainerButtons.add(containerBill);
        mainContainerButtons.add(containerReceivedNote);
        mainContainerButtons.add(containerSaleEvent);
        for(Pane container : mainContainerButtons)
            mainMenu.getChildren().add(container);
    }

    public void buildMainButtonAdmin(VBox mainMenu) {
        MainPageContainers mainPageContainers = new MainPageContainers();

        Pane containerProduct = mainPageContainers.generateProductButton();
        Pane containerCustomer = mainPageContainers.generateCustomerButton();
        Pane containerType = mainPageContainers.generateTypeButton();
        Pane containerBill = mainPageContainers.generateBillButton();
        Pane containerReceivedNote = mainPageContainers.generateGrnButton();
        Pane containerSaleEvent = mainPageContainers.generateSaleEventButton();

        // Thứ tự hiển thị: Sản phẩm -> Loại -> Khách hàng -> Hóa đơn -> Nhập hàng -> Khuyến mãi
        mainMenu.getChildren().addAll(
                containerProduct,
                containerType,
                containerCustomer,
                containerBill,
                containerReceivedNote,
                containerSaleEvent
        );
    }

    public void buildMainButtonOwner(VBox mainMenu) {
        MainPageContainers mainPageContainers = new MainPageContainers();

        Pane containerProduct = mainPageContainers.generateProductButton();
        Pane containerCustomer = mainPageContainers.generateCustomerButton();
        Pane containerType = mainPageContainers.generateTypeButton();
        Pane containerBill = mainPageContainers.generateBillButton();
        Pane containerReceivedNote = mainPageContainers.generateGrnButton();
        Pane containerSaleEvent = mainPageContainers.generateSaleEventButton();

        // Chức năng thêm cho Owner
        Pane containerStaff = mainPageContainers.generateEmployeeButton();
        Pane containerAccount = mainPageContainers.generateAccountButton();
        Pane containerStatistics = mainPageContainers.generateStatisticsButton();

        // Sắp xếp hợp lý:
        // - Nhóm sản phẩm & khách hàng trước
        // - Nhóm hóa đơn & nhập hàng ở giữa
        // - Nhóm nhân sự & quản lý tài khoản sau
        // - Cuối cùng là thống kê
        mainMenu.getChildren().addAll(
                containerProduct,
                containerType,
                containerCustomer,
                containerBill,
                containerReceivedNote,
                containerSaleEvent,
                containerStaff,
                containerAccount,
                containerStatistics
        );
    }

}
