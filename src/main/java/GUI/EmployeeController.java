package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import SERVICE.SessionManagerService;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.util.HashMap;

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
    private TableColumn<EmployeeDTO, String> tlb_col_roleId;
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
    private CheckBox cbStatusFilter;
    @FXML
    private ComboBox<String> cbSearchBy;
    @FXML
    private ComboBox<String> cbRoleFilter;

    // Biến lưu bộ lọc để tránh truy xuất UI nhiều lần
    private String searchBy = null;
    private String keyword = "";
    private int roleId = -1;
    private int statusFilter = 1;

    // HashMap lưu role để tìm ID nhanh hơn
    private final HashMap<String, Integer> roleMap = new HashMap<>();

    @FXML
    public void initialize() {
        Platform.runLater(() -> tblEmployee.getSelectionModel().clearSelection());

        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();
        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();

        tblEmployee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS); // Tránh deprecated

        hideButtonWithoutPermission();
        loadComboBox();
        setupListeners();
        searchBy = cbSearchBy.getSelectionModel().getSelectedItem();
        roleId = getSelectedRoleId();
        loadTable();
        applyFilters();
    }

    private void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        RoleBUS roleBUS = RoleBUS.getInstance();
        EmployeeBUS employeeBUS = EmployeeBUS.getInstance();

        tlb_col_employeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTime(cellData.getValue().getDateOfBirth())));

        tlb_col_roleId.setCellValueFactory(cellData -> formatCell(roleBUS.getByIdLocal(cellData.getValue().getRoleId()).getName()));
        tlb_col_salary.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getSalary())));

        tlb_col_finalSalary.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(calculateFinalSalary(cellData.getValue(), roleBUS))));

        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Active" : "Inactive"));

        addTooltipToColumn(tlb_col_roleId, 10);

        tblEmployee.setItems(FXCollections.observableArrayList(employeeBUS.getAllLocal()));
    }

    private void addTooltipToColumn(TableColumn<EmployeeDTO, String> column, int maxLength) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    if (item.length() > maxLength) {
                        setText(item.substring(0, maxLength) + "...");
                        tooltip.setText(item);
                        tooltip.setShowDelay(Duration.millis(100)); // Giảm thời gian delay
                        tooltip.setHideDelay(Duration.millis(50));
                        setTooltip(tooltip);
                    } else {
                        setText(item);
                        setTooltip(null);
                    }
                }
            }
        });
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

        cbSearchBy.getSelectionModel().selectFirst(); // Chọn giá trị đầu tiên
        cbRoleFilter.getSelectionModel().select("Tất cả");
        cbStatusFilter.setSelected(false);

    }


    private void setupListeners() {
        cbSearchBy.setOnAction(event -> handleSearchByChange());
        cbRoleFilter.setOnAction(event -> handleRoleFilterChange());
        cbStatusFilter.setOnAction(event -> handleStatusFilterChange());
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> handleKeywordChange());
        refreshBtn.setOnAction(event -> resetFilters());
    }

    private void handleStatusFilterChange() {
        statusFilter = cbStatusFilter.isSelected() ? -1 : 1;
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

    private void applyFilters() {
        EmployeeBUS employeeBUS = EmployeeBUS.getInstance();
        tblEmployee.setItems(FXCollections.observableArrayList(
                employeeBUS.filterEmployees(searchBy, keyword, roleId, statusFilter)
        ));
        tblEmployee.getSelectionModel().clearSelection();

    }

    private void resetFilters() {
        cbSearchBy.getSelectionModel().selectFirst(); // Chọn giá trị đầu tiên
        cbRoleFilter.getSelectionModel().select("Tất cả");

        cbStatusFilter.setSelected(false); // Mặc định lọc Active
        txtSearch.clear();

        // Cập nhật lại các biến bộ lọc
        searchBy = null;
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

    private void hideButtonWithoutPermission() {
        addBtn.setVisible(SessionManagerService.getInstance().hasPermission(1));
        editBtn.setVisible(SessionManagerService.getInstance().hasPermission(3));
        deleteBtn.setVisible(SessionManagerService.getInstance().hasModuleAccess(2));
    }
}
