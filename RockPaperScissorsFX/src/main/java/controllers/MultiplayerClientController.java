package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean gameActive = true;

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
                String hostName = (String) input.readObject();

                Platform.runLater(() -> {
                    statusLabel.setText("Connected to " + hostName);
                    gameControls.setVisible(true);
                    logMessage("Connected to host: " + hostName);
                });

                // Game loop
                while (gameActive) {
                    Object received = input.readObject();
                    if (received instanceof String) {
                        String message = (String) received;
                        if (message.equals("Rock") || message.equals("Paper") || message.equals("Scissors")) {
                            Platform.runLater(() -> {
                                logMessage(hostName + " has chosen. Your turn!");
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
                Platform.runLater(() -> logMessage("Connection error: " + e.getMessage()));
            } finally {
                closeConnections();
            }
        }).start();
    }

    private void makeMove(String move) {
        try {
            output.writeObject(move);
            output.flush();
            Platform.runLater(() -> {
                gameControls.setVisible(false);
                logMessage("You chose: " + move);
            });
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
    private void handleDisconnect() {
        gameActive = false;
        closeConnections();
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private void closeConnections() {
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Platform.runLater(() -> logArea.appendText("[" + timestamp + "] " + message + "\n"));
    }
}