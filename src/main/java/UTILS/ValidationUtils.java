
package UTILS;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

public class ValidationUtils {
    private static final ValidationUtils INSTANCE = new ValidationUtils();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_TIME_WITH_HOUR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final Pattern VIETNAMESE_TEXT_PATTERN = Pattern.compile("^[\\p{L}\\d_\\-./]+(\\s[\\p{L}\\d_\\-./]+)*$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern VIETNAMESE_PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    private ValidationUtils() {}

    public static ValidationUtils getInstance() {
        return INSTANCE;
    }

    public boolean validateStringLength(String value, int maxLength) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.length() <= maxLength;
    }

    public boolean validateBigDecimal(BigDecimal value, int precision, int scale, boolean allowNegative) {
        if (value == null) return false;
        if (!allowNegative && value.compareTo(BigDecimal.ZERO) < 0) return false;
        value = value.stripTrailingZeros();
        return value.scale() <= scale && value.precision() - value.scale() <= precision - scale;
    }

    public boolean validateSalary(BigDecimal value, int precision, int scale, boolean allowNegative) {
        if (value == null) return false;
        if (!allowNegative && value.compareTo(BigDecimal.ZERO) <= 0) return false;
        value = value.stripTrailingZeros();
        return value.scale() <= scale && value.precision() - value.scale() <= precision - scale;
    }

    public boolean validateVietnameseText(String value, int maxLength) {
        if (value == null) return false;
        return validateStringLength(value, maxLength) && VIETNAMESE_TEXT_PATTERN.matcher(value.trim()).matches();
    }

    public boolean validateVietnameseText50(String value) {
        return validateVietnameseText(value, 50);
    }

    public boolean validateVietnameseText100(String value) {
        return validateVietnameseText(value, 100);
    }

    public boolean validateVietnameseText255(String value) {
        return validateVietnameseText(value, 255);
    }
    public boolean validateVietnameseText65k4(String value) {
        return validateVietnameseText(value, 65400);
    }


    public boolean validatePassword(String password, int minLength, int maxLength) {
        if (password == null) return false;
        return password.length() >= minLength
                && password.length() <= maxLength
                && !password.contains(" "); // Ch�+� cߦ�n kh+�ng ch�+�a khoߦ�ng trߦ�ng, c+�n lߦ�i cho ph+�p tߦ�t cߦ�.
    }

    public boolean validateUsername(String username, int minLength, int maxLength) {
        if (username == null) return false;
        return username.length() >= minLength
                && username.length() <= maxLength
                && USERNAME_PATTERN.matcher(username).matches();
    }

    public boolean validateVietnamesePhoneNumber(String phoneNumber) {
        if (phoneNumber == null  || phoneNumber.trim().isEmpty()) return false;
        return VIETNAMESE_PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    public String formatCurrency(BigDecimal price) {
        if (price == null) return "0";
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
        return currencyFormat.format(price);
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public String formatDateTimeWithHour(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_WITH_HOUR_FORMATTER) : "";
    }

    public boolean validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return false; // Ng+�y sinh kh+�ng -榦�+�c l+� null
        LocalDate today = LocalDate.now();
        return dateOfBirth.isBefore(today); // Ng+�y sinh phߦ�i tr���+�c ng+�y h+�m nay
    }

    public boolean validateDateOfBirth(LocalDateTime dateOfBirth) {
        if (dateOfBirth == null) return false; // Ng+�y sinh kh+�ng -榦�+�c l+� null
        LocalDate today = LocalDate.now();
        return dateOfBirth.toLocalDate().isBefore(today); // Ng+�y sinh phߦ�i tr���+�c ng+�y h+�m nay
    }

}