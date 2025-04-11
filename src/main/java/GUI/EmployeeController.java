
    package GUI;

    import BUS.EmployeeBUS;
    import BUS.RoleBUS;
    import DTO.EmployeeDTO;
    import DTO.RoleDTO;
    import INTERFACE.IController;
    import SERVICE.ExcelService;
    import SERVICE.PrintService;
    import SERVICE.RolePermissionService;
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
    import javafx.scene.layout.HBox;

    import java.io.IOException;
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
    private HBox functionBtns;
    @FXML
    private Button addBtn, editBtn, deleteBtn, refreshBtn, exportExcel;
    @FXML
    private TextField txtSearch;
    @FXML
    private CheckBox ckbStatusFilter;
    @FXML
    private ComboBox<String> cbSearchBy;
    @FXML
    private ComboBox<String> cbRoleFilter;

    // Biߦ+n l��u b�+� l�+�c -��+� tr+�nh truy xuߦ�t UI nhi�+�u lߦ�n
    private String searchBy = "Mã nhân viên";
    private String keyword = "";
    private int roleId = -1;
    private int statusFilter = 1;
    private final HashMap<String, Integer> roleMap = new HashMap<>();
    private EmployeeDTO selectedEmployee;

    @FXML
    public void initialize() {
        if (EmployeeBUS.getInstance().isLocalEmpty()) EmployeeBUS.getInstance().loadLocal();
        if (RoleBUS.getInstance().isLocalEmpty()) RoleBUS.getInstance().loadLocal();
        tblEmployee.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
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

        tlb_col_employeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlb_col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tlb_col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tlb_col_dateOfBirth.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatDateTime(cellData.getValue().getDateOfBirth())));

        tlb_col_roleName.setCellValueFactory(cellData -> {
            var role = roleBUS.getByIdLocal(cellData.getValue().getRoleId());
            return new SimpleStringProperty(role != null && role.getName() != null ? role.getName() : "");
        });

        tlb_col_salary.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getSalary())));

        tlb_col_finalSalary.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(calculateFinalSalary(cellData.getValue(), roleBUS))));

        tlb_col_status.setCellValueFactory(cellData ->
                formatCell(cellData.getValue().isStatus() ? "Hoạt động" : "Ngưng hoạt động"));

        UiUtils.gI().addTooltipToColumn(tlb_col_roleName, 10);
        UiUtils.gI().addTooltipToColumn(tlb_col_status, 10);
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
        refreshBtn.setOnAction(event -> {
            resetFilters();
            NotificationUtils.showInfoAlert("Làm mới thành công", "Thông báo");
        });

        addBtn.setOnAction(event -> handleAddBtn());
        deleteBtn.setOnAction(e -> handleDeleteBtn());
        editBtn.setOnAction(e -> handleEditBtn());
        exportExcel.setOnAction(e -> {
            try {
                handleExportExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
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
        cbSearchBy.getSelectionModel().selectFirst(); // Ch�+�n gi+� tr�+� -�ߦ�u ti+�n
        cbRoleFilter.getSelectionModel().select("Tất cả");
        ckbStatusFilter.setSelected(false); // Mߦ+c -��+�nh l�+�c Active
        txtSearch.clear();

        // Cߦ�p nhߦ�t lߦ�i c+�c biߦ+n b�+� l�+�c
        searchBy = "Mã nhân viên";
        keyword = "";
        roleId = -1;
        statusFilter = 1; // Ch�+� Active
        applyFilters(); // +�p d�+�ng lߦ�i b�+� l�+�c
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    private BigDecimal calculateFinalSalary(EmployeeDTO employee, RoleBUS roleBUS) {
        RoleDTO role = roleBUS.getByIdLocal(employee.getRoleId());

        if (role == null || role.getSalaryCoefficient() == null) {
            return employee.getSalary();
        }

        return employee.getSalary().multiply(role.getSalaryCoefficient());
    }

    @Override
    public void hideButtonWithoutPermission() {
        boolean canAdd = SessionManagerService.getInstance().hasPermission(1);
        boolean canEdit = SessionManagerService.getInstance().hasPermission(3);
        boolean canDelete = SessionManagerService.getInstance().hasPermission(2);

        if (!canAdd) functionBtns.getChildren().remove(addBtn);
        if (!canEdit) functionBtns.getChildren().remove(editBtn);
        if (!canDelete) functionBtns.getChildren().remove(deleteBtn);
    }

    public void handleAddBtn() {
        EmployeeModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/EmployeeModal.fxml",
                controller -> controller.setTypeModal(0),
                "Th+�m nh+�n vi+�n"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("Th+�m nh+�n vi+�n th+�nh c+�ng", "Th+�ng b+�o");
            resetFilters();
        }
    }

    public void handleDeleteBtn() {
        if (isNotSelectedEmployee()) {
            NotificationUtils.showErrorAlert("Vui l+�ng ch�+�n nh+�n vi+�n", "Th+�ng b+�o");
            return;
        }
        if (selectedEmployee.getId() == SessionManagerService.getInstance().employeeLoginId()) {
            NotificationUtils.showErrorAlert("Bߦ�n kh+�ng th�+� x+�a th+�ng tin c�+�a ch+�nh m+�nh.", "Th+�ng b+�o");
            return;
        }
        if(selectedEmployee.getId() == 1)
        {
            NotificationUtils.showErrorAlert("Kh+�ng th�+� x+�a nh+�n vi+�n g�+�c.", "Th+�ng b+�o");
            return;
        }

//        int numEmployeeHasRole = EmployeeBUS.getInstance().numEmployeeHasRoleId(selectedRole.getId());
//        if (numEmployeeHasRole != 0) {
//            String ask = "Hi�+�n c+� " + numEmployeeHasRole + " nh+�n vi+�n s�+� h�+�u ch�+�c v�+� n+�y!";
//            if (!UiUtils.gI().showConfirmAlert("Bߦ�n chߦ�c mu�+�n x+�a ch�+�c v�+� n+�y? " + ask, "Th+�ng b+�o x+�c nhߦ�n")) return;
//        }

        int deleteResult = EmployeeBUS.getInstance().delete(selectedEmployee.getId(),SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());

        switch (deleteResult) {
            case 1 ->
            {
                NotificationUtils.showInfoAlert("X+�a nh+�n vi+�n th+�nh c+�ng.", "Th+�ng b+�o");
                resetFilters();
            }
            case 2 ->
                    NotificationUtils.showErrorAlert("C+� l�+�i khi x+�a nh+�n vi+�n. Vui l+�ng th�+� lߦ�i.", "Th+�ng b+�o");
            case 3 ->
                    NotificationUtils.showErrorAlert("Bߦ�n kh+�ng th�+� x+�a th+�ng tin c�+�a ch+�nh m+�nh.", "Th+�ng b+�o");
            case 4 ->
                    NotificationUtils.showErrorAlert("Bߦ�n kh+�ng c+� quy�+�n \"X+�a nh+�n vi+�n\" -��+� th�+�c hi�+�n thao t+�c n+�y.", "Th+�ng b+�o");
            case 5 ->
                    NotificationUtils.showErrorAlert("Bߦ�n kh+�ng th�+� x+�a nh+�n vi+�n ngang quy�+�n", "Th+�ng b+�o");
            case 6 ->
                    NotificationUtils.showErrorAlert("X+�a nh+�n vi+�n thߦ�t bߦ�i. Vui l+�ng th�+� lߦ�i sau.", "Th+�ng b+�o");
            case 7 ->
                    NotificationUtils.showErrorAlert("Nh+�n vi+�n kh+�ng h�+�p l�+� hoߦ+c -�+� b�+� x+�a.", "Th+�ng b+�o");
            case 8 ->
                    NotificationUtils.showErrorAlert("Kh+�ng th�+� x+�a nh+�n vi+�n g�+�c.", "Th+�ng b+�o");
            default ->
                    NotificationUtils.showErrorAlert("L�+�i kh+�ng x+�c -��+�nh, vui l+�ng th�+� lߦ�i sau.", "Th+�ng b+�o");
        }
    }

    public void handleEditBtn() {
        if (isNotSelectedEmployee()) {
            NotificationUtils.showErrorAlert("Vui l+�ng ch�+�n nh+�n vi+�n", "Th+�ng b+�o");
            return;
        }
        if(selectedEmployee.getId() == 1 && SessionManagerService.getInstance().employeeLoginId() != 1)
        {
            NotificationUtils.showErrorAlert("Kh+�ng th�+� s�+�a nh+�n vi+�n g�+�c.", "Th+�ng b+�o");
            return;
        }
        EmployeeModalController modalController = UiUtils.gI().openStageWithController(
                "/GUI/EmployeeModal.fxml",
                controller -> {
                    controller.setTypeModal(1);
                    controller.setEmployee(selectedEmployee);
                },
                "S�+�a nh+�n vi+�n"
        );
        if (modalController != null && modalController.isSaved()) {
            NotificationUtils.showInfoAlert("S�+�a nh+�n vi+�n th+�nh c+�ng", "Th+�ng b+�o");
            applyFilters();
        }
    }

    private void handleExportExcel() throws IOException {
        try {
            ExcelService.getInstance().exportToFileExcel("employee");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isNotSelectedEmployee() {
        selectedEmployee = tblEmployee.getSelectionModel().getSelectedItem();
        return selectedEmployee == null;
    }
}