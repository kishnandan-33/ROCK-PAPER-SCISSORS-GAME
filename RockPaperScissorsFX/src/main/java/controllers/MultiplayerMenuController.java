package controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MultiplayerMenuController {
    @FXML private TextField hostIpField;
    
    private String username;
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @FXML
    private void handleHostGame() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MultiplayerHost.fxml"));
        Parent root = loader.load();
        MultiplayerHostController controller = loader.getController();
        controller.setUsername(username);
        
        Stage stage = new Stage();
        stage.setTitle("Host Game - " + username);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    
    @FXML
    private void handleJoinGame() throws Exception {
        String hostIp = hostIpField.getText().trim();
        if (hostIp.isEmpty()) {
            hostIp = "localhost"; // Default to localhost if no IP provided
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MultiplayerClient.fxml"));
        Parent root = loader.load();
        MultiplayerClientController controller = loader.getController();
        controller.setUsername(username);
        controller.setHostIp(hostIp);
        
        Stage stage = new Stage();
        stage.setTitle("Join Game - " + username);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    
    @FXML
    private void handleBackToMenu() {
        Stage stage = (Stage) hostIpField.getScene().getWindow();
        stage.close();
    }
}