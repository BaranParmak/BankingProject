package client.ui.controllers;

import client.net.ClientNetworkHandler;
import client.ui.dialogs.AlertDialog;
import common.Account;
import common.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TransferController {

    @FXML private TextField receiverAccountField;
    @FXML private TextField amountField;
    @FXML private Label userNameLabel;
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;
    @FXML private StackPane confirmationDialog;

    private User currentUser;
    private Account userAccount;
    private ClientNetworkHandler networkHandler;
    private String receiverAccount;
    private double transferAmount;

    @FXML
    public void initialize() {
        // Force update layout after screen is loaded
        Platform.runLater(() -> {
            // First reduce window size by one pixel then restore it
            // This will force layout refresh
            Stage stage = (Stage) receiverAccountField.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();

            // If maximized, exit full screen
            if (wasMaximized) {
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
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
            });
        });
    }

    public void initData(User user, ClientNetworkHandler handler) {
        this.currentUser = user;
        this.networkHandler = handler;

        userNameLabel.setText(currentUser.getFullName());
        loadAccountData();
    }

    private void loadAccountData() {
        try {
            String response = networkHandler.sendRequest("GETACCOUNT:" + currentUser.getCustomerNo());
            String[] parts = response.split(":");

            if (parts[0].equals("SUCCESS")) {
                String accountNo = parts[1];
                String fullName = parts[2];
                double balance = Double.parseDouble(parts[3]);

                userAccount = new Account(accountNo, fullName, currentUser.getCustomerNo(), balance);
                balanceLabel.setText("Current Balance: $" + String.format("%.2f", balance));
            } else {
                statusLabel.setText("Failed to load account data");
            }
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTransfer() {
        if (receiverAccountField.getText().isEmpty()) {
            statusLabel.setText("Please enter receiver account number");
            return;
        }

        try {
            transferAmount = Double.parseDouble(amountField.getText());
            if (transferAmount <= 0) {
                statusLabel.setText("Amount must be positive");
                return;
            }

            // Check if sufficient balance
            if (transferAmount > userAccount.getBalance()) {
                statusLabel.setText("Insufficient balance");
                return;
            }

            receiverAccount = receiverAccountField.getText();

            // Using AlertDialog instead of confirmationDialog
            boolean confirmed = AlertDialog.showConfirmDialog(
                    "Confirm Transfer",
                    "Are you sure you want to transfer $" + String.format("%.2f", transferAmount) +
                            " to account " + receiverAccount + "?\n\nThis action cannot be undone."
            );

            if (confirmed) {
                processTransfer();
            } else {
                statusLabel.setText("Transfer cancelled");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid amount");
        }
    }

    private void processTransfer() {
        try {
            String response = networkHandler.sendRequest("TRANSFER:" + userAccount.getAccountNo() +
                    ":" + receiverAccount + ":" + transferAmount);

            if (response.equals("SUCCESS")) {
                // Using custom AlertDialog
                AlertDialog.showInfoDialog(
                        "Transfer Successful",
                        "$" + String.format("%.2f", transferAmount) +
                                " has been transferred to account " + receiverAccount
                );

                // Go back to dashboard
                goToDashboard();
            } else {
                statusLabel.setText("Transfer failed. Please check account number.");
            }
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccept() {
        // This method is no longer used, but kept for FXML references
        processTransfer();
    }

    @FXML
    private void handleReject() {
        // This method is no longer used, but kept for FXML references
        confirmationDialog.setVisible(false);
        statusLabel.setText("Transfer cancelled");
    }

    @FXML
    private void handleBack() {
        // Return to main screen
        goToDashboard();
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent dashboardView = loader.load();

            DashboardController controller = loader.getController();
            controller.initData(currentUser, networkHandler);

            // Get current window size and state
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setScene(new Scene(dashboardView));

            // Preserve size and state
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMaximized(isMaximized);

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}