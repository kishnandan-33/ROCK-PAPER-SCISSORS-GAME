package models;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        String createLeaderboardTable = """
            CREATE TABLE IF NOT EXISTS leaderboard (
                username VARCHAR(50) PRIMARY KEY,
                total_games INT DEFAULT 0,
                wins INT DEFAULT 0,
                win_percentage FLOAT DEFAULT 0,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
                match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_player1 (player1),
                INDEX idx_player2 (player2),
                INDEX idx_winner (winner)
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

            // Create tables
            stmt.executeUpdate(createLeaderboardTable);
            stmt.executeUpdate(createMatchHistoryTable);
            System.out.println("Database tables initialized successfully");

            // Create view
            try {
                stmt.executeUpdate(createPlayerStatsView);
            } catch (SQLException e) {
                System.out.println("View already exists or couldn't be created: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database tables:");
            e.printStackTrace();
        }
    }
}