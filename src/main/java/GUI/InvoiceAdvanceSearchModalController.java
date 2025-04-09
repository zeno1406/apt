
package GUI;

import BUS.CategoryBUS;
import BUS.InvoiceBUS;
import BUS.ProductBUS;
import DTO.InvoiceDTO;
import UTILS.NotificationUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class InvoiceAdvanceSearchModalController {
    @FXML
    private Button saveBtn,closeBtn;
    @FXML
    private TextField txtEmployeeId;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtDiscountCode;
    @FXML
    private DatePicker txtStartCreateDate;
    @FXML
    private DatePicker txtEndCreateDate;
    @FXML
    private TextField txtStartTotalPrice;
    @FXML
    private TextField txtEndTotalPrice;
    private String employeeId;
    private String customerId;
    private String discountCode;
    private LocalDate startCreateDate;
    private LocalDate endCreateDate;
    private BigDecimal startTotalPrice;
    private BigDecimal endTotalPrice;
    @Getter
    private boolean isSaved;
    @Getter
    private ArrayList<InvoiceDTO> filteredInvoices;
    @FXML
    public void initialize() {
        setupListeners();
    }

    public void setupListeners() {
        saveBtn.setOnAction(e -> handleSave());
        closeBtn.setOnAction(e -> handleClose());
    }

    private void handleSave() {
        this.employeeId = txtEmployeeId.getText();
        this.customerId = txtCustomerId.getText();
        this.discountCode = txtDiscountCode.getText();
        this.startCreateDate = txtStartCreateDate.getValue();
        this.endCreateDate = txtEndCreateDate.getValue();
        validTotalPrice();
        if (employeeId.isEmpty() && customerId.isEmpty() && discountCode.isEmpty()
                && startCreateDate == null && endCreateDate == null &&
                startTotalPrice == null && endTotalPrice == null) {
            NotificationUtils.showErrorAlert("Vui l+�ng nhߦ�p +�t nhߦ�t m�+�t -�i�+�u ki�+�n -��+� l�+�c.", "Th+�ng b+�o");
            return;
        }

        if (startCreateDate != null && endCreateDate != null && startCreateDate.isAfter(endCreateDate)) {
            NotificationUtils.showErrorAlert("Ng+�y bߦ�t -�ߦ�u kh+�ng -榦�+�c l�+�n h��n ng+�y kߦ+t th+�c.", "Th+�ng b+�o");
            return;
        }

        filteredInvoices = InvoiceBUS.getInstance().filterInvoicesAdvance(employeeId, customerId, discountCode, startCreateDate, endCreateDate, startTotalPrice, endTotalPrice);
        int numResult = filteredInvoices.size();
        if (numResult != 0) {
            NotificationUtils.showErrorAlert("T+�m thߦ�y " + numResult + " kߦ+t quߦ� ph+� h�+�p.", "Th+�ng b+�o");
            this.isSaved = true;
            handleClose();
        } else {
            NotificationUtils.showErrorAlert("Kh+�ng t+�m thߦ�y kߦ+t quߦ� ph+� h�+�p.", "Th+�ng b+�o");
        }
    }

    private void validTotalPrice() {
        try {
            String startText = txtStartTotalPrice.getText().trim();
            if (!startText.isEmpty()) {
                startTotalPrice = new BigDecimal(startText);
            } else {
                startTotalPrice = null;
            }

            String endText = txtEndTotalPrice.getText().trim();
            if (!endText.isEmpty()) {
                endTotalPrice = new BigDecimal(endText);
            } else {
                endTotalPrice = null;
            }
        } catch (NumberFormatException e) {
        }
    }


    private void handleClose() {
        if (closeBtn.getScene() != null && closeBtn.getScene().getWindow() != null) {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        }
    }
}