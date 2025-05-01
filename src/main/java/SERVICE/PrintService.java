package SERVICE;

import BUS.*;
import DTO.*;
import UTILS.ValidationUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Desktop;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PrintService {
    private static final PrintService INSTANCE = new PrintService();
    private PrintService() {

    }

    public static PrintService getInstance() {
        return INSTANCE;
    }

    public void printInvoiceForm(int invoiceId) {
        InvoiceBUS invBus = InvoiceBUS.getInstance();
        if (invBus.isLocalEmpty()) invBus.loadLocal();
        InvoiceDTO invoice = invBus.getByIdLocal(invoiceId);
        if (invoice == null) return;

        DetailInvoiceBUS dinvBus = DetailInvoiceBUS.getInstance();
        if (dinvBus.isLocalEmpty()) dinvBus.loadLocal();
        ArrayList<DetailInvoiceDTO> arrDetailInvoice = dinvBus.getAllDetailInvoiceByInvoiceIdLocal(invoiceId);
        if (arrDetailInvoice.isEmpty()) return;

        ProductBUS proBus = ProductBUS.getInstance();
        if (proBus.isLocalEmpty()) proBus.loadLocal();

        EmployeeBUS emBus = EmployeeBUS.getInstance();
        if (emBus.isLocalEmpty()) emBus.loadLocal();
        EmployeeDTO employee = emBus.getByIdLocal(invoice.getEmployeeId());

        CustomerBUS cusBus = CustomerBUS.getInstance();
        if (cusBus.isLocalEmpty()) cusBus.loadLocal();
        CustomerDTO customer = cusBus.getByIdLocal(invoice.getCustomerId());

        String dest = "invoice.pdf";
        String fontPath = "src/main/resources/fonts/arial-unicode-ms.ttf"; // Đường dẫn tương đối
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            // Nhúng font Arial Unicode MS để hỗ trợ tiếng Việt
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            Font titleFont = new Font(baseFont, 14, Font.BOLD);
            Font italicFont = new Font(baseFont, 12, Font.ITALIC);

            // Tiêu đề
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // Lấy thông tin khách hàng và người lập đơn
            String customerName = customer.getFirstName() + " " + customer.getLastName();
            String employeeName = employee.getFirstName() + " " + employee.getLastName();
            String invoiceDate = invoice.getCreateDate().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));

            Phrase discountPhrase = invoice.getDiscountCode() != null
                    ? new Phrase(invoice.getDiscountCode(), normalFont)
                    : new Phrase("Không có", italicFont);

            // Thông tin hóa đơn
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{25, 25, 25, 25});

            String[][] invoiceInfo = {
                    {"Mã hóa đơn:", String.valueOf(invoiceId), "Người lập đơn:", employeeName},
                    {"Ngày tạo:", invoiceDate, "Tên khách hàng:", customerName},
            };

            // Thêm dữ liệu bình thường
            for (String[] row : invoiceInfo) {
                for (String cell : row) {
                    PdfPCell pdfCell = new PdfPCell(new Phrase(cell, cell.endsWith(":") ? boldFont : normalFont));
                    pdfCell.setBorder(Rectangle.NO_BORDER);
                    pdfCell.setPadding(5);
                    infoTable.addCell(pdfCell);
                }
            }

            // Hàng "Khuyến mãi" (Merge 2 cột phải)
            PdfPCell promoLabelCell = new PdfPCell(new Phrase("Khuyến mãi:", boldFont));
            promoLabelCell.setBorder(Rectangle.NO_BORDER);
            promoLabelCell.setPadding(5);
            infoTable.addCell(promoLabelCell);

            PdfPCell promoValueCell = new PdfPCell(discountPhrase);
            promoValueCell.setBorder(Rectangle.NO_BORDER);
            promoValueCell.setPadding(5);
            promoValueCell.setColspan(3); // Merge 3 ô còn lại
            infoTable.addCell(promoValueCell);

            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // Bảng sản phẩm
            int index = 1;
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            String[] headers = {"STT", "TÊN SẢN PHẨM", "SỐ LƯỢNG", "ĐƠN GIÁ", "THÀNH TIỀN"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, boldFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(5);
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(headerCell);
            }

            for (DetailInvoiceDTO detail : arrDetailInvoice) {
                int quantity = detail.getQuantity(); // Số lượng sản phẩm
                BigDecimal price = detail.getPrice(); // Giá của một sản phẩm
                BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity)); // Tổng tiền của sản phẩm này
                String productName = proBus.getByIdLocal(detail.getProductId()).getName();

                // Tạo các ô của bảng
                PdfPCell[] cells = {
                        new PdfPCell(new Phrase(String.valueOf(index++), normalFont)),
                        new PdfPCell(new Phrase(productName, normalFont)),
                        new PdfPCell(new Phrase(String.valueOf(quantity), normalFont)),
                        new PdfPCell(new Phrase(ValidationUtils.getInstance().formatCurrency(price), normalFont)),
                        new PdfPCell(new Phrase(ValidationUtils.getInstance().formatCurrency(totalPrice), normalFont))
                };

                // Đặt căn giữa và chiều cao tối thiểu cho tất cả ô
                for (PdfPCell cell : cells) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setMinimumHeight(25f);
                    table.addCell(cell);
                }

            }
            document.add(table);

            document.add(new Paragraph("\n"));

            // Tổng kết hóa đơn căn phải
            PdfPTable summaryWrapper = new PdfPTable(2);
            summaryWrapper.setWidthPercentage(100);
            summaryWrapper.setWidths(new float[]{50, 50}); // Cột đầu chiếm 50%, cột sau chứa bảng tổng kết

            // Cột trống bên trái
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            summaryWrapper.addCell(emptyCell);

            // Bảng tổng kết hóa đơn
            PdfPTable summaryTable = new PdfPTable(3);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{2f, 1f, 1f});
            String[][] summaryData = {
                    {"Tổng tiền:", ValidationUtils.getInstance().formatCurrency(invoice.getTotalPrice()), "đ"},
                    {"Số tiền giảm:", ValidationUtils.getInstance().formatCurrency(invoice.getDiscountAmount()), "đ"},
                    {"Thành tiền:", ValidationUtils.getInstance().formatCurrency(
                            invoice.getTotalPrice().subtract(invoice.getDiscountAmount()).max(BigDecimal.ZERO)), "đ"}
            };
            for (String[] row : summaryData) {
                PdfPCell labelCell = new PdfPCell(new Phrase(row[0], boldFont));
                labelCell.setBorder(Rectangle.NO_BORDER);
                labelCell.setPadding(5);
                summaryTable.addCell(labelCell);

                PdfPCell valueCell = new PdfPCell(new Phrase(row[1], normalFont));
                valueCell.setBorder(Rectangle.NO_BORDER);
                valueCell.setPadding(5);
                summaryTable.addCell(valueCell);

                PdfPCell valueCell1 = new PdfPCell(new Phrase(row[2], normalFont));
                valueCell1.setBorder(Rectangle.NO_BORDER);
                valueCell1.setPadding(5);
                summaryTable.addCell(valueCell1);
            }

            // Cột chứa bảng tổng kết
            PdfPCell summaryCell = new PdfPCell(summaryTable);
            summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryCell.setBorder(Rectangle.NO_BORDER);
            summaryWrapper.addCell(summaryCell);

            document.add(summaryWrapper);

            document.close();
            System.out.println("Hóa đơn PDF đã được tạo thành công!");
            File file = new File(dest);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
