
package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DAL.StatisticDAL;
import DTO.StatisticRevenue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Map;

public class StatisticController {
    @FXML
    private LineChart<String, Number> chart1; // S�+�a ki�+�u r+� r+�ng

    @FXML
    private CategoryAxis x1;

    @FXML
    private NumberAxis y1;

    Map<LocalDate, BigDecimal> doanhThuTheoNgay;
    Map<Month, BigDecimal> doanhThuTheoThang;
    Map<Integer, BigDecimal> doanhThuTheoNam;


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
        tbl_col_productcate.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        tbl_col_totalquantity.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        tbl_col_revenue.setCellValueFactory(cellData ->
                formatCell(validationUtils.formatCurrency(cellData.getValue().getRevenue())));

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
        btnThongKe1.setOnAction(event -> handleStatisticBtn1());
        btnExportExcel1.setOnAction(event -> handleExportExcel());
        btnThongKe2.setOnAction(e -> handleStatisticBtn2());
        btnExportExcel2.setOnAction(e -> handleExportExcel());
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
            tblProductRevenue.setItems(productRevenuesList);

            BigDecimal totalProductRevenue = BigDecimal.ZERO;
            for (ProductRevenue item : list) {
                totalProductRevenue = totalProductRevenue.add(item.getRevenue());
            }

            txtTotalRevenue1.setText(ValidationUtils.getInstance().formatCurrency(totalProductRevenue));
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
            tblEmployeeRevenue.setItems(employeeRevenueList);
            BigDecimal totalYearRevenue = BigDecimal.ZERO;
            for (QuarterlyEmployeeRevenue item : list) {
                totalYearRevenue = totalYearRevenue.add(item.getRevenue());
            }
            txtTotalRevenue2.setText(ValidationUtils.getInstance().formatCurrency(totalYearRevenue));
        } catch (NumberFormatException e) {
            NotificationUtils.showErrorAlert("Năm không hợp lệ", "Lỗi");
        } catch (IllegalArgumentException e) {
            NotificationUtils.showErrorAlert(e.getMessage(), "Lỗi");
        } catch (Exception e) {
            NotificationUtils.showErrorAlert("Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi");
        }
    }

    // Phương thức xuất Excel cho tab 1 (Doanh thu sản phẩm)
    private void exportProductRevenueToExcel(File file, LocalDate startDate, LocalDate endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TK_sanpham_"+now());

            // Thêm tên bảng thống kê và thời gian từ ngày đến ngày
            Row headerInfoRow = sheet.createRow(0);
            headerInfoRow.createCell(0).setCellValue("Bảng thống kê doanh thu sản phẩm từ " +
                    startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    " đến " + endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            // Thêm thời gian xuất file vào ô
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Row timeRow = sheet.createRow(1);
            timeRow.createCell(0).setCellValue("Thời gian xuất file: " + currentTime);

            // Thêm tiêu đề cho bảng thống kê
            Row headerRow = sheet.createRow(2);
            headerRow.createCell(0).setCellValue("Mã sản phẩm");
            headerRow.createCell(1).setCellValue("Tên sản phẩm");
            headerRow.createCell(2).setCellValue("Thể loại sản phẩm");
            headerRow.createCell(3).setCellValue("Số lượng bán ra");
            headerRow.createCell(4).setCellValue("Tổng doanh thu");

            // Dữ liệu bảng thống kê
            int rowNum = 3;
            for (ProductRevenue item : productRevenuesList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getProductId());
                row.createCell(1).setCellValue(item.getProductName());
                row.createCell(2).setCellValue(item.getCategoryName());
                row.createCell(3).setCellValue(item.getTotalQuantity());
                row.createCell(4).setCellValue(item.getRevenue().doubleValue());
            }

            // Tổng doanh thu
            Row totalRevenueRow = sheet.createRow(rowNum+1);
            totalRevenueRow.createCell(3).setCellValue("Tổng: ");
            BigDecimal totalProductRevenue = BigDecimal.ZERO;
            for (ProductRevenue item : productRevenuesList) {
                totalProductRevenue = totalProductRevenue.add(item.getRevenue());
            }
            totalRevenueRow.createCell(4).setCellValue(totalProductRevenue.doubleValue());

            //Căn chỉnh cột trước khi lưu(bỏ qua cột đầu tiên)
            for (int i = 1; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Lưu file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            NotificationUtils.showInfoAlert("Xuất file Excel thành công!", "Thông báo");
        } catch (Exception e) {
            NotificationUtils.showErrorAlert("Lỗi khi xuất file: " + e.getMessage(), "Lỗi");
        }
    }

    // Phương thức xuất Excel cho tab 2 (Doanh thu nhân viên theo quý)
    private void exportEmployeeRevenueToExcel(File file, String year) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TK_nhanvien_"+now());

            // Thêm tên bảng thống kê và thời gian từ ngày đến ngày
            Row headerInfoRow = sheet.createRow(0);
            headerInfoRow.createCell(0).setCellValue("Bảng thống kê doanh thu sản phẩm theo Quý năm " + year);

            // Thêm thời gian xuất file vào ô
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Row timeRow = sheet.createRow(1);
            timeRow.createCell(0).setCellValue("Thời gian xuất file: " + currentTime);

            // Thêm tiêu đề cho bảng thống kê
            Row headerRow = sheet.createRow(2);
            headerRow.createCell(0).setCellValue("Mã Nhân viên");
            headerRow.createCell(1).setCellValue("Quý 1");
            headerRow.createCell(2).setCellValue("Quý 2");
            headerRow.createCell(3).setCellValue("Quý 3");
            headerRow.createCell(4).setCellValue("Quý 4");
            headerRow.createCell(5).setCellValue("Tổng doanh thu");

            int rowNum = 3;
            for (QuarterlyEmployeeRevenue item : employeeRevenueList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getEmployeeId());
                row.createCell(1).setCellValue(item.getQuarter1().doubleValue());
                row.createCell(2).setCellValue(item.getQuarter2().doubleValue());
                row.createCell(3).setCellValue(item.getQuarter3().doubleValue());
                row.createCell(4).setCellValue(item.getQuarter4().doubleValue());
                row.createCell(5).setCellValue(item.getRevenue().doubleValue());
            }

            // Tổng doanh thu
            Row totalRevenueRow = sheet.createRow(rowNum+1);
            totalRevenueRow.createCell(4).setCellValue("Tổng: ");
            BigDecimal totalEmployeeRevenue = BigDecimal.ZERO;
            for (QuarterlyEmployeeRevenue item : employeeRevenueList) {
                totalEmployeeRevenue = totalEmployeeRevenue.add(item.getRevenue());
            }
            totalRevenueRow.createCell(5).setCellValue(totalEmployeeRevenue.doubleValue());

            //Căn chỉnh cột trước khi lưu(bỏ qua cột đầu tiên)
            for (int i = 1; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            //Lưu file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            NotificationUtils.showInfoAlert("Xuất file Excel thành công!", "Thông báo");
        } catch (Exception e) {
            NotificationUtils.showErrorAlert("Lỗi khi xuất file: " + e.getMessage(), "Lỗi");
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
