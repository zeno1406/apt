package GUI;

import BUS.ImportBUS;
import BUS.ProductBUS;
import DTO.CategoryDTO;
import DTO.ImportDTO;
import DTO.ProductDTO;
import DTO.SupplierDTO;
import SERVICE.SessionManagerService;
import UTILS.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jboss.jandex.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ImportProductController {
    @FXML
    private Button btnExitImportingForm, btnImportListProductEdit, btnSearchProduct, btnImportListProductClear, btnImportListProductAdd, btnImportListProductRemove, btnSubmitImport, btnGetSupInfo;
    @FXML
    private Label lbSellingProductName, lbFieldImportID, lbFieldImportDate, lbFieldImportEMPID, lbFieldImportEMPName;
    @FXML
    private GridPane gpShowProductWrapper;
    @FXML
    private TextField txtFieldProductImage, txtFieldProductName, txtFieldProductQuantity, txtProductPrice, txtProductNameSearch;
    @FXML
    private ComboBox<CategoryDTO> cbxFieldProductCategory;
    @FXML
    private ComboBox<SupplierDTO> txtFieldProductSupplier;
    @FXML
    private TableView<List<String>> tbvDetailImportProduct;

    @FXML
    public void initialize()
    {
        EventForImportAndSell temp = EventForImportAndSell.getInstance();
        temp.addConstraintRow(gpShowProductWrapper, temp.listLocalProducts(), 80.0);
        temp.addEventClickForProduct(tbvDetailImportProduct, gpShowProductWrapper);
        changeLabelContent();
        setOnMouseClicked();
    }

    // Set click Event
    private void setOnMouseClicked() {
        System.out.println((long) gpShowProductWrapper.getChildren().size());
        btnExitImportingForm.setOnMouseClicked(event -> onMouseClickedExitImportingForm());
        btnImportListProductEdit.setOnMouseClicked(event -> onMouseClickedImportListProductEdit());
        btnSearchProduct.setOnMouseClicked(event -> onMousedClickSearchProduct());
        btnGetSupInfo.setOnMouseClicked(event -> onMouseClickedShowSupplierContainer());
    }

    // search
    private void onMousedClickSearchProduct() {
        System.out.println(ProductBUS.getInstance().getAllLocal().size());
        System.out.println(txtProductNameSearch.getText());
        ArrayList<ProductDTO> list = ProductBUS.getInstance().getAllLocal();
        for(ProductDTO product : list) {
            if (product.getName().toLowerCase().contains(txtProductNameSearch.getText().toLowerCase().trim()))
                System.out.println("product : " + product.getName());
        }
        EventForImportAndSell temp = EventForImportAndSell.getInstance();
        temp.hiddenProduct(txtProductNameSearch.getText(), gpShowProductWrapper);
    }

    // show select supplier
    private void onMouseClickedShowSupplierContainer() {
        (UiUtils.gI()).openStage("/GUI/SupForImportModal.fxml", "Danh Sách Nhà Cung Cấp", (Stage) btnGetSupInfo.getScene().getWindow());
    }

    // close
    private void onMouseClickedExitImportingForm() {
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "danh sách chức năng");
        btnExitImportingForm.getScene().getWindow().hide();
    }

    private void onMouseClickedImportListProductEdit() {
        (UiUtils.gI()).openStage("/GUI/ImportProductModal.fxml", "sửa chi tiết", (Stage) btnImportListProductEdit.getScene().getWindow());
    }

    private void changeLabelContent() {
        lbFieldImportID.setText(String.valueOf(ImportBUS.getInstance().getAllLocal().size()+1));
        lbFieldImportDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        lbFieldImportEMPID.setText(String.valueOf(SessionManagerService.getInstance().currEmployee().getId()));
        lbFieldImportEMPName.setText(SessionManagerService.getInstance().currEmployee().getFullName());
    }
}
