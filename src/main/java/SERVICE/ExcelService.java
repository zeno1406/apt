package SERVICE;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import impl.org.controlsfx.tableview2.RowHeader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {
    private static final ExcelService INSTANCE = new ExcelService();
    private ExcelService() {}
    public static ExcelService getInstance() {
        return INSTANCE;
    }

    public void exportToFileExcel(String filePath, String exportData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Source");

        if (exportData.equalsIgnoreCase("employee"))
            sheet = sheetOfEmployee(sheet, (EmployeeBUS.getInstance().getAllLocal()));
        else if (exportData.equalsIgnoreCase("product"))
            sheet = sheetOfProduct(sheet, (ProductBUS.getInstance().getAllLocal()));

//        filePath = "src/main/resources/excel_files/ExportEmployees.xlsx";
        try (DataOutputStream file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))){
            workbook.write(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            workbook.close();
        }
    }

    private Sheet sheetOfEmployee(Sheet sheet, List<EmployeeDTO> employeeDTOList) {
        int rowIndex = 0;
        Row rowHeader = sheet.createRow(0);
        rowHeader.createCell(0).setCellValue("INT");
        rowHeader.createCell(1).setCellValue("First Name");
        rowHeader.createCell(2).setCellValue("Last Name");
        rowHeader.createCell(3).setCellValue("Salary");
        rowHeader.createCell(4).setCellValue("Date_Of_Birth");
        rowHeader.createCell(5).setCellValue("Role_ID");

        for (EmployeeDTO employeeDTO : employeeDTOList) {
            Row dataRow = sheet.createRow(++rowIndex);
            dataRow.createCell(0).setCellValue(employeeDTO.getId());
            dataRow.createCell(1).setCellValue(employeeDTO.getFirstName());
            dataRow.createCell(2).setCellValue(employeeDTO.getLastName());
            dataRow.createCell(3).setCellValue(employeeDTO.getSalary().toString());
            dataRow.createCell(4).setCellValue(employeeDTO.getDateOfBirth());
            dataRow.createCell(5).setCellValue(employeeDTO.getRoleId());
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
