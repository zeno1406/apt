package SERVICE;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImageService {
    private static final ImageService INSTANCE = new ImageService();
    private ImageService() {};
    public static ImageService gI() {
        return INSTANCE;
    }

    public String saveProductImage(String productId, String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }

        if (imageUrl.startsWith("file:/")) {
            imageUrl = imageUrl.substring(6);  // Xóa tiền tố file:/ nếu có
        }

        // Kiểm tra sự tồn tại của tệp
        File sourceFile = new File(imageUrl);
        if (!sourceFile.exists()) {
            throw new IOException("Tệp nguồn không tồn tại: " + imageUrl);
        }

        String extension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();
        if (!extension.equals("png") && !extension.equals("jpg")) {
            throw new IllegalArgumentException("Chỉ hỗ trợ các định dạng ảnh PNG và JPG.");
        }

        String targetDirectory = "images/product/";
        File targetDirFile = new File(targetDirectory);
        if (!targetDirFile.exists()) {
            targetDirFile.mkdirs();
        }

        String tempFileName = productId + "_temp." + extension;
        File tempFile = new File(targetDirFile, tempFileName);

        // Tạo tệp tạm thời
        Files.copy(sourceFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Đọc ảnh từ file tạm thời và kiểm tra
        BufferedImage originalImage = ImageIO.read(tempFile);
        if (originalImage == null) {
            throw new IOException("Không thể đọc ảnh từ tệp: " + tempFile);
        }

        int targetSize = 400;
        BufferedImage resizedImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Tối ưu chất lượng ảnh khi resize
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Tính toán tỷ lệ co giãn cho ảnh để giữ nguyên tỷ lệ
        double scale = Math.min((double) targetSize / originalWidth, (double) targetSize / originalHeight);
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        // Tính vị trí để canh giữa ảnh
        int x = (targetSize - newWidth) / 2;
        int y = (targetSize - newHeight) / 2;

        // Vẽ ảnh đã resize và canh giữa
        g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);
        g2d.dispose();

        // Kiểm tra và xóa ảnh cũ
        new File(targetDirectory, productId + ".png").delete();
        new File(targetDirectory, productId + ".jpg").delete();

        // Lưu ảnh đã resize vào file chính
        String fileName = productId + "." + extension;
        File targetFile = new File(targetDirFile, fileName);
        ImageIO.write(resizedImage, extension, targetFile);

        // Xóa tệp tạm thời
        tempFile.delete();

        return "images/product/" + fileName;
    }




}
