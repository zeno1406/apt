package GUI;

import BUS.ImportBUS;
import DTO.CategoryDTO;
import DTO.ImportDTO;
import DTO.SupplierDTO;
import SERVICE.SessionManagerService;
import UTILS.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.jboss.jandex.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ImportProductController {
    @FXML
    private Button btnExitDetailEdit, btnExitImportingForm, btnImportListProductEdit, btnSearchProduct, btnImportListProductClear, btnImportListProductAdd, btnImportListProductRemove, btnSubmitImport;
    @FXML
    private HBox hbMoreEditImportProduct;
    @FXML
    private Label lbSellingProductName, lbFieldImportID, lbFieldImportDate, lbFieldImportEMPID, lbFieldImportEMPName;
    @FXML
    private GridPane gpShowProductWrapper;
    @FXML
    private TextField txtFieldProductImage, txtFieldProductName, txtFieldProductQuantity, txtProductPrice;
    @FXML
    private ComboBox<CategoryDTO> cbxFieldProductCategory;
    @FXML
    private ComboBox<SupplierDTO> txtFieldProductSupplier;

    @FXML
    public void initialize()
    {
        MainController temp = MainController.getInstance();
        temp.addConstraintRow(gpShowProductWrapper, temp.listLocalProducts(), 80);
        changeLabelContent();
        setOnMouseClicked();
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
        (UiUtils.gI()).openStage("/GUI/NavigatePermission.fxml", "danh sách chức năng");
        btnExitImportingForm.getScene().getWindow().hide();
    }

    @FXML
    private void onMouseClickedImportListProductEdit(MouseEvent e) {
        hbMoreEditImportProduct.setDisable(false);
        hbMoreEditImportProduct.setVisible(true);
    }

    private void changeLabelContent() {
        lbFieldImportID.setText(ImportBUS.getInstance().autoId());
        lbFieldImportDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        lbFieldImportEMPID.setText(String.valueOf(SessionManagerService.getInstance().currEmployee().getId()));
        lbFieldImportEMPName.setText(SessionManagerService.getInstance().currEmployee().getFullName());
    }

    private void setOnMouseClicked() {
        System.out.println((long) gpShowProductWrapper.getChildren().size());

    }
}
