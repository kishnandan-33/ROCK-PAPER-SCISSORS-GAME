package controllers;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import models.DBUtil;
import java.net.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiplayerHostController {
    @FXML private Label statusLabel;
    @FXML private VBox gameControls;
    @FXML private TextArea logArea;
    @FXML private ImageView celebrationView;

    private AudioClip clickSound = new AudioClip(getClass().getResource("/sounds/click.wav").toString());
    private AudioClip clickSound2 = new AudioClip(getClass().getResource("/sounds/mclick.wav").toString());


    private String username;
    private String playerMove;
    private String opponentMove;
    private String opponentName;
    public String ip;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean gameActive = true;
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
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ip = Inet4Address.getLocalHost().getHostAddress();

                serverSocket = new ServerSocket(5555);
                Platform.runLater(() -> {
                    statusLabel.setText(String.format("Waiting for player on IP %s", ip));
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

                // Game loop
                while (gameActive) {
                    listenForOpponentMove();
                }

            } catch (Exception e) {
                Platform.runLater(() -> logMessage("Error: " + e.getMessage()));
            } finally {
                closeConnections();
            }
        }).start();
    }

    private void listenForOpponentMove() {
        try {
            opponentMove = (String) input.readObject();
            Platform.runLater(() -> {
                // logMessage(opponentName + " chose: " + opponentMove);
                determineWinner();
                prepareNextRound();
            });
        } catch (Exception e) {
            Platform.runLater(() -> logMessage(opponentName + " disconnected"));
            gameActive = false;
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
            showWinGif("win");
        } else {
            result = opponentName + " wins! " + opponentMove + " beats " + playerMove;
            winner = opponentName;
            showWinGif("loss");
        }

        logMessage(result);
        sendResult(result);

        if (!winner.equals("Draw")) {
            saveMatchResult(winner);
        }
    }

    private void prepareNextRound() {
        playerMove = null;
        opponentMove = null;
        Platform.runLater(() -> {
            gameControls.setVisible(true);
            logMessage("New round started - make your move!");
        });
    }

    private void sendResult(String result) {
        try {
            output.writeObject(result);
            output.flush();
        } catch (IOException e) {
            logMessage("Error sending result: " + e.getMessage());
        }
    }

    private void saveMatchResult(String winner) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO match_history (player1, player2, player1_move, player2_move, winner) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, opponentName);
            stmt.setString(3, playerMove);
            stmt.setString(4, opponentMove);
            stmt.setString(5, winner);
            stmt.executeUpdate();
        } catch (Exception e) {
            logMessage("Error saving match: " + e.getMessage());
        }
    }

    private void makeMove(String move) {
        clickSound.play();
        playerMove = move;
        Platform.runLater(() -> {
            gameControls.setVisible(false);
            logMessage("You chose: " + move);
        });

        try {
            output.writeObject(move);
            output.flush();
        } catch (IOException e) {
            logMessage("Error sending move: " + e.getMessage());
        }
    }

    @FXML
    private void handleRock() { makeMove("Rock"); }

    @FXML
    private void handlePaper() { makeMove("Paper"); }

    @FXML
    private void handleScissors() { makeMove("Scissors"); }

    @FXML
    private void handleCloseGame() {
        clickSound2.play();
        gameActive = false;
        closeConnections();
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private void closeConnections() {
        clickSound2.play();
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Platform.runLater(() -> logArea.appendText("[" + timestamp + "] " + message + "\n"));
    }
}