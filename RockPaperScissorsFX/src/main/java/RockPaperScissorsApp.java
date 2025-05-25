import controllers.MainMenuController;  // Add this import
import models.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RockPaperScissorsApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Show username dialog
        DatabaseInitializer.initializeDatabase();
        UsernameInputDialog usernameDialog = new UsernameInputDialog();
        String username = usernameDialog.showAndWait().orElse("Guest");

        // Load main menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        Parent root = loader.load();

        // Get controller and set username
        MainMenuController controller = loader.getController();
        controller.setUsername(username);  // Ensure this method exists

        primaryStage.setTitle("Rock, Paper, Scissors - " + username);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
