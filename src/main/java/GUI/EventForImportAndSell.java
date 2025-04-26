package GUI;

import BUS.ProductBUS;
import DTO.ProductDTO;
import UTILS.ValidationUtils;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.controlsfx.control.spreadsheet.Grid;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventForImportAndSell {
    private static final EventForImportAndSell INSTANCE = new EventForImportAndSell();

    public EventForImportAndSell() {}
    public static EventForImportAndSell getInstance() {
        return INSTANCE;
    }


    // Add constraint row
    public void addConstraintRow(GridPane gridPane, ArrayList<ProductDTO> products, double height) {
        boolean row_col = true, wait = false;
        int quantity = products.size();
        int numRows = (int) Math.ceil(quantity / 2.0);
        for (int i = 0; i < numRows ; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(height);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        int prevRow = 0;
        System.out.println(quantity);
        for (int i = 0; i < quantity; i++) {
            String url = "/images/default/" + "default.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(url)).toString());
            row_col = !row_col;
            addProductToGrid(gridPane, wait ? prevRow : i, row_col ? 1 : 0, image, products.get(i).getName(),  products.get(i).getStockQuantity(), products.get(i).getSellingPrice(), products.get(i).getId());
            wait = !wait;
            prevRow = i;
        }
    }

    // add container
    public void addProductToGrid(GridPane gridPane, int row, int col, Image image, String name, int quantity, BigDecimal price, String id) {
        // ImageView
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(134);
        imageView.setFitHeight(134);
        imageView.setPreserveRatio(true);
        // Labels
        Label nameLabel = new Label("Name :");
        Label quantityLabel = new Label("Quantity :");
        Label priceLabel = new Label("Price :");

        Label nameValue = new Label(name);
        Label quantityValue = new Label(String.valueOf(quantity));
        Label priceValue = new Label(String.format("%.0f ₫", price));

        // VBox
        VBox infoBox = new VBox(10); // khoảng cách giữa các dòng
        infoBox.getChildren().addAll(
                createInfoRow(nameLabel, nameValue),
                createInfoRow(quantityLabel, quantityValue),
                createInfoRow(priceLabel, priceValue)
        );
        infoBox.setPrefHeight(134);

        // HBox
        HBox productBox = new HBox(10);
        productBox.getChildren().addAll(imageView, infoBox);
        productBox.setPadding(new Insets(5));
        productBox.setStyle("-fx-background-color: #e0ffff; -fx-border-color: #ccc;");
        productBox.setPrefHeight(134);
        //set name as id for quick searching
        productBox.setId(id);

        // Thêm vào grid
        gridPane.add(productBox, col, row);
    }

    private HBox createInfoRow(Label label, Label value) {
        HBox row = new HBox(5);
        row.getChildren().addAll(label, value);
        return row;
    }

    //get local products
    public ArrayList<ProductDTO> listLocalProducts() {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().getAllLocal();
        if (list.isEmpty()) {
            ProductBUS.getInstance().getAll();
            ProductBUS.getInstance().loadLocal();
            list = ProductBUS.getInstance().getAllLocal();
        }
        return list;
    }

    public void hiddenProduct(String name, GridPane gridPane) {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().getAllLocal();
        ArrayList<ProductDTO> temp = new ArrayList<>();
        for(ProductDTO product : list) {
            if (product.getName().toLowerCase().contains(name.toLowerCase().trim()))
                temp.add(product);
        }
        if(temp.isEmpty()) {
            clearGrid(gridPane);
            System.out.println("Not found");
            return;
        }
        double height = getConstraintRowHeight(gridPane);
        clearGrid(gridPane);
        addConstraintRow(gridPane, temp, height);
    }

    public double getConstraintRowHeight(GridPane gridPane) {
        return gridPane.getRowConstraints().getFirst().getPrefHeight();
    }

    public void clearGrid(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
    }

    public void addEventClickForProduct(TableView<List<String>> tableView, GridPane gridPane) {
        ObservableList<Node> listNode = gridPane.getChildren();
        for(Node node : listNode) {
            String productID = node.getId();
            node.setOnMouseClicked(event -> addProductToTable(tableView, productID));
        }
    }

    public void addProductToTable(TableView<List<String>> tableView, String productId) {
        ProductDTO product = ProductBUS.getInstance().getByIdLocal(productId);
        if (product == null) return;

        ObservableList<List<String>> items = tableView.getItems();
        List<String> list = new ArrayList<>();
        list.add(String.valueOf(items.size() + 1));
        list.add(product.getName());
        list.add(String.valueOf(1));
        list.add(String.valueOf(product.getSellingPrice()));
        list.add(String.valueOf(product.getSellingPrice().multiply(BigDecimal.valueOf(1))));
        tableView.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/base.css")).toExternalForm());
        tableView.getItems().add(list);
        System.out.println(list);
    }

}
