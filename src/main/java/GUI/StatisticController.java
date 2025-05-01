package GUI;

import BUS.StatisticBUS;
import DTO.StatisticDTO;
import DTO.StatisticDTO.ProductRevenue;
import DTO.StatisticDTO.QuarterlyEmployeeRevenue;
import INTERFACE.IController;
import SERVICE.ExcelService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import UTILS.NotificationUtils;
import UTILS.ValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static java.time.LocalDate.now;

public class StatisticController implements IController {
    @FXML
    private Tab tab1;
    @FXML
    private TableView<ProductRevenue> tblProductRevenue;
    @FXML
    private TableColumn<ProductRevenue, Integer> tbl_col_productid;
    @FXML
    private TableColumn<ProductRevenue, String> tbl_col_productname;
    @FXML
    private TableColumn<ProductRevenue, String> tbl_col_productcate;
    @FXML
    public TableColumn<ProductRevenue, Integer> tbl_col_totalquantity;
    @FXML
    private DatePicker txtStartCreateDate;
    @FXML
    private DatePicker txtEndCreateDate;
    @FXML
    private Button btnExportExcel1;
    @FXML
    private Button btnThongKe1;
    @FXML
    private Tab tab2;
    @FXML
    private TableView<DTO.StatisticDTO.QuarterlyEmployeeRevenue> tblEmployeeRevenue;
    @FXML
    private TableColumn<DTO.StatisticDTO.QuarterlyEmployeeRevenue, Integer> tbl_col_id;
    @FXML
    private TableColumn<DTO.StatisticDTO.QuarterlyEmployeeRevenue, String> tbl_col_quy1;
    @FXML
    private TableColumn<DTO.StatisticDTO.QuarterlyEmployeeRevenue, String> tbl_col_quy2;
    @FXML
    private TableColumn<DTO.StatisticDTO.QuarterlyEmployeeRevenue, String> tbl_col_quy3;
    @FXML
    private TableColumn<DTO.StatisticDTO.QuarterlyEmployeeRevenue, String> tbl_col_quy4;
    @FXML
    private TableColumn<DTO.StatisticDTO.QuarterlyEmployeeRevenue, String> tbl_col_total;
    @FXML
    private TextField txtInputYear;
    @FXML
    private Button btnExportExcel2;
    @FXML
    private Button btnThongKe2;
    @FXML
    private Label txtTotalRevenue2;

    private ArrayList<StatisticDTO.ProductRevenue> productRevenuesList = new ArrayList<>();
    private ArrayList<DTO.StatisticDTO.QuarterlyEmployeeRevenue> employeeRevenueList = new ArrayList<>();

    @FXML
    public void initialize() {
        tblProductRevenue.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tblEmployeeRevenue.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        Platform.runLater(() -> {
            tblProductRevenue.getSelectionModel().clearSelection();
            tblEmployeeRevenue.getSelectionModel().clearSelection();
        });

        hideButtonWithoutPermission();
        setupListeners();
        loadTable();
    }

    @Override
    public void loadTable() {
        ValidationUtils validationUtils = ValidationUtils.getInstance();
        // Product Revenue Table
        tbl_col_productid.setCellValueFactory(new PropertyValueFactory<>("productId"));
        tbl_col_productname.setCellValueFactory(new PropertyValueFactory<>("productName"));
        tbl_col_productcate.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        tbl_col_totalquantity.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        // Employee Revenue Table
        tbl_col_id.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        tbl_col_quy1.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getQuarter1())));
        tbl_col_quy2.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getQuarter2())));
        tbl_col_quy3.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getQuarter3())));
        tbl_col_quy4.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getQuarter4())));
        tbl_col_total.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getRevenue())));
    }

    private SimpleStringProperty formatCell(String value) {
        return new SimpleStringProperty(value);
    }

    @Override
    public void setupListeners() {
        btnThongKe1.setOnAction(event -> {
            handleStatisticBtn1();
            tblProductRevenue.getSelectionModel().clearSelection();
        });
        btnExportExcel1.setOnAction(event -> {
            try {
                handleExportExcel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnThongKe2.setOnAction(e -> {
            handleStatisticBtn2();
            tblEmployeeRevenue.getSelectionModel().clearSelection();
        });
        btnExportExcel2.setOnAction(e -> {
            try {
                handleExportExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void handleStatisticBtn1() {
        try {
            if (txtStartCreateDate.getValue() == null || txtEndCreateDate.getValue() == null) {
                throw new IllegalArgumentException("Vui lòng chọn đầy đủ khoảng thời gian.");
            }

            LocalDate start = txtStartCreateDate.getValue();
            LocalDate end = txtEndCreateDate.getValue();

            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            }

            ArrayList<ProductRevenue> list = StatisticBUS.getInstance().getProductRevenue(start, end);
            productRevenuesList.clear();
            productRevenuesList.addAll(list);
            tblProductRevenue.setItems((FXCollections.observableArrayList(productRevenuesList)));

            if (productRevenuesList.isEmpty()) {
                NotificationUtils.showInfoAlert("Không có dữ liệu tương ứng.", "Thông báo");
            } else {
                NotificationUtils.showInfoAlert("Thống kê thành công.", "Thông báo");
            }
        } catch (IllegalArgumentException e) {
            NotificationUtils.showErrorAlert(e.getMessage(), "Lỗi");
        } catch (Exception e) {
            NotificationUtils.showErrorAlert("Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi");
        }
    }

    private void handleStatisticBtn2() {
        try {
            int year = Integer.parseInt(txtInputYear.getText());
            ArrayList<QuarterlyEmployeeRevenue> list = StatisticBUS.getInstance().getQuarterlyEmployeeRevenue(year);
            employeeRevenueList.clear();
            employeeRevenueList.addAll(list);
            tblEmployeeRevenue.setItems((FXCollections.observableArrayList(employeeRevenueList)));
            BigDecimal totalYearRevenue = BigDecimal.ZERO;
            for (QuarterlyEmployeeRevenue item : list) {
                totalYearRevenue = totalYearRevenue.add(item.getRevenue());
            }
            txtTotalRevenue2.setText(ValidationUtils.getInstance().formatCurrency(totalYearRevenue));
            if (employeeRevenueList.isEmpty()) {
                NotificationUtils.showInfoAlert("Không có dữ liệu tương ứng.", "Thông báo");
            } else {
                NotificationUtils.showInfoAlert("Thống kê thành công.", "Thông báo");
            }
        } catch (NumberFormatException e) {
            NotificationUtils.showErrorAlert("Năm không hợp lệ", "Lỗi");
        } catch (IllegalArgumentException e) {
            NotificationUtils.showErrorAlert(e.getMessage(), "Lỗi");
        } catch (Exception e) {
            NotificationUtils.showErrorAlert("Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi");
        }
    }

    private void handleExportExcel() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss"));

        // Kiểm tra dữ liệu
        if (tab1.isSelected() && productRevenuesList.isEmpty()) {
            NotificationUtils.showInfoAlert("Không có dữ liệu để xuất cho Doanh thu Sản phẩm", "Thông báo");
            return;
        } else if (tab2.isSelected() && employeeRevenueList.isEmpty()) {
            NotificationUtils.showInfoAlert("Không có dữ liệu để xuất cho Doanh thu Nhân Viên", "Thông báo");
            return;
        }

        LocalDate start = txtStartCreateDate.getValue();
        LocalDate end = txtEndCreateDate.getValue();
        String year = txtInputYear.getText();

        if (tab1.isSelected()) {
            ExcelService.getInstance().exportToFileExcelProductRevenues(productRevenuesList, timestamp,start, end);
        } else if (tab2.isSelected()) {
            ExcelService.getInstance().exportToFileExcelEmployeeRevenues(employeeRevenueList, timestamp, year);
        }
    }

    @Override
    public void applyFilters() {

    }

    @Override
    public void resetFilters() {

    }

    @Override
    public void hideButtonWithoutPermission() {

    }
}
