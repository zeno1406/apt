
package SERVICE;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import DTO.RoleDTO;
import UTILS.NotificationUtils;
import UTILS.UiUtils;
import UTILS.ValidationUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

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
}