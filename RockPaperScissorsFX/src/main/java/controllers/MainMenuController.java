package controllers;  // Must match your package structure
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;

public class MainMenuController {
    private String username; // Store username

    private AudioClip clickSound = new AudioClip(getClass().getResource("/sounds/click.wav").toString());


    // Add this method
    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void handlePlayWithBot() throws Exception {
        clickSound.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BotGame.fxml"));
        Parent root = loader.load();
        BotGameController controller = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("Play with Bot - " + username);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        controller.setUsername(username);  // Pass username to next screen
    }

    @FXML
    private void handlePlayWithFriend() throws Exception {
        clickSound.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MultiplayerMenu.fxml"));
        Parent root = loader.load();
        MultiplayerMenuController controller = loader.getController();
//        controller.setUsername(username);

        Stage stage = new Stage();
        stage.setTitle("Play with Friend - " + username);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        controller.setUsername(username);
    }

    @FXML
    private void handleViewLeaderboard() throws Exception {
        clickSound.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Leaderboard.fxml"));
        Parent root = loader.load();
        LeaderboardController controller = loader.getController();
//        controller.loadLeaderboard();

        Stage stage = new Stage();
        stage.setTitle("Leaderboard");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        controller.loadLeaderboard();

    }

    @FXML
    private void handleViewMatchHistory() throws Exception {
        clickSound.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MatchHistory.fxml"));
        Parent root = loader.load();
        MatchHistoryController controller = loader.getController();
//        controller.setUsername(username);

        Stage stage = new Stage();
        stage.setTitle("Match History - " + username);
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
        controller.setUsername(username);
    }
}