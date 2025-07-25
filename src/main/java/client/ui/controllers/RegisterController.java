package client.ui.controllers;

import client.net.ClientNetworkHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private Label statusLabel;

    private ClientNetworkHandler networkHandler;

    @FXML
    public void initialize() {
        // Force update layout after screen is loaded
        Platform.runLater(() -> {
            // Wait for scene to load to get the window
            if (usernameField.getScene() != null) {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                double width = stage.getWidth();
                double height = stage.getHeight();
                boolean isMaximized = stage.isMaximized();

                // If maximized, exit full screen
                if (isMaximized) {
                    stage.setMaximized(false);
                }

                // Make a small change and immediately revert it
                stage.setWidth(width - 1);
                stage.setHeight(height - 1);

                // Restore to original size
                Platform.runLater(() -> {
                    stage.setWidth(width);
                    stage.setHeight(height);

                    // If it was maximized, restore full screen
                    if (isMaximized) {
                        stage.setMaximized(true);
                    }
                });
            }
        });
    }

    public void setNetworkHandler(ClientNetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    private void navigateToLogin() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/views/login.fxml"));

            // Get current window size and state
            Stage stage = (Stage) usernameField.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene loginScene = new Scene(loginView);
            stage.setScene(loginScene);
            stage.setTitle("Login");

            // Preserve size and state
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMaximized(isMaximized);

            stage.show();

            // Force layout refresh after transitioning to login screen
            Platform.runLater(() -> {
                if (isMaximized) {
                    stage.setMaximized(false);
                }

                stage.setWidth(width - 1);
                stage.setHeight(height - 1);

                Platform.runLater(() -> {
                    stage.setWidth(width);
                    stage.setHeight(height);

                    if (isMaximized) {
                        stage.setMaximized(true);
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            statusLabel.setText("All fields are required");
            return;
        }

        try {
            String response = networkHandler.sendRequest("REGISTER:" + username + ":" + password + ":" + fullName);

            if (response.equals("SUCCESS")) {
                statusLabel.setText("Registration successful! Redirecting to login...");
                // Wait 2 seconds then go back to login
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::navigateToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                statusLabel.setText("Registration failed. Username may already exist.");
            }
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }
}