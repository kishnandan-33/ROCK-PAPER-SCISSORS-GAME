import controllers.MainMenuController;
import models.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class RockPaperScissorsApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database (ensure tables exist, etc.)
            DatabaseInitializer.initializeDatabase();

            // Ask user for their name
            UsernameInputDialog usernameDialog = new UsernameInputDialog();
            String username = usernameDialog.showAndWait().orElse("Guest");

            // Validate username (optional)
            if (username.trim().isEmpty()) {
                username = "Guest";
            }

            // Load the MainMenu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();

            // Set the username in the controller
            MainMenuController controller = loader.getController();
            controller.setUsername(username);  // Ensure this method is defined in controller

            // Set up the scene and show the window
            primaryStage.setTitle("Rock, Paper, Scissors - " + username);
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();

        } catch (Exception e) {
            // Show alert if there is any unexpected error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while starting the application.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace(); // For debugging in console
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
