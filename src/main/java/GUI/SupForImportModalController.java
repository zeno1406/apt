package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SupForImportModalController {
    @FXML
    private Button btnExitGetSupplier;
    @FXML
    public void initialize()
    {
        setOnMouseClicked();
    }

    // Set click Event
    private void setOnMouseClicked() {
        btnExitGetSupplier.setOnMouseClicked(e -> onMouseClickedExitGetSupplier());
    }

    // close
    private void onMouseClickedExitGetSupplier() {
        ((Stage) (btnExitGetSupplier.getScene().getWindow())).close();
    }
}
