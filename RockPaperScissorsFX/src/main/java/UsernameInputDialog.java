import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class UsernameInputDialog extends Dialog<String> {
    public UsernameInputDialog() {
        setTitle("Welcome to Rock, Paper, Scissors");
        setHeaderText("Please enter your username");

        // Set the button types
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the username input field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);

        getDialogPane().setContent(grid);

        // Request focus on the username field by default
        Platform.runLater(usernameField::requestFocus);

        // Convert the result to a username when the OK button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return usernameField.getText();
            }
            return null;
        });
    }
}