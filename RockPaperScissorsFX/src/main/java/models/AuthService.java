package models;

import java.sql.*;

public class AuthService {

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    String initSql = "INSERT INTO leaderboard (user_id, username) VALUES (?, ?)";
                    try (PreparedStatement initStmt = conn.prepareStatement(initSql)) {
                        initStmt.setInt(1, userId);
                        initStmt.setString(2, username.trim());
                        initStmt.executeUpdate();
                    }
                }
            }

            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Username already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
//                System.out.println("Entered: " + password);
//                System.out.println("Stored : " + storedPassword);
                return storedPassword.equals(password);
            } else {
                System.out.println("Username not found: " + username);
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

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("user_id") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
