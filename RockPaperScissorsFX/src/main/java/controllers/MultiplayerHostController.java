package controllers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiplayerHostController {
    @FXML private Label statusLabel;
    @FXML private VBox gameControls;
    @FXML private TextArea logArea;
    
    private String username;
    private String playerMove;
    private String opponentMove;
    private String opponentName;
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
    public void setUsername(String username) {
        this.username = username;
        startServer();
    }
    
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5555);
                Platform.runLater(() -> {
                    statusLabel.setText("Waiting for player to connect on port 5555...");
                    logMessage("Server started. Waiting for connection...");
                });
                
                clientSocket = serverSocket.accept();
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                input = new ObjectInputStream(clientSocket.getInputStream());
                
                // Exchange usernames
                output.writeObject(username);
                output.flush();
                opponentName = (String) input.readObject();
                
                Platform.runLater(() -> {
                    statusLabel.setText("Connected to " + opponentName);
                    gameControls.setVisible(true);
                    logMessage("Connected to " + opponentName);
                });
                
                // Listen for opponent's move
                new Thread(this::listenForOpponentMove).start();
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    logMessage("Error in server: " + e.getMessage());
                    statusLabel.setText("Connection error");
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void listenForOpponentMove() {
        try {
            opponentMove = (String) input.readObject();
            Platform.runLater(() -> {
                logMessage(opponentName + " has made their move");
                determineWinner();
            });
        } catch (Exception e) {
            Platform.runLater(() -> logMessage("Error receiving opponent's move: " + e.getMessage()));
            e.printStackTrace();
        }
    }
    
    private void determineWinner() {
        String result;
        String winner = "Draw";
        
        if (playerMove.equals(opponentMove)) {
            result = "It's a draw! Both chose " + playerMove;
        } else if ((playerMove.equals("Rock") && opponentMove.equals("Scissors")) ||
                  (playerMove.equals("Paper") && opponentMove.equals("Rock")) ||
                  (playerMove.equals("Scissors") && opponentMove.equals("Paper"))) {
            result = username + " wins! " + playerMove + " beats " + opponentMove;
            winner = username;
        } else {
            result = opponentName + " wins! " + opponentMove + " beats " + playerMove;
            winner = opponentName;
        }
        
        logMessage(result);
        
        // Send result to client
        try {
            output.writeObject(result);
            output.flush();
        } catch (IOException e) {
            logMessage("Error sending result: " + e.getMessage());
        }
        
        // Save to database if not a draw
        if (!winner.equals("Draw")) {
            saveMatchResult(winner);
        }
        
        // Reset for next round
        playerMove = null;
        opponentMove = null;
        Platform.runLater(() -> gameControls.setVisible(true));
    }
    
    private void saveMatchResult(String winner) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO match_history (player1, player2, player1_move, player2_move, winner) " +
                         "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, opponentName);
            stmt.setString(3, playerMove);
            stmt.setString(4, opponentMove);
            stmt.setString(5, winner);
            stmt.executeUpdate();
            
            // Update leaderboard for both players
            updateLeaderboard(username, winner.equals(username));
            updateLeaderboard(opponentName, winner.equals(opponentName));
            
        } catch (Exception e) {
            logMessage("Error saving match result: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateLeaderboard(String player, boolean won) {
        try (Connection conn = getConnection()) {
            // Check if player exists in leaderboard
            String checkSql = "SELECT username FROM leaderboard WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, player);
            boolean exists = checkStmt.executeQuery().next();
            
            if (exists) {
                String updateSql = "UPDATE leaderboard SET total_games = total_games + 1, " +
                                  "wins = wins + ?, win_percentage = (wins + ?) / (total_games + 1) * 100 " +
                                  "WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, won ? 1 : 0);
                updateStmt.setInt(2, won ? 1 : 0);
                updateStmt.setString(3, player);
                updateStmt.executeUpdate();
            } else {
                String insertSql = "INSERT INTO leaderboard (username, total_games, wins, win_percentage) " +
                                  "VALUES (?, 1, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, player);
                insertStmt.setInt(2, won ? 1 : 0);
                insertStmt.setDouble(3, won ? 100 : 0);
                insertStmt.executeUpdate();
            }
        } catch (Exception e) {
            logMessage("Error updating leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void makeMove(String move) {
        playerMove = move;
        gameControls.setVisible(false);
        logMessage("You chose: " + move);
        
        try {
            output.writeObject(move);
            output.flush();
        } catch (IOException e) {
            logMessage("Error sending your move: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRock() {
        makeMove("Rock");
    }
    
    @FXML
    private void handlePaper() {
        makeMove("Paper");
    }
    
    @FXML
    private void handleScissors() {
        makeMove("Scissors");
    }
    
    @FXML
    private void handleCloseGame() {
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
    
    private void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Platform.runLater(() -> logArea.appendText("[" + timestamp + "] " + message + "\n"));
    }
    
    private Connection getConnection() throws Exception {
        // Replace with your actual database connection details
        String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12781074";
        String user = "sql12781074";
        String password = "ym3wh2nlkJ";
        return DriverManager.getConnection(url, user, password);
    }
}