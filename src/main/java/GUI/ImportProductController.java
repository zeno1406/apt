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
import javafx.stage.Stage;
import org.jboss.jandex.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ImportProductController {
    @FXML
    private Button btnExitImportingForm, btnImportListProductEdit, btnSearchProduct, btnImportListProductClear, btnImportListProductAdd, btnImportListProductRemove, btnSubmitImport;
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
        EventForImportAndSell temp = EventForImportAndSell.getInstance();
        temp.addConstraintRow(gpShowProductWrapper, temp.listLocalProducts(), 80);
        changeLabelContent();
        setOnMouseClicked();
    }

    // Set click Event
    private void setOnMouseClicked() {
        System.out.println((long) gpShowProductWrapper.getChildren().size());
        btnExitImportingForm.setOnMouseClicked(event -> onMouseClickedExitImportingForm());
        btnImportListProductEdit.setOnMouseClicked(event -> onMouseClickedImportListProductEdit());
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
