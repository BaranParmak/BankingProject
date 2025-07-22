package client.ui;

import client.net.ClientNetworkHandler;
import common.Account;
import common.User;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static ClientNetworkHandler networkHandler;

    public static void main(String[] args) {
        try {
            networkHandler = new ClientNetworkHandler();
        } catch (IOException e) {
            System.out.println("Failed to connect to server. Please check if server is running.");
            e.printStackTrace();
            return;
        }

        User loggedInUser = null;

        while (loggedInUser == null) {
            System.out.println("1- Login");
            System.out.println("2- Register");
            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                loggedInUser = login(username, password);
                if (loggedInUser == null) {
                    System.out.println("Invalid username or password. Try again.");
                }
            } else if (choice.equals("2")) {
                boolean registered = register();
                if (registered) {
                    System.out.println("User registered successfully. You can login now.");
                } else {
                    System.out.println("Registration failed.");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }

        System.out.printf("Welcome %s!\n", loggedInUser.getFullName());

        // Always fetch the latest account information
        Account account = getAccount(loggedInUser.getCustomerNo());

        if (account == null) {
            System.out.println("No account found for user. Exiting.");
            closeConnection();
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("*************************");
            System.out.println("BANKING PROGRAM");
            System.out.println("*************************");
            System.out.println("1. Show Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            String menuChoice = scanner.nextLine();

            switch (menuChoice) {
                case "1" -> {
                    // Always get fresh balance data when showing balance
                    account = getAccount(loggedInUser.getCustomerNo());
                    System.out.printf("Balance: $%.2f\n", account.getBalance());
                }
                case "2" -> {
                    System.out.print("Enter amount: ");
                    double amount;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount.");
                        continue;
                    }
                    if (deposit(account.getAccountNo(), amount)) {
                        System.out.println("Deposit successful");
                        // Refresh account data after successful deposit
                        account = getAccount(loggedInUser.getCustomerNo());
                    } else {
                        System.out.println("Deposit failed. Invalid amount.");
                    }
                }
                case "3" -> {
                    System.out.print("Enter amount to withdraw: ");
                    double amount;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount.");
                        continue;
                    }
                    if (withdraw(account.getAccountNo(), amount)) {
                        System.out.println("Withdraw successful");
                        // Refresh account data after successful withdrawal
                        account = getAccount(loggedInUser.getCustomerNo());
                    } else {
                        System.out.println("Insufficient balance or invalid amount");
                    }
                }
                case "4" -> {
                    System.out.print("Receiver Account Number: ");
                    String receiverAccNo = scanner.nextLine();

                    System.out.print("Enter transfer amount: ");
                    double amount;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount.");
                        continue;
                    }

                    boolean transferSuccess = transfer(account.getAccountNo(), receiverAccNo, amount);
                    if (transferSuccess) {
                        System.out.println("Transfer successful.");
                        // Refresh account data after successful transfer
                        account = getAccount(loggedInUser.getCustomerNo());
                    } else {
                        System.out.println("Transfer failed. Check your balance or receiver account.");
                    }
                }
                case "5" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option");
            }
        }
        closeConnection();
    }

    private static User login(String username, String password) {
        try {
            String response = networkHandler.sendRequest("LOGIN:" + username + ":" + password);
            String[] parts = response.split(":");
            if (parts[0].equals("SUCCESS")) {
                int customerNo = Integer.parseInt(parts[1]);
                String fullName = parts[2];
                return new User(username, password, fullName, customerNo);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
            e.printStackTrace();
        }
        return null;
    }

    private static boolean register() {
        System.out.print("Choose username: ");
        String username = scanner.nextLine();

        System.out.print("Choose password: ");
        String password = scanner.nextLine();

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        try {
            String response = networkHandler.sendRequest("REGISTER:" + username + ":" + password + ":" + fullName);
            return response.equals("SUCCESS");
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
            e.printStackTrace();
            return false;
        }
    }

    private static Account getAccount(int customerNo) {
        try {
            String response = networkHandler.sendRequest("GETACCOUNT:" + customerNo);
            String[] parts = response.split(":");
            if (parts[0].equals("SUCCESS")) {
                String accountNo = parts[1];
                String fullName = parts[2];
                double balance = Double.parseDouble(parts[3]);
                return new Account(accountNo, fullName, customerNo, balance);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
            e.printStackTrace();
        }
        return null;
    }

    private static boolean deposit(String accountNo, double amount) {
        try {
            String response = networkHandler.sendRequest("DEPOSIT:" + accountNo + ":" + amount);
            return response.equals("SUCCESS");
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean withdraw(String accountNo, double amount) {
        try {
            String response = networkHandler.sendRequest("WITHDRAW:" + accountNo + ":" + amount);
            return response.equals("SUCCESS");
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean transfer(String senderAccNo, String receiverAccNo, double amount) {
        try {
            String response = networkHandler.sendRequest("TRANSFER:" + senderAccNo + ":" + receiverAccNo + ":" + amount);
            return response.equals("SUCCESS");
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
            e.printStackTrace();
            return false;
        }
    }

    private static void closeConnection() {
        try {
            if (networkHandler != null) {
                networkHandler.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}