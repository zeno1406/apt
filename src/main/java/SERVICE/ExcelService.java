
package SERVICE;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import DTO.RoleDTO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!list.isEmpty()) list.clear();

        ArrayList<ProductDTO> tempList = new ArrayList<>();
        StringBuilder errorMessages = new StringBuilder();
        int errorCount = 0; // Đếm số lỗi

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề
                boolean isEmpty = true;
                for (Cell cell : row) {
                    if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty) continue;
                try {
                    String name = row.getCell(1).getStringCellValue().trim();
                    String description = row.getCell(2) != null ? row.getCell(2).getStringCellValue().trim() : null;

                    Cell categoryCell = row.getCell(3);
                    Cell statusCell = row.getCell(4);

                    // Kiểm tra và chuyển đổi categoryId
                    int categoryId;
                    try {
                        categoryId = (int) categoryCell.getNumericCellValue();
                    } catch (Exception e) {
                        errorMessages.append("Dòng ").append(row.getRowNum() + 1)
                            .append(": Thể loại không hợp lệ (phải là số).\n");
                    errorCount++;
                    if (errorCount >= 20) break; // Dừng lại khi đã đủ 20 lỗi
                    continue;
                }

                // Kiểm tra và chuyển đổi status
                int statusInt;
                try {
                    statusInt = (int) statusCell.getNumericCellValue();
                } catch (Exception e) {
                    errorMessages.append("Dòng ").append(row.getRowNum() + 1)
                            .append(": Trạng thái không hợp lệ (phải là 0 hoặc 1).\n");
                    errorCount++;
                    if (errorCount >= 20) break; // Dừng lại khi đã đủ 20 lỗi
                    continue;
                }

                if (name.isEmpty()) {
                    errorMessages.append("Dòng ").append(row.getRowNum() + 1)
                            .append(": Tên sản phẩm không được để trống.\n");
                    errorCount++;
                    if (errorCount >= 20) break; // Dừng lại khi đã đủ 20 lỗi
                    continue;
                }

                if (categoryId < 0 || !AvailableUtils.getInstance().isValidCategory(categoryId)) {
                    errorMessages.append("Dòng ").append(row.getRowNum() + 1)
                            .append(": Thể loại không hợp lệ hoặc đã bị xóa.\n");
                    errorCount++;
                    if (errorCount >= 20) break; // Dừng lại khi đã đủ 20 lỗi
                    continue;
                }

                if (statusInt != 0 && statusInt != 1) {
                    errorMessages.append("Dòng ").append(row.getRowNum() + 1)
                            .append(": Trạng thái chỉ được là 0 hoặc 1.\n");
                    errorCount++;
                    if (errorCount >= 20) break; // Dừng lại khi đã đủ 20 lỗi
                    continue;
                }

                ProductDTO product = new ProductDTO(
                        null, name, 0, null, statusInt == 1, description, null, categoryId
                );
                tempList.add(product);

            } catch (Exception e) {
                errorMessages.append("Dòng ").append(row.getRowNum() + 1)
                        .append(": Lỗi không xác định: ").append(e.getMessage()).append("\n");
                errorCount++;
                if (errorCount >= 20) break; // Dừng lại khi đã đủ 20 lỗi
            }
        }

        if (errorMessages.length() > 0) {
            // Nếu có hơn 20 lỗi, chỉ hiển thị 20 dòng đầu tiên
            if (errorCount > 20) {
                errorMessages.append("Và " + (errorCount - 20) + " lỗi khác không thể hiển thị.\n");
            }
            NotificationUtils.showErrorAlert(errorMessages.toString(), "Thông báo");
            return new ArrayList<>(); // Trả về rỗng nếu có lỗi
        }

        list.addAll(tempList);
        return list;
    }



}
