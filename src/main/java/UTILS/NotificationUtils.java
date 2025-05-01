package UTILS;

import DTO.TempDetailImportDTO;
import DTO.TempDetailInvoiceDTO;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.Optional;

public class NotificationUtils {
    public static void showErrorAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfoAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static <T> boolean showConfirmAlert(String message, ArrayList<T> list, String title, String extraFooter) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);

        StringBuilder content = new StringBuilder();
        ValidationUtils validate = ValidationUtils.getInstance();

        for (T obj : list) {
            if (obj instanceof TempDetailImportDTO item) {
                content.append("- ")
                        .append(item.getName())
                        .append(" | SL: ").append(item.getQuantity())
                        .append(" | Đơn giá: ").append(validate.formatCurrency(item.getPrice())).append(" Đ")
                        .append("\n");
            } else if (obj instanceof TempDetailInvoiceDTO item) {
                content.append("- ")
                        .append(item.getName())
                        .append(" | SL: ").append(item.getQuantity())
                        .append(" | Đơn giá: ").append(validate.formatCurrency(item.getPrice())).append(" Đ")
                        .append("\n");
            } else {
                content.append("- Không xác định: ").append(obj.toString()).append("\n");
            }
        }

        if (extraFooter != null && !extraFooter.isEmpty()) {
            content.append("\n").append(extraFooter);
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        alert.getDialogPane().setContent(textArea);

        ButtonType okButton = new ButtonType("Đồng ý", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Huỷ", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }
}
