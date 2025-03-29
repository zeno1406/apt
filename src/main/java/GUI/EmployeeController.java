package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.beans.property.SimpleObjectProperty;

import javax.management.relation.Role;

public class EmployeeController {
    @FXML
    private TableView<EmployeeDTO> tblEmployee;
    @FXML
    private TableColumn<EmployeeDTO, Integer> tlb_col_employeeId;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_firstName;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_lastName;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_dateOfBirth;
    @FXML
    private TableColumn<EmployeeDTO, Integer> tlb_col_roleId;
    @FXML
    private TableColumn<EmployeeDTO, BigDecimal> tlb_col_salary;
    @FXML
    private TableColumn<EmployeeDTO, BigDecimal> tlb_col_finalSalary;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_status;

    @FXML
    public void initialize() {
        Platform.runLater(() -> tblEmployee.getSelectionModel().clearSelection());
        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();
        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();
        tblEmployee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTable();
    }

    private void setupTable() {
        tlb_col_employeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData -> new SimpleStringProperty(ValidationUtils.getInstance().formatDateTime(cellData.getValue().getDateOfBirth())));
        tlb_col_roleId.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        tlb_col_salary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        tlb_col_finalSalary.setCellValueFactory(cellData-> {

            BigDecimal finalSalary = cellData.getValue().getSalary().multiply(BigDecimal.ONE.add(RoleBUS.getInstance().getByIdLocal(cellData.getValue()
                    .getRoleId()).getSalaryCoefficient()));
            return new SimpleObjectProperty<>(finalSalary);
        });
        tlb_col_status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isStatus() ? "Active" : "Inactive"));
        tblEmployee.setItems(FXCollections.observableArrayList(EmployeeBUS.getInstance().getAllLocal()));
        tblEmployee.refresh();
    }
}
