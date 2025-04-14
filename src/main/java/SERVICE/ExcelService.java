
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

//    IMPORT EXCEL
    public void ImportSheet(String importData) throws IOException {
        String fileName = importData.toLowerCase() + ".xlsx";
        File file = new File(fileName);
        if (!file.exists()) return;
        //file input
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            //create sheets
            Sheet sheet = workbook.getSheetAt(0);
            //switch case employees or products
            if (importData.equalsIgnoreCase("products"))
                importToProducts(sheet);
        } catch (Exception e) {
            System.out.println(("Lỗi khi import file Excel: " + e.getMessage()));
        }
    }

    private void importToProducts(Sheet sheet) {
        ArrayList<ProductDTO> list = returnListProduct(sheet, new ArrayList<>());
        //check empty
        if (list.isEmpty()) {
            System.out.println("Error size of list");
            return;
        }
        //check local
        if(UiUtils.gI().showConfirmAlert("Are you sure babe?", "Cáo phó"))
            for (ProductDTO product : list)
                // validate before insert
                if (validateForProduct(product)) {
                    //  ProductBUS.getInstance().insert(product, SessionManagerService.getInstance().employeeRoleId(), SessionManagerService.getInstance().employeeLoginId());

                    System.out.println(product);
                }
    }

//  validate for importing products
    private boolean validateForImportingProduct(ProductDTO product) {

        return false;
    }

//    validate after get list import products
    private boolean validateForProduct(ProductDTO product) {
        ProductBUS validate = ProductBUS.getInstance();
        validate.getAllLocal();
        if (validate.isLocalEmpty())
            validate.getAll();
        if(validate.isLocalEmpty())
            return false;
        // check valid product
        return validate.isValidProductInput(product) && !validate.isDuplicateProduct(product);
    }

//    VALIDATE ON PRODUCT BUS
    private ArrayList<ProductDTO> returnListProduct(Sheet sheet, ArrayList<ProductDTO> list) {
        if (list == null)
            return new ArrayList<>();
        if (!list.isEmpty())
            list.clear();

        ValidationUtils validate = ValidationUtils.getInstance();
        for (Row row : sheet) {
            if (row.getRowNum() == 0)
                continue;

            // get instance of product bus
            int id = (int) row.getCell(0).getNumericCellValue();
            String name = row.getCell(1).getStringCellValue();
            String imageUrl = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;
            String description = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
            int categoryId = (int) row.getCell(7).getNumericCellValue();
            BigDecimal sellingPrice = validate.canParseToBigDecimal(String.valueOf(row.getCell(3).getNumericCellValue()));
            int stockQuantity = (int) row.getCell(2).getNumericCellValue();
            int statusInt = (int) row.getCell(4).getNumericCellValue();

            if (stockQuantity == -1 || statusInt == -1 || categoryId == -1) {
                System.out.println("Lỗi định dạng số ở stock, status hoặc category.");
                return new ArrayList<>();
            }
            if (sellingPrice.equals(BigDecimal.valueOf(-1))) {
                System.out.println("Lỗi giá bán.");
                return new ArrayList<>();
            }
            ProductDTO product = new ProductDTO(null, name, stockQuantity, sellingPrice,statusInt != 0, description, null, categoryId);
            list.add(product);
        }
        return list;
    }
}
