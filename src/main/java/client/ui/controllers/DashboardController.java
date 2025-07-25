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
import javafx.stage.Stage;
import java.util.Base64;

import java.io.IOException;
import java.util.Optional;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;

    private User currentUser;
    private Account userAccount;
    private ClientNetworkHandler networkHandler;

    public void initData(User user, ClientNetworkHandler handler) {
        this.currentUser = user;
        this.networkHandler = handler;

        welcomeLabel.setText("Welcome, " + currentUser.getFullName());
    }

    @FXML
    private void handleShowBalance() {
        try {
            String response = networkHandler.sendRequest("GETACCOUNT:" + currentUser.getCustomerNo());
            String[] parts = response.split(":");

            if (parts[0].equals("SUCCESS")) {
                String accountNo = parts[1];
                String fullName = parts[2];
                double balance = Double.parseDouble(parts[3]);

                userAccount = new Account(accountNo, fullName, currentUser.getCustomerNo(), balance);

                // Using custom AlertDialog
                AlertDialog.showInfoDialog(
                        "Account Balance",
                        "Your account number: " + accountNo + "\n\n" +
                                "Your current balance: $" + String.format("%.2f", balance)
                );

            } else {
                statusLabel.setText("Failed to load account data");
            }
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewTransactionHistory() {
        try {
            // Request last 3 transactions from the server
            String response = networkHandler.sendRequest("GETLASTTRANSACTIONS:" + currentUser.getCustomerNo() + ":3");
            String[] parts = response.split(":");

            if (parts[0].equals("SUCCESS")) {
                if (parts.length == 2 && parts[1].equals("NO_TRANSACTIONS")) {
                    AlertDialog.showInfoDialog(
                            "Transaction History",
                            "You have no transaction history yet."
                    );
                    return;
                }

                StringBuilder historyText = new StringBuilder("Your last 3 transactions:\n\n");

                // Skip the first part (SUCCESS) and decode each transaction
                for (int i = 1; i < parts.length; i++) {
                    byte[] decodedBytes = Base64.getDecoder().decode(parts[i]);
                    String txnString = new String(decodedBytes);
                    historyText.append(txnString).append("\n\n");
                }

                AlertDialog.showInfoDialog(
                        "Transaction History",
                        historyText.toString()
                );
            } else {
                statusLabel.setText("Failed to load transaction history");
            }
        } catch (IOException e) {
            statusLabel.setText("Error connecting to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDepositAction() {
        openTransactionView("deposit");
    }

    @FXML
    private void handleWithdrawAction() {
        openTransactionView("withdraw");
    }

    @FXML
    private void handleTransferAction() {
        try {
            // First, fetch the latest account data
            String response = networkHandler.sendRequest("GETACCOUNT:" + currentUser.getCustomerNo());
            String[] parts = response.split(":");

            if (parts[0].equals("SUCCESS")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/transferView.fxml"));
                Parent transferView = loader.load();

                TransferController transferController = loader.getController();
                transferController.initData(currentUser, networkHandler);

                // Get current window size and state
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                boolean isMaximized = stage.isMaximized();
                double width = stage.getWidth();
                double height = stage.getHeight();

                stage.setScene(new Scene(transferView));

                // Preserve size and state
                stage.setWidth(width);
                stage.setHeight(height);
                stage.setMaximized(isMaximized);

                stage.show();
            } else {
                statusLabel.setText("Failed to load account data");
            }
        } catch (IOException e) {
            statusLabel.setText("Error loading transfer view");
            e.printStackTrace();
        }
    }

    private void openTransactionView(String transactionType) {
        // Using our custom dialog instead of TextInputDialog
        Optional<String> result = AlertDialog.showInputDialog(
                transactionType.substring(0, 1).toUpperCase() + transactionType.substring(1),
                "Enter amount to " + transactionType,
                "Amount"
        );

        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    statusLabel.setText("Amount must be positive");
                    return;
                }

                String response;
                if (transactionType.equals("deposit")) {
                    response = networkHandler.sendRequest("GETACCOUNT:" + currentUser.getCustomerNo());
                    String[] parts = response.split(":");
                    if (parts[0].equals("SUCCESS")) {
                        String accountNo = parts[1];
                        response = networkHandler.sendRequest("DEPOSIT:" + accountNo + ":" + amount);
                    } else {
                        statusLabel.setText("Failed to get account information");
                        return;
                    }
                } else { // withdraw
                    response = networkHandler.sendRequest("GETACCOUNT:" + currentUser.getCustomerNo());
                    String[] parts = response.split(":");
                    if (parts[0].equals("SUCCESS")) {
                        String accountNo = parts[1];
                        response = networkHandler.sendRequest("WITHDRAW:" + accountNo + ":" + amount);
                    } else {
                        statusLabel.setText("Failed to get account information");
                        return;
                    }
                }

                if (response.equals("SUCCESS")) {
                    AlertDialog.showInfoDialog(
                            transactionType.substring(0, 1).toUpperCase() + transactionType.substring(1) + " Successful",
                            "Your " + transactionType + " of $" + String.format("%.2f", amount) + " was successful."
                    );
                    statusLabel.setText(transactionType.substring(0, 1).toUpperCase() +
                            transactionType.substring(1) + " successful");
                } else {
                    AlertDialog.showInfoDialog(
                            transactionType.substring(0, 1).toUpperCase() + transactionType.substring(1) + " Failed",
                            "Your " + transactionType + " transaction could not be completed."
                    );
                    statusLabel.setText(transactionType.substring(0, 1).toUpperCase() +
                            transactionType.substring(1) + " failed");
                }

            } catch (NumberFormatException e) {
                statusLabel.setText("Please enter a valid number");
            } catch (IOException e) {
                statusLabel.setText("Error connecting to server");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleLogout() {
        try {
            // Send logout request to server
            networkHandler.sendRequest("LOGOUT:" + currentUser.getUsername());

            Parent loginView = FXMLLoader.load(getClass().getResource("/views/login.fxml"));

            // Get current window size and state
            Stage stage = (Stage) statusLabel.getScene().getWindow();
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
}