
package SERVICE;

import BUS.EmployeeBUS;
import BUS.ProductBUS;
import BUS.RoleBUS;
import DTO.EmployeeDTO;
import DTO.ProductDTO;
import UTILS.ValidationUtils;
import impl.org.controlsfx.tableview2.RowHeader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        // Tߦ�o t+�n file
        String fileName = exportData.toLowerCase() + ".xlsx";
        File file = new File(fileName);

        // Ghi v+� -�+�ng file Excel
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } finally {
            workbook.close(); // -�ߦ�m bߦ�o workbook lu+�n -榦�+�c -�+�ng
        }

        // M�+� file sau khi -�+� -�+�ng ho+�n to+�n
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
        rowHeader.createCell(3).setCellValue("Salary");
        rowHeader.createCell(4).setCellValue("Date Of Birth");
        rowHeader.createCell(5).setCellValue("Role ID");

        for (EmployeeDTO employee : employeeDTOList) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(employee.getId());
            dataRow.createCell(1).setCellValue(employee.getFirstName());
            dataRow.createCell(2).setCellValue(employee.getLastName());
            dataRow.createCell(3).setCellValue(employee.getSalary().toString());
            dataRow.createCell(4).setCellValue(ValidationUtils.getInstance().formatDateTime(employee.getDateOfBirth()));
            dataRow.createCell(5).setCellValue(employee.getRoleId());
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

//    IMPORT EXCEL
    private void ImportSheet(String importData) throws IOException {
        String fileName = importData.toLowerCase() + ".xlsx";
        File file = new File(fileName);
        if (!file.exists()) return;
        //file input
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            //create sheets
            Sheet sheet = workbook.getSheetAt(0);
            //switch case employees or products
            if (importData.equalsIgnoreCase("product"))
                importToProducts(sheet);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi import file Excel: " + e.getMessage(), e);
        }
    }
//    import to ImportData
    private void importToProducts(Sheet sheet) {
        ArrayList<ProductDTO> list = returnListProduct(sheet, new ArrayList<>());
        //check empty
        if (list.isEmpty()) {
            System.out.println("Error size of list");
            return;
        }

        //check local
        for (ProductDTO product : list)
                // validate before insert
            if (validateForProduct(product))
                ProductBUS.getInstance().insert(product, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());
        return;
    }

//    validate after get list import products
    private boolean validateForProduct(ProductDTO product) {
        ProductBUS validate = ProductBUS.getInstance();
        validate.getAllLocal();
        // check valid product
        return validate.isValidProductInput(product) && !validate.isDuplicateProduct(product);
    }

//    VALIDATE ON PRODUCT BUS
    private ArrayList<ProductDTO> returnListProduct(Sheet sheet, ArrayList<ProductDTO> list) {
        for (Row row : sheet) {
            if (row.getRowNum() == 0)
                continue;
            if (list == null)
                return new ArrayList<>();
            if (!list.isEmpty())
                list.clear();

            // get instance of product bus
            ValidationUtils validate = ValidationUtils.getInstance();
            String id = row.getCell(0).getStringCellValue();
            String name = row.getCell(1).getStringCellValue();
            String description = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
            String imageUrl = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;
            BigDecimal sellingPrice = validate.canParseToBigDecimal(row.getCell(3).getStringCellValue());
            int stockQuantity = validate.canParseToInt(row.getCell(2).getStringCellValue());
            int statusInt = validate.canParseToInt(row.getCell(4).getStringCellValue());
            int categoryId = validate.canParseToInt(row.getCell(7).getStringCellValue());

            if (stockQuantity == -1 || statusInt == -1 || categoryId == -1) {
                System.out.println("Lỗi định dạng số ở stock, status hoặc category.");
                return new ArrayList<>();
            }
            if (sellingPrice.equals(BigDecimal.valueOf(-1))) {
                System.out.println("Lỗi giá bán.");
                return new ArrayList<>();
            }
            ProductDTO product = new ProductDTO(id, name, stockQuantity, sellingPrice,statusInt != 0, description, imageUrl, categoryId);
            list.add(product);
        }
        return list;
    }
}
