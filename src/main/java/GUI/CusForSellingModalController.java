package GUI;

import BUS.ImportBUS;
import BUS.ProductBUS;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

public class CusForSellingModalController {
    @FXML
    private Button btnExitGetCustomer;
    @FXML
    public void initialize()
    {
        setOnMouseClicked();
    }

    // Set click Event
    private void setOnMouseClicked() {
        btnExitGetCustomer.setOnMouseClicked(event -> handleClose());
//        btnExitSellingForm.setOnMouseClicked(event -> onMouseClickedExitSellingForm());
//        btnSearchProduct.setOnMouseClicked(event -> onMousedClickSearchProduct());
//        btnGetCusInfo.setOnMouseClicked(event -> onMouseClickedShowCustomerContainer());
//        btnInvoiceListProductRemove.setOnMouseClicked(e -> onMouseClickedRemove());
//        btnInvoiceListProductClear.setOnMouseClicked(e -> onMouseClickedClear());
    }

    private void handleClose() {
        if (btnExitGetCustomer.getScene() != null && btnExitGetCustomer.getScene().getWindow() != null) {
            Stage stage = (Stage) btnExitGetCustomer.getScene().getWindow();
            stage.close();
        }
    }
}
