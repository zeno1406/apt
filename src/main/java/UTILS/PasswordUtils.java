package UTILS;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    private static final PasswordUtils INSTANCE = new PasswordUtils();
    private PasswordUtils() {

    }
    public static PasswordUtils getInstance() {
        return INSTANCE;
    }
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Kiểm tra mật khẩu nhập vào với mật khẩu đã băm
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
