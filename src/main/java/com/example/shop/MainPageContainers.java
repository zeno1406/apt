package com.example.shop;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MainPageContainers {
    public Pane generateProductButton() {
        Pane pProductButtonContainer = new Pane();
        Button btnProduct = new Button();
        pProductButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnProduct.getStyleClass().add("btnMainMenuChild");
        btnProduct.setText("Product");
        pProductButtonContainer.getChildren().add(btnProduct);
        return pProductButtonContainer;
    }

    public Pane generateCustomerButton() {
        Pane pCustomerButtonContainer = new Pane();
        Button btnCustomer = new Button();
        pCustomerButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnCustomer.getStyleClass().add("btnMainMenuChild");
        btnCustomer.setText("Customer");
        pCustomerButtonContainer.getChildren().add(btnCustomer);
        return pCustomerButtonContainer;
    }

    public Pane generateSupplierButton() {
        Pane pSupplierButtonContainer = new Pane();
        Button btnSupplier = new Button();
        pSupplierButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnSupplier.getStyleClass().add("btnMainMenuChild");
        btnSupplier.setText("Supplier");
        pSupplierButtonContainer.getChildren().add(btnSupplier);
        return pSupplierButtonContainer;
    }

    public Pane generateTypeButton() {
        Pane pTypeButtonContainer = new Pane();
        Button btnType = new Button();
        pTypeButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnType.getStyleClass().add("btnMainMenuChild");
        btnType.setText("Type");
        pTypeButtonContainer.getChildren().add(btnType);
        return pTypeButtonContainer;
    }

    public Pane generateSaleEventButton() {
        Pane pSaleEventButtonContainer = new Pane();
        Button btnSaleEvent = new Button();
        pSaleEventButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnSaleEvent.getStyleClass().add("btnMainMenuChild");
        btnSaleEvent.setText("Sale Event");
        pSaleEventButtonContainer.getChildren().add(btnSaleEvent);
        return pSaleEventButtonContainer;
    }

    public Pane generateGrnButton() {
        Pane pGrnButtonContainer = new Pane();
        Button btnGrn = new Button();
        pGrnButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnGrn.getStyleClass().add("btnMainMenuChild");
        btnGrn.setText("GRN");
        pGrnButtonContainer.getChildren().add(btnGrn);
        return pGrnButtonContainer;
    }

    public Pane generateBillButton() {
        Pane pBillButtonContainer = new Pane();
        Button btnBill = new Button();
        pBillButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnBill.getStyleClass().add("btnMainMenuChild");
        btnBill.setText("Bill");
        pBillButtonContainer.getChildren().add(btnBill);
        return pBillButtonContainer;
    }

    public Pane generateRoleButton() {
        Pane pRoleButtonContainer = new Pane();
        Button btnRole = new Button();
        pRoleButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnRole.getStyleClass().add("btnMainMenuChild");
        btnRole.setText("Role");
        pRoleButtonContainer.getChildren().add(btnRole);
        return pRoleButtonContainer;
    }

    public Pane generateAccountButton() {
        Pane pAccountButtonContainer = new Pane();
        Button btnAccount = new Button();
        pAccountButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnAccount.getStyleClass().add("btnMainMenuChild");
        btnAccount.setText("Account");
        pAccountButtonContainer.getChildren().add(btnAccount);
        return pAccountButtonContainer;
    }

    public Pane generateEmployeeButton() {
        Pane pEmployeeButtonContainer = new Pane();
        Button btnEmployee = new Button();
        pEmployeeButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnEmployee.getStyleClass().add("btnMainMenuChild");
        btnEmployee.setText("Employee");
        pEmployeeButtonContainer.getChildren().add(btnEmployee);
        return pEmployeeButtonContainer;
    }

    public Pane generateStatisticButton() {
        Pane pStatisticButtonContainer = new Pane();
        Button btnStatistic = new Button();
        pStatisticButtonContainer.getStyleClass().add("pMainMenuContainer");
        btnStatistic.getStyleClass().add("btnMainMenuChild");
        btnStatistic.setText("Statistic");
        pStatisticButtonContainer.getChildren().add(btnStatistic);
        return pStatisticButtonContainer;
    }
}
