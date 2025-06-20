package controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import models.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LeaderboardController {
    @FXML private TableView<LeaderboardEntry> leaderboardTable;
    @FXML private TableColumn<LeaderboardEntry, Integer> rankColumn;
    @FXML private TableColumn<LeaderboardEntry, String> usernameColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> totalGamesColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> winsColumn;
    @FXML private TableColumn<LeaderboardEntry, Double> winPercentageColumn;
    private AudioClip clickSound2 = new AudioClip(getClass().getResource("/sounds/mclick.wav").toString());


    private ObservableList<LeaderboardEntry> leaderboardData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Set up the table columns
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        totalGamesColumn.setCellValueFactory(new PropertyValueFactory<>("totalGames"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        winPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("winPercentage"));
        
        leaderboardTable.setItems(leaderboardData);
    }
    
    public void loadLeaderboard() {
        leaderboardData.clear();
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT username, total_games, wins, win_percentage " +
                         "FROM leaderboard ORDER BY win_percentage DESC, wins DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            int rank = 1;
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                    rank++,
                    rs.getString("username"),
                    rs.getInt("total_games"),
                    rs.getInt("wins"),
                    rs.getDouble("win_percentage")
                );
                leaderboardData.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRefresh() {
        clickSound2.play();
        loadLeaderboard();
    }
    
    @FXML
    private void handleClose() {
        clickSound2.play();
        Stage stage = (Stage) leaderboardTable.getScene().getWindow();
        stage.close();
    }

    
    public static class LeaderboardEntry {
        private final int rank;
        private final String username;
        private final int totalGames;
        private final int wins;
        private final double winPercentage;
        
        public LeaderboardEntry(int rank, String username, int totalGames, int wins, double winPercentage) {
            this.rank = rank;
            this.username = username;
            this.totalGames = totalGames;
            this.wins = wins;
            this.winPercentage = winPercentage;
        }
        
        public int getRank() { return rank; }
        public String getUsername() { return username; }
        public int getTotalGames() { return totalGames; }
        public int getWins() { return wins; }
        public double getWinPercentage() { return winPercentage; }
    }
}