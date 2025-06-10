package models;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Platform;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL
            )
        """;

        String createLeaderboardTable = """
            CREATE TABLE IF NOT EXISTS leaderboard (
                user_id INT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                total_games INT DEFAULT 0,
                wins INT DEFAULT 0,
                win_percentage FLOAT DEFAULT 0,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            )
        """;

        String createMatchHistoryTable = """
            CREATE TABLE IF NOT EXISTS match_history (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player1 VARCHAR(50) NOT NULL,
                player2 VARCHAR(50) NOT NULL,
                player1_move VARCHAR(10) NOT NULL,
                player2_move VARCHAR(10) NOT NULL,
                winner VARCHAR(50),
                match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createPlayerStatsView = """
            CREATE OR REPLACE VIEW player_stats AS
            SELECT 
                username,
                total_games,
                wins,
                win_percentage,
                RANK() OVER (ORDER BY win_percentage DESC, wins DESC) AS rank
            FROM leaderboard
        """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            if (conn == null) {
                Platform.runLater(() -> {
                    Utils.showError("Database Error", "Could not connect to the database. Please check your internet or configuration.");
                    System.exit(1);
                });
                return;
            }

            System.out.println("Database connected successfully.");

            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createLeaderboardTable);
            stmt.executeUpdate(createMatchHistoryTable);

            System.out.println("Database tables initialized successfully.");

            try {
                stmt.executeUpdate(createPlayerStatsView);
            } catch (SQLException e) {
//                System.out.println("View creation skipped: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database:");
            e.printStackTrace();
        }
    }
}
