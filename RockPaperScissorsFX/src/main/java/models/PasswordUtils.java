package models;

public class PasswordUtils {
    // No hashing, just plain text pass-through
    public static String hashPassword(String password, String salt) {
        return password;  // ignore salt, just return password
    }

    public static boolean verifyPassword(String password, String salt, String storedHash) {
        return password.equals(storedHash);
    }
}
