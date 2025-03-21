package SERVICE;
import java.math.BigDecimal;

public class ValidationUtils {
    private static final ValidationUtils INSTANCE = new ValidationUtils();

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

    public boolean validateBigDecimal5_2(BigDecimal value) {
        return validateBigDecimal(value, 5, 2);
    }
}
