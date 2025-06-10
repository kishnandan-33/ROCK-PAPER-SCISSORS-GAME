package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

public class MainMenuController {
    private String username;
    private int userId;

    private AudioClip clickSound = new AudioClip(getClass().getResource("/sounds/click.wav").toString());

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            this.username = "Guest";
        } else {
            this.username = username;
        }
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }


    @FXML
    private void handlePlayWithBot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BotGame.fxml"));
            Parent root = loader.load();
            BotGameController controller = loader.getController();
            clickSound.play();
            Stage stage = new Stage();
            stage.setTitle("Play with Bot - " + username);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
            controller.setUsername(username);
        } catch (Exception e) {
            showError("Failed to load Bot Game", e.getMessage());
        }
    }

    @FXML
    private void handlePlayWithFriend() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MultiplayerMenu.fxml"));
            Parent root = loader.load();
            MultiplayerMenuController controller = loader.getController();
            clickSound.play();

            Stage stage = new Stage();
            stage.setTitle("Play with Friend - " + username);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
            controller.setUsername(username);
        } catch (Exception e) {
            showError("Failed to load Multiplayer Menu", e.getMessage());
        }
    }

    @FXML
    private void handleViewLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Leaderboard.fxml"));
            Parent root = loader.load();
            LeaderboardController controller = loader.getController();
            controller.loadLeaderboard();
            clickSound.play();

            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            showError("Failed to load Leaderboard", e.getMessage());
        }
    }

    @FXML
    private void handleViewMatchHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MatchHistory.fxml"));
            Parent root = loader.load();
            MatchHistoryController controller = loader.getController();
            clickSound.play();

            Stage stage = new Stage();
            stage.setTitle("Match History - " + username);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            controller.setUsername(username);
        } catch (Exception e) {
            showError("Failed to load Match History", e.getMessage());
        }
    }

    // Helper: Show error alert
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
