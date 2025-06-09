import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;
import controllers.MainMenuController;
import models.DatabaseInitializer;
import models.LoginDialog;
import models.AuthService;
import java.util.Optional;


public class RockPaperScissorsApp extends Application {
    private static String currentUsername;
    private static int currentUserId;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database (ensure tables exist, etc.)
            DatabaseInitializer.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database init failed: " + e.getMessage());
            return;
        }

        // Show login dialog
        LoginDialog loginDialog = new LoginDialog();
        boolean authenticated = false;

        while (!authenticated) {
            Optional<Pair<String, Boolean>> result = loginDialog.showAndWait();
            if (!result.isPresent()) {
                Platform.exit();
                return;
            }

            Pair<String, Boolean> authData = result.get();
            String username = authData.getKey();
            boolean isLogin = authData.getValue();

            if (isLogin) {
                // Login attempt
                if (AuthService.authenticateUser(username, loginDialog.getPassword())) {
                    currentUsername = username;
                    currentUserId = AuthService.getUserId(username);
                    authenticated = true;
                } else {
                    showAlert("Login Failed", "Invalid username or password");
                }
            } else {
                // Registration attempt
                if (AuthService.registerUser(username, loginDialog.getPassword())) {
                    showAlert("Registration Successful", "Account created! Please login");
                } else {
                    showAlert("Registration Failed", "Username already exists");
                }
            }
        }

        // Load main menu
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            MainMenuController controller = loader.getController();
            controller.setUsername(currentUsername);
            controller.setUserId(currentUserId);

            primaryStage.setTitle("Rock, Paper, Scissors - " + currentUsername);
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void main(String[] args) {
        launch(args);
    }
}