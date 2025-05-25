package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchHistoryEntry {
    private final String player1;
    private final String player2;
    private final String player1Move;
    private final String player2Move;
    private final String winner;
    private final String matchDate;

    public MatchHistoryEntry(String player1, String player2, String player1Move,
                             String player2Move, String winner, String matchDate) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Move = player1Move;
        this.player2Move = player2Move;
        this.winner = winner;
        this.matchDate = matchDate;
    }

    // Getters
    public String getPlayer1() { return player1; }
    public String getPlayer2() { return player2; }
    public String getPlayer1Move() { return player1Move; }
    public String getPlayer2Move() { return player2Move; }
    public String getWinner() { return winner; }
    public String getMatchDate() { return matchDate; }

    // Formatted date for display
    public String getFormattedDate() {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(matchDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        } catch (Exception e) {
            return matchDate;
        }
    }
}