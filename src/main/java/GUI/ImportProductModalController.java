package GUI;

import DTO.CategoryDTO;
import DTO.SupplierDTO;
import UTILS.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ImportProductModalController {
    @FXML
    private Button btnExitDetailEdit, btnSubmitDetailEdit;
    @FXML
    private TextArea txtaFieldEditDescription;
    @FXML
    private TextField txtFieldProductImage, txtFieldProductName, txtFieldProductQuantity, txtProductPrice;
    @FXML
    private ComboBox<CategoryDTO> cbxFieldProductCategory;
    @FXML
    private ComboBox<SupplierDTO> txtFieldProductSupplier;

    @FXML
    public void initialize()
    {
        setOnMouseClicked();
    }

    // Set click Event
    private void setOnMouseClicked() {
        btnExitDetailEdit.setOnMouseClicked(e -> onMouseClickedExitImportingDetailEdit());
    }

    // close
    private void onMouseClickedExitImportingDetailEdit() {
        ((Stage) (btnExitDetailEdit.getScene().getWindow())).close();
    }
}
