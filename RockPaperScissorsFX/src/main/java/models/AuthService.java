package models;
import java.sql.*;
public class AuthService {
    public static boolean registerUser(String username, String password) {
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(password, salt);

        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            // Initialize leaderboard entry
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    String initSql = "INSERT INTO leaderboard (user_id, username) VALUES (?, ?)";
                    try (PreparedStatement initStmt = conn.prepareStatement(initSql)) {
                        initStmt.setInt(1, userId);
                        initStmt.setString(2, username);
                        initStmt.executeUpdate();
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash, salt FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                return PasswordUtils.verifyPassword(password, salt, storedHash);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserId(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("user_id") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}