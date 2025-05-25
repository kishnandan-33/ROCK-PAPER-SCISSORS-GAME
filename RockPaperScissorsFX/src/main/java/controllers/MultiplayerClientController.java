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
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiplayerClientController {
    @FXML private Label statusLabel;
    @FXML private VBox gameControls;
    @FXML private TextArea logArea;
    
    private String username;
    private String hostIp;
    private String playerMove;
    private String opponentName;
    
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
        connectToHost();
    }
    
    private void connectToHost() {
        new Thread(() -> {
            try {
                socket = new Socket(hostIp, 5555);
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                
                // Exchange usernames
                output.writeObject(username);
                output.flush();
                opponentName = (String) input.readObject();
                
                Platform.runLater(() -> {
                    statusLabel.setText("Connected to " + opponentName);
                    gameControls.setVisible(true);
                    logMessage("Connected to " + opponentName);
                });
                
                // Listen for game updates
                while (true) {
                    Object received = input.readObject();
                    if (received instanceof String) {
                        String message = (String) received;
                        if (message.equals("Rock") || message.equals("Paper") || message.equals("Scissors")) {
                            // Opponent made their move
                            Platform.runLater(() -> {
                                logMessage(opponentName + " has made their move. Your turn!");
                                gameControls.setVisible(true);
                            });
                        } else {
                            // Game result
                            Platform.runLater(() -> {
                                logMessage(message);
                                gameControls.setVisible(true);
                            });
                        }
                    }
                }
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    logMessage("Error in client: " + e.getMessage());
                    statusLabel.setText("Connection error");
                });
                e.printStackTrace();
            }
        }).start();
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
    private void handleDisconnect() {
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null) socket.close();
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
}