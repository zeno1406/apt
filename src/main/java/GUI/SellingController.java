package GUI;

import DTO.CategoryDTO;
import DTO.ProductDTO;
import UTILS.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import javax.swing.*;

public class SellingController {
    @FXML
    private Button btnExitSellingForm, btnSubmitIInvoice, btnSearchProduct, btnInvoiceListProductClear, btnInvoiceListProductEdit, btnInvoiceListProductRemove;
    @FXML
    private TextField txtSellingProductNameSearch, txtFieldShowInvoiceSaleCode, txtFieldShowInvoiceSaleValue;
    @FXML
    private Label lbTotalInvoicePrice, lbFieldInvoiceID, lbFieldInvoiceEMPID, lbFieldInvoiceDate, lbFieldInvoiceCUSID, lbFieldInvoiceEMPName, lbFieldInvoiceCUSName;
    @FXML
    private TableView<ProductDTO> tbvDetailInvoiceProduct;
    @FXML
    private GridPane gpShowProductWrapper;
    @FXML
    private ComboBox<CategoryDTO> cbxListProductFilter;

    @FXML
    public void initialize()
    {
        EventForImportAndSell temp = EventForImportAndSell.getInstance();
        temp.addConstraintRow(gpShowProductWrapper, temp.listLocalProducts(), 80);
    }

    // exit form
    @FXML
    private void onMouseClickedExitImportingForm(MouseEvent e) {
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "danh sách chức năng");
        btnExitSellingForm.getScene().getWindow().hide();
    }
}
