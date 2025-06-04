import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class UsernameInputDialog extends Dialog<String> {
    public UsernameInputDialog() {
        setTitle("Welcome to Rock, Paper, Scissors");
        setHeaderText("Please enter your username");

        // Set the button types
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create the username input field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (required)");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);

        getDialogPane().setContent(grid);

        // Request focus on the username field by default
        Platform.runLater(usernameField::requestFocus);

        // Enable/disable OK button based on input
        Button okButton = (Button) getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        // Validate input
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result to a username when the OK button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return usernameField.getText().trim();
            }
            return null; // Return null if cancelled
        });

        // Prevent closing on X or ESC if username is empty
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getScene().getWindow().setOnCloseRequest(event -> {
            if (usernameField.getText().trim().isEmpty()) {
                event.consume(); // Prevent closing
                showErrorAlert();
            }
        });
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Username Required");
        alert.setHeaderText(null);
        alert.setContentText("You must enter a username to play the game.");
        alert.showAndWait();
    }
}