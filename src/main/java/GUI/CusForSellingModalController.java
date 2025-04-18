package GUI;

import BUS.CustomerBUS;
import BUS.ImportBUS;
import BUS.ProductBUS;
import BUS.SupplierBUS;
import DTO.CustomerDTO;
import DTO.SupplierDTO;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
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
    private TextField txtSearchCustomer, txtFieldCusFirstName, txtFieldCusLastName, txtFieldCusPhone, txtFieldCusAddress;
    @FXML
    private DatePicker dpDateOfBirth;
    @FXML
    private TableView <CustomerDTO> tbvInfoCusList;
    @FXML
    private TableColumn<CustomerDTO, String> tbcCusFirstName, tbcCusLastName, tbcCusPhone;
    @FXML
    private TableColumn<CustomerDTO, Integer> tbcCusID;
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
        tbcCusID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbcCusFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tbcCusLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tbcCusPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        UiUtils.gI().addTooltipToColumn(tbcCusFirstName, 10);
        UiUtils.gI().addTooltipToColumn(tbcCusLastName, 10);
        UiUtils.gI().addTooltipToColumn(tbcCusPhone, 10);

        tbvInfoCusList.setItems(FXCollections.observableArrayList(CustomerBUS.getInstance().filterCustomers("", "", 1)));
        tbvInfoCusList.getSelectionModel().clearSelection();
    }
    
    // Set click Event
    public void setOnMouseClicked() {
        btnExitGetCustomer.setOnAction(e -> handleClose());
        btnSubmitCustomer.setOnAction(e -> handleGetCustomer());
        btnSearchCustomer.setOnMouseClicked(e -> handleSearch());
    }

    // handle search
    private void handleSearch() {
        tbvInfoCusList.setItems(FXCollections.observableArrayList(CustomerBUS.getInstance().searchCustomerByPhone(txtSearchCustomer.getText().trim())));
        tbvInfoCusList.getSelectionModel().clearSelection();
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
        selectedCustomer = tbvInfoCusList.getSelectionModel().getSelectedItem();
        return selectedCustomer == null;
    }
}
