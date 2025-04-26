package GUI;

import BUS.CustomerBUS;
import BUS.ImportBUS;
import BUS.ProductBUS;
import BUS.SupplierBUS;
import DTO.CustomerDTO;
import DTO.SupplierDTO;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import org.w3c.dom.Text;

import java.awt.*;

public class CusForSellingModalController {
    @FXML
    private Button btnExitGetCustomer, btnSearchCustomer, btnSubmitCustomer;
    @FXML
    private TextField txtSearchCustomer;
    @FXML private TableView<CustomerDTO> tblCustomer;
    @FXML private TableColumn<CustomerDTO, Integer> tlb_col_id;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_firstName;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_lastName;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_dateOfBirth;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_phone;
    @FXML private TableColumn<CustomerDTO, String> tlb_col_address;
    @Getter
    private boolean isSaved;
    @Getter
    private CustomerDTO selectedCustomer;

    @FXML
    public void initialize()
    {
        if  (CustomerBUS.getInstance().isLocalEmpty()) CustomerBUS.getInstance().loadLocal();
        setOnMouseClicked();
        loadTable();
    }

    // Setup Customer Table
    public void loadTable() {
        tlb_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData ->
                new SimpleStringProperty(ValidationUtils.getInstance().formatDateTime(cellData.getValue().getDateOfBirth())));

        tlb_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tlb_col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        UiUtils.gI().addTooltipToColumn(tlb_col_firstName, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_lastName, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_dateOfBirth, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_phone, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_address, 10);

        tblCustomer.setItems(FXCollections.observableArrayList(CustomerBUS.getInstance().filterCustomers("", "", 1)));
        tblCustomer.getSelectionModel().clearSelection();
    }
    
    // Set click Event
    public void setOnMouseClicked() {
        btnExitGetCustomer.setOnAction(e -> handleClose());
        btnSubmitCustomer.setOnAction(e -> handleGetCustomer());
//        btnSearchCustomer.setOnMouseClicked(e -> handleSearch());
        txtSearchCustomer.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    // handle search
    private void handleSearch() {
        tblCustomer.setItems(FXCollections.observableArrayList(CustomerBUS.getInstance().searchCustomerByPhone(txtSearchCustomer.getText().trim())));
        tblCustomer.getSelectionModel().clearSelection();
    }

    // close
    private void handleClose() {
        if (btnExitGetCustomer.getScene() != null && btnExitGetCustomer.getScene().getWindow() != null) {
            Stage stage = (Stage) btnExitGetCustomer.getScene().getWindow();
            stage.close();
        }
    }

    // handle select customer
    private void handleGetCustomer() {
        if (isNotSelectedCustomer()) {
            NotificationUtils.showErrorAlert("Vui lòng chọn khách hàng", "Thông báo");
            return;
        }
        isSaved = true;
        handleClose();
    }

    // check select customer
    private boolean isNotSelectedCustomer() {
        selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
        return selectedCustomer == null;
    }
}
