package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class SellingController {
    @FXML
    private Button btnExitSellingForm;

    @FXML
    public void initialize()
    {
    }

    // exit form
    @FXML
    private void onMouseClickedExitImportingForm(MouseEvent e) {
        (MainController.getInstance()).openStage("/GUI/NavigatePermission.fxml");
        btnExitSellingForm.getScene().getWindow().hide();
    }
}
