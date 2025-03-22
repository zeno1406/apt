package UTILS;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class ValidationUtils {
    private static final ValidationUtils INSTANCE = new ValidationUtils();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ValidationUtils() {}

    // Get the singleton instance
    public static ValidationUtils getInstance() {
        return INSTANCE;
    }

    public boolean validateStringLength(String value, int maxLength) {
        if (value == null) return false;
        return value.length() <= maxLength;
    }

    public boolean validateBigDecimal(BigDecimal value, int precision, int scale) {
        if (value == null) return false;

        return value.scale() <= scale && value.precision() - value.scale() <= precision - scale;
    }

    public boolean validateLength50(String value) {
        return validateStringLength(value, 50);
    }

    public boolean validateLength100(String value) {
        return validateStringLength(value, 100);
    }

    public boolean validateLength255(String value) {
        return validateStringLength(value, 255);
    }

    public boolean validateBigDecimal10_2(BigDecimal value) {
        return validateBigDecimal(value, 10, 2);
    }

    public boolean validateBigDecimal20_2(BigDecimal value) {
        return validateBigDecimal(value, 20, 2);
    }

    public boolean validateDateTimeFormat(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return false;
        try {
            LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String formatCurrency(BigDecimal price) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(price).replace("₫", "");
    }
}
