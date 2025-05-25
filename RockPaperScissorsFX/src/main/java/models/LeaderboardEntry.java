package models;

public class LeaderboardEntry {
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
    
    // Getters
    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getTotalGames() { return totalGames; }
    public int getWins() { return wins; }
    public double getWinPercentage() { return winPercentage; }
    
    // Format win percentage for display
    public String getWinPercentageFormatted() {
        return String.format("%.1f%%", winPercentage);
    }
}