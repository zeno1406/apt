package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import INTERFACE.IController;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.HashMap;

public class EmployeeController implements IController {
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
    private TableColumn<EmployeeDTO, String> tlb_col_roleName;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_salary;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_finalSalary;
    @FXML
    private TableColumn<EmployeeDTO, String> tlb_col_status;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML
    private TextField txtSearch;
    @FXML
    private CheckBox ckbStatusFilter;
    @FXML
    private ComboBox<String> cbSearchBy;
    @FXML
    private ComboBox<String> cbRoleFilter;

    // Biến lưu bộ lọc để tránh truy xuất UI nhiều lần
    private String searchBy = "Mã nhân viên";
    private String keyword = "";
    private int roleId = -1;
    private int statusFilter = 1;
    private final HashMap<String, Integer> roleMap = new HashMap<>();

    @FXML
    public void initialize() {
        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();
        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();
        tblEmployee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated
        Platform.runLater(() -> tblEmployee.getSelectionModel().clearSelection());


        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();

        loadTable();
        applyFilters();
    }

    @Override
    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        RoleBUS roleBUS = RoleBUS.getInstance();
        EmployeeBUS employeeBUS = EmployeeBUS.getInstance();

        tlb_col_employeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTime(cellData.getValue().getDateOfBirth())));

        tlb_col_roleName.setCellValueFactory(cellData -> formatCell(roleBUS.getByIdLocal(cellData.getValue().getRoleId()).getName()));
        tlb_col_salary.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getSalary())));

        tlb_col_finalSalary.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(calculateFinalSalary(cellData.getValue(), roleBUS))));

        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));

        UiUtils.gI().addTooltipToColumn(tlb_col_roleName, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_status, 10);
        tblEmployee.setItems(FXCollections.observableArrayList(employeeBUS.getAllLocal()));
    }



    private void loadComboBox() {
        cbSearchBy.getItems().addAll("Mã nhân viên", "Họ đệm", "Tên");

        RoleBUS roleBUS = RoleBUS.getInstance();
        roleMap.clear();

        cbRoleFilter.getItems().add("Tất cả");
        roleMap.put("Tất cả", -1);

        for (RoleDTO role : roleBUS.getAllLocal()) {
            cbRoleFilter.getItems().add(role.getName());
            roleMap.put(role.getName(), role.getId());
        }

        cbSearchBy.getSelectionModel().selectFirst();
        cbRoleFilter.getSelectionModel().selectFirst();
        ckbStatusFilter.setSelected(false);
    }

    @Override
    public void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        cbRoleFilter.setOnAction(event -> handleRoleFilterChange());
        ckbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> resetFilters());
    }

    private void handleStatusFilterChange() {
        statusFilter = ckbStatusFilter.isSelected() ? -1 : 1;
        applyFilters();
    }

    private void handleSearchByChange() {
        searchBy = cbSearchBy.getValue();
        applyFilters();
    }

    private void handleKeywordChange() {
        keyword = txtSearch.getText().trim();
        applyFilters();
    }

    private void handleRoleFilterChange() {
        roleId = getSelectedRoleId();
        applyFilters();
    }

    private int getSelectedRoleId() {
        return roleMap.getOrDefault(cbRoleFilter.getValue(), -1);
    }

    @Override
    public void applyFilters() {
        EmployeeBUS employeeBUS = EmployeeBUS.getInstance();
        tblEmployee.setItems(FXCollections.observableArrayList(
                employeeBUS.filterEmployees(searchBy, keyword, roleId, statusFilter)
        ));
        tblEmployee.getSelectionModel().clearSelection();

    }

    @Override
    public void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst(); // Chọn giá trị đầu tiên
        cbRoleFilter.getSelectionModel().select("Tất cả");
        ckbStatusFilter.setSelected(false); // Mặc định lọc Active
        txtSearch.clear();

        // Cập nhật lại các biến bộ lọc
        searchBy = "Mã nhân viên";
        keyword = "";
        roleId = -1;
        statusFilter = 1; // Chỉ Active
        applyFilters(); // Áp dụng lại bộ lọc

        NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    private BigDecimal calculateFinalSalary(EmployeeDTO employee, RoleBUS roleBUS) {
        BigDecimal coefficient = roleBUS.getByIdLocal(employee.getRoleId()).getSalaryCoefficient();
        return employee.getSalary().multiply(BigDecimal.ONE.add(coefficient));
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(1);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(3);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(2);

        addBtn.setVisible(canAdd);
        addBtn.setManaged(canAdd);

        editBtn.setVisible(canEdit);
        editBtn.setManaged(canEdit);

        deleteBtn.setVisible(canDelete);
        deleteBtn.setManaged(canDelete);
    }
}
