package models;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;


public class LoginDialog extends Dialog<Pair<String, Boolean>> {
    private final ToggleGroup authGroup = new ToggleGroup();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField newUsernameField = new TextField();
    private final PasswordField newPasswordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();
    private final RadioButton loginRadio = new RadioButton("Login");

    public LoginDialog() {
        setTitle("Rock Paper Scissors - Login");
        setHeaderText("Please login or register");

        // Set up radio buttons
        RadioButton loginRadio = new RadioButton("Login");
        loginRadio.setToggleGroup(authGroup);
        loginRadio.setSelected(true);

        RadioButton registerRadio = new RadioButton("Register");
        registerRadio.setToggleGroup(authGroup);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(loginRadio, 0, 0);
        grid.add(registerRadio, 1, 0);

        // Login fields
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        // Registration fields (initially hidden)
        grid.add(new Label("New Username:"), 0, 3);
        grid.add(newUsernameField, 1, 3);
        grid.add(new Label("New Password:"), 0, 4);
        grid.add(newPasswordField, 1, 4);
        grid.add(new Label("Confirm Password:"), 0, 5);
        grid.add(confirmPasswordField, 1, 5);

        newUsernameField.setVisible(false);
        newPasswordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        grid.getChildren().get(6).setVisible(false);
        grid.getChildren().get(7).setVisible(false);
        grid.getChildren().get(8).setVisible(false);
        grid.getChildren().get(9).setVisible(false);
        grid.getChildren().get(10).setVisible(false);

        // Toggle between login and register
        authGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isLogin = loginRadio.isSelected();

            usernameField.setVisible(isLogin);
            passwordField.setVisible(isLogin);
            newUsernameField.setVisible(!isLogin);
            newPasswordField.setVisible(!isLogin);
            confirmPasswordField.setVisible(!isLogin);

            grid.getChildren().get(1).setVisible(isLogin);
            grid.getChildren().get(2).setVisible(isLogin);
            grid.getChildren().get(3).setVisible(isLogin);
            grid.getChildren().get(4).setVisible(isLogin);
            grid.getChildren().get(5).setVisible(isLogin);
            grid.getChildren().get(6).setVisible(!isLogin);
            grid.getChildren().get(7).setVisible(!isLogin);
            grid.getChildren().get(8).setVisible(!isLogin);
            grid.getChildren().get(9).setVisible(!isLogin);
            grid.getChildren().get(10).setVisible(!isLogin);
        });

        // Set button types
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Update button text based on selection
        authGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            Button loginBtn = (Button) getDialogPane().lookupButton(loginButtonType);
            loginBtn.setText(loginRadio.isSelected() ? "Login" : "Register");

        });

        getDialogPane().setContent(grid);
        Platform.runLater(usernameField::requestFocus);

        // Convert result
        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                boolean isLogin = loginRadio.isSelected();
                if (isLogin) {
                    return new Pair<>(usernameField.getText().trim(), true);
                } else {
                    return new Pair<>(newUsernameField.getText().trim(), false);
                }
            }
            return null;
        });

        // Validate input
        final Button loginButton = (Button) getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        Runnable validateInput = () -> {
            boolean valid;
            if (loginRadio.isSelected()) {
                valid = !usernameField.getText().trim().isEmpty()
                        && !passwordField.getText().trim().isEmpty();
            } else {
                valid = !newUsernameField.getText().trim().isEmpty()
                        && !newPasswordField.getText().trim().isEmpty()
                        && newPasswordField.getText().equals(confirmPasswordField.getText());
            }
            loginButton.setDisable(!valid);
        };

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        newUsernameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
    }

    public String getPassword() {
        return loginRadio.isSelected() ? passwordField.getText() : newPasswordField.getText();
    }
}