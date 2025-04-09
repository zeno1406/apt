package GUI;

import BUS.DiscountBUS;
import DTO.DiscountDTO;
import UTILS.NotificationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;

public class DiscountAdvanceSearchModalController {
    @FXML
    private Button saveBtn,closeBtn;
    @FXML
    private TextField txtDiscountName;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private ComboBox<String> cbTypeDiscount;
    private String discountName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int type = 0;
    @Getter
    private boolean isSaved;
    @Getter
    private ArrayList<DiscountDTO> filteredDiscounts;
    @FXML
    public void initialize() {
        loadComboBox();
        setupListeners();
    }

    private void loadComboBox() {
        cbTypeDiscount.getItems().addAll("Phߦ�n tr-�m", "Giߦ�m c�+�ng");
        cbTypeDiscount.getSelectionModel().selectFirst();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void handleSave() {
        this.discountName = txtDiscountName.getText();
        this.type = cbTypeDiscount.getValue().equals("Phߦ�n tr-�m") ? 0 : 1;
        this.startDate = dpStartDate.getValue();
        this.endDate = dpEndDate.getValue();
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            NotificationUtils.showErrorAlert("Ng+�y bߦ�t -�ߦ�u kh+�ng -榦�+�c l�+�n h��n ng+�y kߦ+t th+�c.", "Th+�ng b+�o");
            return;
        }

        filteredDiscounts = DiscountBUS.getInstance().filterDiscountsAdvance(discountName, type, startDate, endDate);
        int numResult = filteredDiscounts.size();
        if (numResult != 0) {
            NotificationUtils.showErrorAlert("T+�m thߦ�y " + numResult + " kߦ+t quߦ� ph+� h�+�p.", "Th+�ng b+�o");
            this.isSaved = true;
            handleClose();
        } else {
            NotificationUtils.showErrorAlert("Kh+�ng t+�m thߦ�y kߦ+t quߦ� ph+� h�+�p.", "Th+�ng b+�o");
        }
    }

    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }
}