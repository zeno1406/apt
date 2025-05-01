
package SERVICE;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import DTO.RoleDTO;
import DTO.StatisticDTO;
import UTILS.AvailableUtils;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

public class ExcelService {
    private static final ExcelService INSTANCE = new ExcelService();
    private ExcelService() {}
    public static ExcelService getInstance() {
        return INSTANCE;
    }

    public void exportToFileExcel(String exportData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Source");

        if (exportData.equalsIgnoreCase("employee"))
            sheet = sheetOfEmployee(sheet, EmployeeBUS.getInstance().getAllLocal());
        else if (exportData.equalsIgnoreCase("product"))
            sheet = sheetOfProduct(sheet, ProductBUS.getInstance().getAllLocal());

        // Auto-size all columns
        int numberOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < numberOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = exportData.toLowerCase() + ".xlsx";
        File file = new File(fileName);

        // Kiểm tra nếu file đang mở
        if (file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                 FileChannel channel = raf.getChannel()) {
                try {
                    channel.lock(); // thử lock file
                } catch (IOException e) {
                    System.err.println("File đang được mở. Vui lòng đóng file trước khi xuất.");
                    workbook.close();
                    return;
                }
            } catch (IOException e) {
                NotificationUtils.showErrorAlert("Không thể truy cập file: " + e.getMessage(), "Thông báo");
                workbook.close();
                return;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }

        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                System.err.println("Không thể mở file: " + e.getMessage());
            }
        }
    }

    private Sheet sheetOfEmployee(Sheet sheet, List<EmployeeDTO> employeeDTOList) {
        int rowIndex = 0;
        Row rowHeader = sheet.createRow(rowIndex++);
        rowHeader.createCell(0).setCellValue("ID");
        rowHeader.createCell(1).setCellValue("First Name");
        rowHeader.createCell(2).setCellValue("Last Name");
        rowHeader.createCell(3).setCellValue("Date Of Birth");
        rowHeader.createCell(4).setCellValue("Role Name");
        rowHeader.createCell(5).setCellValue("Salary");
        rowHeader.createCell(6).setCellValue("Final Salary");
        rowHeader.createCell(7).setCellValue("Status");
        RoleBUS rolBus = RoleBUS.getInstance();
        ValidationUtils validate = ValidationUtils.getInstance();
        for (EmployeeDTO employee : employeeDTOList) {
            Row dataRow = sheet.createRow(rowIndex++);
            RoleDTO role = rolBus.getByIdLocal(employee.getRoleId());
            dataRow.createCell(0).setCellValue(employee.getId());
            dataRow.createCell(1).setCellValue(employee.getFirstName());
            dataRow.createCell(2).setCellValue(employee.getLastName());
            dataRow.createCell(3).setCellValue(ValidationUtils.getInstance().formatDateTime(employee.getDateOfBirth()));
            dataRow.createCell(4).setCellValue(role != null ? role.getName() : "");
            dataRow.createCell(5).setCellValue(validate.formatCurrency(employee.getSalary()));
            dataRow.createCell(6).setCellValue(role != null ? validate.formatCurrency(employee.getSalary().multiply(role.getSalaryCoefficient())) : validate.formatCurrency(employee.getSalary()));
            dataRow.createCell(7).setCellValue(employee.isStatus() ? "Hoạt động" : "Ngưng hoat động");
        }

        return sheet;
    }

    private Sheet sheetOfProduct(Sheet sheet, List<ProductDTO> productDTOList) {
        int rowIndex = 0;
        Row rowHeader = sheet.createRow(0);
        rowHeader.createCell(0).setCellValue("ID");
        rowHeader.createCell(1).setCellValue("Name");
        rowHeader.createCell(2).setCellValue("Stock Quantity");
        rowHeader.createCell(3).setCellValue("Selling Price");

        for (ProductDTO product : productDTOList) {
            Row dataRow = sheet.createRow(++rowIndex);
            dataRow.createCell(0).setCellValue(product.getId());
            dataRow.createCell(1).setCellValue(product.getName());
            dataRow.createCell(2).setCellValue(product.getStockQuantity());
            dataRow.createCell(3).setCellValue(product.getSellingPrice().toString());
        }
        return sheet;
    }

    public void ImportSheet(String importData, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file Excel để nhập dữ liệu");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            return; // Người dùng không chọn file
        }

        // Kiểm tra đúng định dạng
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            NotificationUtils.showErrorAlert("Vui lòng chọn file Excel (.xlsx)", "Thông báo");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (importData.equalsIgnoreCase("products")) {
                importToProducts(sheet);
            }

        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtils.showErrorAlert("Không thể mở file Excel: " + e.getMessage(), "Lỗi");
        }
    }


    private void importToProducts(Sheet sheet) {
        ArrayList<ProductDTO> list = returnListProduct(sheet, new ArrayList<>());

        if (list.isEmpty()) {
            return;
        }

        if (UiUtils.gI().showConfirmAlert("Bạn chắc chắn muốn thêm sản phẩm bằng Excel?", "Thông báo")) {
            int deleteResult = ProductBUS.getInstance().insertListProductExcel(list);
            switch (deleteResult) {
                case 1 -> NotificationUtils.showInfoAlert("Thêm sản phẩm thành công.", "Thông báo");
                case 2 -> NotificationUtils.showErrorAlert("Danh sách rỗng.", "Thông báo");
                case 3 -> NotificationUtils.showErrorAlert("Dữ liệu đầu vào không hợp lệ.", "Thông báo");
                case 4 -> NotificationUtils.showErrorAlert("Thể loại không hợp lệ hoặc đã bị xóa.", "Thông báo");
                case 5 -> NotificationUtils.showErrorAlert("Tên sản phẩm trong hệ thống đã tồn tại.", "Thông báo");
                case 6 -> NotificationUtils.showErrorAlert("Có lỗi khi thêm danh sách sản phẩm qua Excel.", "Thông báo");
                case 7 -> NotificationUtils.showErrorAlert("Lỗi cơ sở dữ liệu khi insert.", "Thông báo");
                default -> NotificationUtils.showErrorAlert("Lỗi không xác định, vui lòng thử lại sau.", "Thông báo");
            }
        }

    }

    //    VALIDATE ON PRODUCT BUS
    private ArrayList<ProductDTO> returnListProduct(Sheet sheet, ArrayList<ProductDTO> list) {
        if (list == null) return new ArrayList<>();
        list.clear(); // Clear luôn nếu không null

        ArrayList<ProductDTO> tempList = new ArrayList<>();
        StringBuilder errorMessages = new StringBuilder();
        int errorCount = 0;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Bỏ qua tiêu đề
            if (isRowEmpty(row)) continue; // Bỏ qua dòng trống

            try {
                Cell nameCell = row.getCell(1);
                Cell descCell = row.getCell(2);
                Cell categoryCell = row.getCell(3);
                Cell statusCell = row.getCell(4);

                String name = (nameCell != null) ? nameCell.getStringCellValue().trim() : "";
                String description = (descCell != null) ? descCell.getStringCellValue().trim() : "";

                if (name.isEmpty()) {
                    if (handleError(errorMessages, row.getRowNum(), "Tên sản phẩm không được để trống.", ++errorCount)) break;
                    continue;
                }
                if (name.length() > 148) {
                    if (handleError(errorMessages, row.getRowNum(), "Tên sản phẩm không được quá 148 ký tự.", ++errorCount)) break;
                    continue;
                }
                if (description.length() > 65400) {
                    if (handleError(errorMessages, row.getRowNum(), "Mô tả không được quá 65k4 ký tự.", ++errorCount)) break;
                    continue;
                }

                int categoryId;
                try {
                    categoryId = (int) categoryCell.getNumericCellValue();
                } catch (Exception e) {
                    if (handleError(errorMessages, row.getRowNum(), "Thể loại không hợp lệ (phải là số).", ++errorCount)) break;
                    continue;
                }
                if (categoryId < 0 || !AvailableUtils.getInstance().isValidCategory(categoryId)) {
                    if (handleError(errorMessages, row.getRowNum(), "Thể loại không hợp lệ hoặc đã bị xóa.", ++errorCount)) break;
                    continue;
                }

                int statusInt;
                try {
                    statusInt = (int) statusCell.getNumericCellValue();
                } catch (Exception e) {
                    if (handleError(errorMessages, row.getRowNum(), "Trạng thái không hợp lệ (phải là 0 hoặc 1).", ++errorCount)) break;
                    continue;
                }
                if (statusInt != 0 && statusInt != 1) {
                    if (handleError(errorMessages, row.getRowNum(), "Trạng thái chỉ được là 0 hoặc 1.", ++errorCount)) break;
                    continue;
                }

                tempList.add(new ProductDTO(null, name, 0, null, statusInt == 1, description, null, categoryId));

            } catch (Exception e) {
                if (handleError(errorMessages, row.getRowNum(), "Lỗi không xác định: " + e.getMessage(), ++errorCount)) break;
            }
        }

        if (errorMessages.length() > 0) {
            if (errorCount > 20) {
                errorMessages.append("Và một số lỗi khác không thể hiển thị.\n");
            }
            NotificationUtils.showErrorAlert(errorMessages.toString(), "Thông báo");
            return new ArrayList<>();
        }

        list.addAll(tempList);
        return list;
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean handleError(StringBuilder errorMessages, int rowNum, String message, int errorCount) {
        errorMessages.append("Dòng ").append(rowNum + 1).append(": ").append(message).append("\n");
        return errorCount >= 20;
    }

    public void exportToFileExcelProductRevenues(ArrayList<StatisticDTO.ProductRevenue> productRevenuesList, String timestamp, LocalDate start, LocalDate end) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TK_Product_"+now());

        Row headerInfoRow = sheet.createRow(0);
        headerInfoRow.createCell(0).setCellValue("Bảng thống kê doanh thu sản phẩm từ " +
                start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                " đến " + end.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

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

        ValidationUtils validate = ValidationUtils.getInstance();

        // Dữ liệu bảng thống kê
        int rowNum = 3;
        for (StatisticDTO.ProductRevenue item : productRevenuesList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getProductId());
            row.createCell(1).setCellValue(item.getProductName());
            row.createCell(2).setCellValue(item.getCategoryName());
            row.createCell(3).setCellValue(item.getTotalQuantity());
        }

        // Tổng doanh thu
        Row totalRevenueRow = sheet.createRow(rowNum+1);
        totalRevenueRow.createCell(3).setCellValue("Tổng: ");
        int totalProductQuantity = 0;
        for (StatisticDTO.ProductRevenue item : productRevenuesList) {
            totalProductQuantity = totalProductQuantity + (item.getTotalQuantity());
        }
        totalRevenueRow.createCell(4).setCellValue(totalProductQuantity);

        //Căn chỉnh cột trước khi lưu(bỏ qua cột đầu tiên)
        for (int i = 1; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "ThongKe_LegoStore_TK_Product_" + timestamp + ".xlsx";
        File file = new File(fileName);

        // Kiểm tra nếu file đang mở
        if (file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                 FileChannel channel = raf.getChannel()) {
                try {
                    channel.lock(); // thử lock file
                } catch (IOException e) {
                    System.err.println("File đang được mở. Vui lòng đóng file trước khi xuất.");
                    workbook.close();
                    return;
                }
            } catch (IOException e) {
                NotificationUtils.showErrorAlert("Không thể truy cập file: " + e.getMessage(), "Thông báo");
                workbook.close();
                return;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }

        if (file.exists()) {
            try {
                NotificationUtils.showInfoAlert("Xuất file Excel thành công!", "Thông báo");
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                System.err.println("Không thể mở file: " + e.getMessage());
            }
        }
    }

    public void exportToFileExcelEmployeeRevenues(ArrayList<StatisticDTO.QuarterlyEmployeeRevenue> employeeRevenueList, String timestamp, String year) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TK_Employee_"+now());

        // Thêm tên bảng thống kê và thời gian từ ngày đến ngày
        Row headerInfoRow = sheet.createRow(0);
        headerInfoRow.createCell(0).setCellValue("Bảng thống kê doanh thu sản phẩm theo Quý năm " + year);

        // Thêm thời gian xuất file vào ô
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
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
        ValidationUtils validate = ValidationUtils.getInstance();
        int rowNum = 3;
        for (StatisticDTO.QuarterlyEmployeeRevenue item : employeeRevenueList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getEmployeeId());
            row.createCell(1).setCellValue(validate.formatCurrency(item.getQuarter1()));
            row.createCell(2).setCellValue(validate.formatCurrency(item.getQuarter2()));
            row.createCell(3).setCellValue(validate.formatCurrency(item.getQuarter3()));
            row.createCell(4).setCellValue(validate.formatCurrency(item.getQuarter4()));
            row.createCell(5).setCellValue(validate.formatCurrency(item.getRevenue()));
        }

        // Tổng doanh thu
        Row totalRevenueRow = sheet.createRow(rowNum+1);
        totalRevenueRow.createCell(4).setCellValue("Tổng: ");
        BigDecimal totalEmployeeRevenue = BigDecimal.ZERO;
        for (StatisticDTO.QuarterlyEmployeeRevenue item : employeeRevenueList) {
            totalEmployeeRevenue = totalEmployeeRevenue.add(item.getRevenue());
        }
        totalRevenueRow.createCell(5).setCellValue(validate.formatCurrency(totalEmployeeRevenue));

        //Căn chỉnh cột trước khi lưu(bỏ qua cột đầu tiên)
        for (int i = 1; i <= 5; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "ThongKe_LegoStore_TK_Employee_" + timestamp + ".xlsx";
        File file = new File(fileName);

        // Kiểm tra nếu file đang mở
        if (file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                 FileChannel channel = raf.getChannel()) {
                try {
                    channel.lock(); // thử lock file
                } catch (IOException e) {
                    System.err.println("File đang được mở. Vui lòng đóng file trước khi xuất.");
                    workbook.close();
                    return;
                }
            } catch (IOException e) {
                NotificationUtils.showErrorAlert("Không thể truy cập file: " + e.getMessage(), "Thông báo");
                workbook.close();
                return;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }

        if (file.exists()) {
            try {
                NotificationUtils.showInfoAlert("Xuất file Excel thành công!", "Thông báo");
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                System.err.println("Không thể mở file: " + e.getMessage());
            }
        }

    }


}
