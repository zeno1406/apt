package com.example.shop;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    private final ObservableList<StudentInfo> data = FXCollections.observableArrayList();
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Group root = new Group();
        Scene scene = new Scene(fxmlLoader.load(), Color.BLACK);
        Image iconShop = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo/main-logo.jpg")));
        stage.setTitle("TLang Shop");
//        stage.setMinWidth(1920);
//        stage.setMinHeight(1080);

//        TextField firstNameField = new TextField();
//        firstNameField.setPromptText("First Name");
//
//        TextField lastNameField = new TextField();
//        lastNameField.setPromptText("Last Name");
//        lastNameField.setLayoutY(50);
//
//        TextField ageField = new TextField();
//        ageField.setPromptText("Age");
//        ageField.setLayoutY(100);
//
//        TextField idField = new TextField();
//        idField.setPromptText("ID");
//        idField.setLayoutY(150);
//
//        TableView<StudentInfo> table = new TableView<>(data);
//        table.setPrefHeight(300);
//        table.setPrefWidth(600);
//        table.setLayoutY(300);
//
////        table col
//        TableColumn<StudentInfo, String> colFirstName = new TableColumn<>("first name");
//        TableColumn<StudentInfo, String> colLastName = new TableColumn<>("last name");
//        TableColumn<StudentInfo, Number> colAge = new TableColumn<>("age");
//        TableColumn<StudentInfo, String> colID = new TableColumn<>("ID");
//        colFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
//        colLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
//        colID.setCellValueFactory((cellData -> new SimpleStringProperty(cellData.getValue().getMssv())));
//        colAge.setCellValueFactory( cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()));
//        colID.setMinWidth(100);
//        colFirstName.setMinWidth(200);
//        colLastName.setMinWidth((200));
//        colAge.setMinWidth(100);
//        table.getColumns().addAll(colID ,colFirstName, colLastName, colAge);
//
//        Button addBtn = new Button("Add");
//        addBtn.setLayoutY(200);
//        Button removeBtn = new Button("Remove");
//        removeBtn.setLayoutY(200);
//        removeBtn.setLayoutX(100);
//        Button replaceBtn = new Button("Replace");
//        replaceBtn.setLayoutY(200);
//        replaceBtn.setLayoutX(removeBtn.getLayoutX() + 100);
//        Button exitBtn = new Button("Exit");
//        exitBtn.setLayoutY(200);
//        exitBtn.setLayoutX(replaceBtn.getLayoutX() + 100);
//
//        // Button actions
//        addBtn.setOnAction(e -> {
//            String fName = firstNameField.getText();
//            String lName = lastNameField.getText();
//            String ageStr = ageField.getText();
//            String idStr = idField.getText();
//
//            try {
//                int ageValue = Integer.parseInt(ageStr);
//                // Add new Person to table
//                data.add(new StudentInfo(fName, lName, idStr, ageValue));
//            } catch (NumberFormatException ex) {
//                showAlert("Invalid Age", "Please enter a valid integer for Age.");
//            }
//        });
//
//        removeBtn.setOnAction(e -> {
//            StudentInfo selected = table.getSelectionModel().getSelectedItem();
//            if (selected != null) {
//                data.remove(selected);
//            } else {
//                showAlert("No Selection", "Please select a row to remove.");
//            }
//        });
//
//        replaceBtn.setOnAction(e -> {
//            StudentInfo selected = table.getSelectionModel().getSelectedItem();
//            if (selected != null) {
//                String fName = firstNameField.getText();
//                String lName = lastNameField.getText();
//                String ageStr = ageField.getText();
//                String idStr = idField.getText();
//                try {
//                    int ageValue = Integer.parseInt(ageStr);
//                    selected.setFirstName(fName);
//                    selected.setLastName(lName);
//                    selected.setAge(ageValue);
//                    selected.setMssv(idStr);
//                    table.refresh(); // refresh table to show changes
//                } catch (NumberFormatException ex) {
//                    showAlert("Invalid Age", "Please enter a valid integer for Age.");
//                }
//            } else {
//                showAlert("No Selection", "Please select a row to replace.");
//            }
//        });
//
//        exitBtn.setOnAction(e -> stage.close());
//
//        // Layout
//        GridPane inputPane = new GridPane();
//        inputPane.setHgap(10);
//        inputPane.setVgap(10);
//
//        inputPane.add(new Label("First Name:"), 0, 0);
//        inputPane.add(firstNameField, 1, 0);
//
//        inputPane.add(new Label("Last Name:"), 0, 1);
//        inputPane.add(lastNameField, 1, 1);
//
//        inputPane.add(new Label("Age:"), 0, 2);
//        inputPane.add(ageField, 1, 2);
//
//        inputPane.add(new Label("ID:"), 0, 3);
//        inputPane.add(idField, 1, 3);
//
//        HBox buttonBox = new HBox(10, addBtn, removeBtn, replaceBtn, exitBtn);
//        buttonBox.setAlignment(Pos.CENTER);
//
//        root.getChildren().addAll(addBtn, removeBtn, replaceBtn, exitBtn);
//        root.getChildren().add(firstNameField);
//        root.getChildren().add(lastNameField);
//        root.getChildren().add(ageField);
//        root.getChildren().add(idField);
//        root.getChildren().add(table);
        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(iconShop);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}