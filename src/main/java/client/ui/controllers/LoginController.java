package client.ui.controllers;

import client.net.ClientNetworkHandler;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Label forgotPasswordLink;

    private ClientNetworkHandler networkHandler;
    private User currentUser;

    @FXML
    public void initialize() {
        try {
            networkHandler = new ClientNetworkHandler();
            statusLabel.setText("Connected to server");

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
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password are required");
            return;
        }

        try {
            String response = networkHandler.sendRequest("LOGIN:" + username + ":" + password);
            String[] parts = response.split(":");

            if (parts[0].equals("SUCCESS")) {
                int customerNo = Integer.parseInt(parts[1]);
                String fullName = parts[2];
                currentUser = new User(username, password, fullName, customerNo);

                // Load dashboard - debugging log
                System.out.println("Login successful, loading dashboard...");

                // Create the FXML loader
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                Parent dashboardView = loader.load();

                // Get the controller and initialize it with user data
                DashboardController controller = loader.getController();
                controller.initData(currentUser, networkHandler);

                // Debug log
                System.out.println("Dashboard controller initialized");

                // Get current stage
                Stage stage = (Stage) usernameField.getScene().getWindow();

                // Get current window size and state
                boolean isMaximized = stage.isMaximized();
                double width = stage.getWidth();
                double height = stage.getHeight();

                // Create new scene
                Scene dashboardScene = new Scene(dashboardView);
                stage.setScene(dashboardScene);
                stage.setTitle("Banking Dashboard");

                // Preserve size and state
                stage.setWidth(width);
                stage.setHeight(height);
                stage.setMaximized(isMaximized);

                // Debug log
                System.out.println("Dashboard scene set");

                // Show the updated stage
                stage.show();

                // Force layout refresh after transitioning to dashboard
                Platform.runLater(() -> {
                    // Same screen sizing trick
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

            } else {
                statusLabel.setText("Invalid username or password");
            }
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Load register view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            Parent registerView = loader.load();

            // Get register controller and pass network handler
            RegisterController registerController = loader.getController();
            registerController.setNetworkHandler(networkHandler);

            // Get current window size and state
            Stage stage = (Stage) usernameField.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Create new scene
            Scene registerScene = new Scene(registerView);

            // Set new scene
            stage.setScene(registerScene);
            stage.setTitle("Register");

            // Preserve size and state
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMaximized(isMaximized);

            stage.show();

            // Force layout refresh after transitioning to register screen
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
            statusLabel.setText("Error loading register view");
            e.printStackTrace();
        }
    }
}