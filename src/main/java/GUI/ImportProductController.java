package GUI;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.jboss.jandex.Main;

import java.util.Objects;

public class ImportProductController {
    @FXML
    private Button btnExitDetailEdit, btnExitImportingForm, btnImportListProductEdit, btnSearchProduct, btnImportListProductClear, btnImportListProductAdd, btnImportListProductRemove, btnSubmitImport;
    @FXML
    private HBox hbMoreEditImportProduct;
    @FXML
    private Label lbSellingProductName, btnSellingProductQuantity, btnSellingProductPrice;
    @FXML
    private GridPane gpShowProductWrapper;

    @FXML
    public void initialize()
    {
        MainController.getInstance().addConstraintRow(gpShowProductWrapper, 10, 80);
    }




    // close
    // Set click Event
    @FXML
    private void onMouseClickedExitDetailEdit(MouseEvent e) {
            hbMoreEditImportProduct.setDisable(true);
            hbMoreEditImportProduct.setVisible(false);
    }

    @FXML
    private void onMouseClickedExitImportingForm(MouseEvent e) {
        (MainController.getInstance()).openStage("/GUI/NavigatePermission.fxml");
        btnExitImportingForm.getScene().getWindow().hide();
    }

    @FXML
    private void onMouseClickedImportListProductEdit(MouseEvent e) {
        hbMoreEditImportProduct.setDisable(false);
        hbMoreEditImportProduct.setVisible(true);
    }
}
