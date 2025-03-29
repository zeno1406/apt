package com.example.shop;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class MainPageContainers {
    public Pane generateProductButton() {
        Pane pProductButtonContainer = new Pane();
        Button btnProduct = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-product-24.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pProductButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnProduct.getStyleClass().add("btnMainMenuChild");
        btnProduct.setText("Product");
        btnProduct.setGraphic(icon);
        pProductButtonContainer.getChildren().add(btnProduct);
        return pProductButtonContainer;
    }

    public Pane generateCustomerButton() {
        Pane pCustomerButtonContainer = new Pane();
        Button btnCustomer = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-customer-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pCustomerButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnCustomer.getStyleClass().add("btnMainMenuChild");
        btnCustomer.setText("Customer");
        btnCustomer.setGraphic(icon);
        pCustomerButtonContainer.getChildren().add(btnCustomer);
        return pCustomerButtonContainer;
    }

    public Pane generateSupplierButton() {
        Pane pSupplierButtonContainer = new Pane();
        Button btnSupplier = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-supplier-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pSupplierButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnSupplier.getStyleClass().add("btnMainMenuChild");
        btnSupplier.setText("Supplier");
        btnSupplier.setGraphic(icon);
        pSupplierButtonContainer.getChildren().add(btnSupplier);
        return pSupplierButtonContainer;
    }

    public Pane generateTypeButton() {
        Pane pTypeButtonContainer = new Pane();
        Button btnType = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-type-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pTypeButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnType.getStyleClass().add("btnMainMenuChild");
        btnType.setText("Type");
        btnType.setGraphic(icon);
        pTypeButtonContainer.getChildren().add(btnType);
        return pTypeButtonContainer;
    }

    public Pane generateSaleEventButton() {
        Pane pSaleEventButtonContainer = new Pane();
        Button btnSaleEvent = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-sale-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pSaleEventButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnSaleEvent.getStyleClass().add("btnMainMenuChild");
        btnSaleEvent.setText("Sale Event");
        btnSaleEvent.setGraphic(icon);
        pSaleEventButtonContainer.getChildren().add(btnSaleEvent);
        return pSaleEventButtonContainer;
    }

    public Pane generateGrnButton() {
        Pane pGrnButtonContainer = new Pane();
        Button btnGrn = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-received-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pGrnButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnGrn.getStyleClass().add("btnMainMenuChild");
        btnGrn.setText("GRN");
        btnGrn.setGraphic(icon);
        pGrnButtonContainer.getChildren().add(btnGrn);
        return pGrnButtonContainer;
    }

    public Pane generateBillButton() {
        Pane pBillButtonContainer = new Pane();
        Button btnBill = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-bill-24.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pBillButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnBill.getStyleClass().add("btnMainMenuChild");
        btnBill.setText("Bill");
        btnBill.setGraphic(icon);
        pBillButtonContainer.getChildren().add(btnBill);
        return pBillButtonContainer;
    }

    public Pane generateRoleButton() {
        Pane pRoleButtonContainer = new Pane();
        Button btnRole = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-critical-role-32.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pRoleButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnRole.getStyleClass().add("btnMainMenuChild");
        btnRole.setText("Role");
        btnRole.setGraphic(icon);
        pRoleButtonContainer.getChildren().add(btnRole);
        return pRoleButtonContainer;
    }

    public Pane generateAccountButton() {
        Pane pAccountButtonContainer = new Pane();
        Button btnAccount = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-account-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pAccountButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnAccount.getStyleClass().add("btnMainMenuChild");
        btnAccount.setText("Account");
        btnAccount.setGraphic(icon);
        pAccountButtonContainer.getChildren().add(btnAccount);
        return pAccountButtonContainer;
    }

    public Pane generateEmployeeButton() {
        Pane pEmployeeButtonContainer = new Pane();
        Button btnEmployee = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-employee-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pEmployeeButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnEmployee.getStyleClass().add("btnMainMenuChild");
        btnEmployee.setText("Employee");
        btnEmployee.setGraphic(icon);
        pEmployeeButtonContainer.getChildren().add(btnEmployee);
        return pEmployeeButtonContainer;
    }

    public Pane generateStatisticsButton() {
        Pane pStatisticButtonContainer = new Pane();
        Button btnStatistic = new Button();
        ImageView icon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/icons/icons8-statistic-50.png")).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        pStatisticButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnStatistic.getStyleClass().add("btnMainMenuChild");
        btnStatistic.setText("Statistic");
        btnStatistic.setGraphic(icon);
        pStatisticButtonContainer.getChildren().add(btnStatistic);
        return pStatisticButtonContainer;
    }

    public void setMainContent(Pane pMainContentPane, VBox vbMainMenu) {
        double parentWidth = ((AnchorPane) pMainContentPane.getParent()).getPrefWidth();
        double parentHeight = ((AnchorPane) pMainContentPane.getParent()).getPrefHeight();
        double vBoxMainMenuWidth = vbMainMenu.getPrefWidth();
        pMainContentPane.setLayoutY(0);
        pMainContentPane.setPrefHeight(parentHeight);
        pMainContentPane.setPrefWidth(parentWidth - vBoxMainMenuWidth);
    }
}
