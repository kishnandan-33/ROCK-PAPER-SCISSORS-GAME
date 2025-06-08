package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import java.sql.Connection;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import models.DBUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BotGameController {
    @FXML private Label resultLabel;
    @FXML private Label computerChoiceLabel;
    @FXML private Label statsLabel;
    @FXML private ImageView celebrationView;


    private String username;
    private int playerWins = 0;
    private int computerWins = 0;
    private int totalGames = 0;

    private AudioClip clickSound = new AudioClip(getClass().getResource("/sounds/click.wav").toString());
    private AudioClip clickSound2 = new AudioClip(getClass().getResource("/sounds/mclick.wav").toString());
    public void showWinGif(String result) {
        // Set the GIF image
        if(result == "win"){
            Image winGif = new Image(getClass().getResource("/images/win.gif").toExternalForm());
            celebrationView.setImage(winGif);
            celebrationView.setVisible(true);
            // Hide it after 2.5 seconds
            AudioClip Sound = new AudioClip(getClass().getResource("/sounds/le.wav").toString());
            Sound.play();
            PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
            pause.setOnFinished(e -> celebrationView.setVisible(false));
            pause.play();
        }else{
            Image winGif = new Image(getClass().getResource("/images/loss.gif").toExternalForm());
            celebrationView.setImage(winGif);
            celebrationView.setVisible(true);
            // Hide it after 3 seconds
            AudioClip Sound = new AudioClip(getClass().getResource("/sounds/cry.wav").toString());
            Sound.play();
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(e -> celebrationView.setVisible(false));
            pause.play();
        }

    }



    public void setUsername(String username) {
        this.username = username;
        loadStats();
    }

    
    private void loadStats() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT total_games, wins FROM leaderboard WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalGames = rs.getInt("total_games");
                playerWins = rs.getInt("wins");
                computerWins = totalGames - playerWins;
                updateStatsLabel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateStatsLabel() {
        double winPercentage = totalGames > 0 ? (double) playerWins / totalGames * 100 : 0;
        statsLabel.setText(String.format("Wins: %d | Losses: %d | Total: %d | Win %%: %.1f", 
            playerWins, computerWins, totalGames, winPercentage));
    }

    private void updateLeaderboard(boolean playerWon) {
        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() {

                try (Connection conn = DBUtil.getConnection()) {
                    String checkSql = "SELECT username FROM leaderboard WHERE username = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        String updateSql = "UPDATE leaderboard SET total_games = total_games + 1, " +
                                "wins = wins + ?, win_percentage = ((wins + ?) * 1.0) / (total_games + 1) * 100 " +
                                "WHERE username = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setInt(1, playerWon ? 1 : 0);
                        updateStmt.setInt(2, playerWon ? 1 : 0);
                        updateStmt.setString(3, username);
                        updateStmt.executeUpdate();
                    } else {
                        String insertSql = "INSERT INTO leaderboard (username, total_games, wins, win_percentage) " +
                                "VALUES (?, 1, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                        insertStmt.setString(1, username);
                        insertStmt.setInt(2, playerWon ? 1 : 0);
                        insertStmt.setDouble(3, playerWon ? 100 : 0);
                        insertStmt.executeUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(updateTask).start();
    }


    private String getComputerChoice() {
        String[] choices = {"Rock", "Paper", "Scissors"};
        return choices[(int) (Math.random() * choices.length)];
    }
    
    private String determineWinner(String playerChoice, String computerChoice) {
        if (playerChoice.equals(computerChoice)) {
            return "Draw";
        }
        
        if ((playerChoice.equals("Rock") && computerChoice.equals("Scissors")) ||
            (playerChoice.equals("Paper") && computerChoice.equals("Rock")) ||
            (playerChoice.equals("Scissors") && computerChoice.equals("Paper"))) {
            playerWins++;
            showWinGif("win");
            return "You win!";
        } else {

            computerWins++;
            showWinGif("loss");
            return "Computer wins! You Loss!";
        }
    }
    
    private void playGame(String playerChoice) {
        clickSound.play();
        String computerChoice = getComputerChoice();
        String result = determineWinner(playerChoice, computerChoice);
        
        computerChoiceLabel.setText("Computer chose: " + computerChoice);
        resultLabel.setText(result);
        
        totalGames++;
        updateStatsLabel();
        
        // Update leaderboard only if not a draw
        if (!result.equals("Draw")) {
            updateLeaderboard(result.equals("You win!"));
        }
    }
    
    @FXML
    private void handleRock() {
        playGame("Rock");
    }
    
    @FXML
    private void handlePaper() {
        playGame("Paper");
    }
    
    @FXML
    private void handleScissors() {
        playGame("Scissors");
    }
    
    @FXML
    private void handleResetStats() {
        clickSound2.play();
        playerWins = 0;
        computerWins = 0;
        totalGames = 0;
        updateStatsLabel();
    }
    
    @FXML
    private void handleBackToMenu() {
        clickSound2.play();
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.close();
    }
}