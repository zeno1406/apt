package GUI;

import BUS.ProductBUS;
import DTO.ProductDTO;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class EventForImportAndSell {
    private static final EventForImportAndSell INSTANCE = new EventForImportAndSell();

    public EventForImportAndSell() {}
    public static EventForImportAndSell getInstance() {
        return INSTANCE;
    }


    // Add constraint row
    public void addConstraintRow(GridPane gridPane, ArrayList<ProductDTO> products, int height) {
        boolean row_col = true, wait = false;
        int quantity = products.size();
        for (int i = 0; i < quantity / 2 ; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(height);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        int prevRow = 0;
        int total = gridPane.getRowCount() * 2;
        System.out.println(total);
        for (int i = 0; i < total; i++) {
            String url = "/images/default/" + "default.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(url)).toString());
            System.out.print(image);
            row_col = !row_col;
            addProductToGrid(gridPane, wait ? prevRow : i, row_col ? 1 : 0, image, products.get(i).getName(),  products.get(i).getStockQuantity(), products.get(i).getSellingPrice());
            wait = !wait;
            prevRow = i;
        }
    }


    // add container
    public void addProductToGrid(GridPane gridPane, int row, int col, Image image, String name, int quantity, BigDecimal price) {
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

        // Thêm vào grid
        gridPane.add(productBox, col, row);
    }

    private HBox createInfoRow(Label label, Label value) {
        HBox row = new HBox(5);
        row.getChildren().addAll(label, value);
        return row;
    }

    public ArrayList<ProductDTO> listLocalProducts() {
        ArrayList<ProductDTO> list = ProductBUS.getInstance().getAllLocal();
        if (list.isEmpty()) {
            ProductBUS.getInstance().getAll();
            ProductBUS.getInstance().loadLocal();
            list = ProductBUS.getInstance().getAllLocal();
        }
        return list;
    }

    public void addEventClickForProduct(TableView<String> tableView) {

    }
}
