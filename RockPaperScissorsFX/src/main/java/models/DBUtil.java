package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://avnadmin:AVNS_3kAGU-MvfBSQlb1LmjY@record-developapp007-06b2.j.aivencloud.com:23811/defaultdb?ssl-mode=REQUIRED";
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASSWORD = "AVNS_3kAGU-MvfBSQlb1LmjY";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}