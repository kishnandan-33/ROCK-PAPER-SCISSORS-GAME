package controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML private ImageView celebrationView;
    private AudioClip clickSound2 = new AudioClip(getClass().getResource("/sounds/mclick.wav").toString());
    private AudioClip clickSound = new AudioClip(getClass().getResource("/sounds/click.wav").toString());

    private String username;
    private String hostIp;
    private Socket socket;
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
                                if (message.contains(username)){
                                    showWinGif("win");
                                }else {
                                    showWinGif("loss");
                                }
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
        clickSound.play();
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