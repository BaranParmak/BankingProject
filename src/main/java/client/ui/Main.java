package client.ui;

import service.AuthenticationService;
import service.TransactionService;
import common.User;
import common.Account;

import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        AuthenticationService authService = new AuthenticationService();
        User loggedInUser = null;

        while (loggedInUser == null) {
            System.out.println("1- Login");
            System.out.println("2- Register");
            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            if(choice.equals("1")){
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                loggedInUser = authService.login(username, password);
                if (loggedInUser == null) {
                    System.out.println("Invalid username or password. Try again.");
                }
            }
            else if(choice.equals("2")){
                boolean registered = register(authService);
                if(registered){
                    System.out.println("User registered successfully. You can login now.");
                } else {
                    System.out.println("Registration failed.");
                }
            }
            else{
                System.out.println("Invalid choice.");
            }
        }

        System.out.printf("Welcome %s!\n", loggedInUser.getFullName());

        TransactionService transactionService = new TransactionService(loggedInUser.getCustomerNo());
        Account account = transactionService.getAccount();

        if (account == null) {
            System.out.println("No account found for user. Exiting.");
            scanner.close();
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
                case "1" -> System.out.printf("Balance: $%.2f\n", account.getBalance());
                case "2" -> {
                    System.out.print("Enter amount: ");
                    double amount;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount.");
                        continue;
                    }
                    if (transactionService.deposit(amount))
                        System.out.println("Deposit successful");
                    else
                        System.out.println("Deposit failed. Invalid amount.");
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
                    if (transactionService.withdraw(amount))
                        System.out.println("Withdraw successful");
                    else
                        System.out.println("Insufficient balance or invalid amount");
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

                    boolean transferSuccess = transactionService.transfer(receiverAccNo, amount);
                    if (transferSuccess) {
                        System.out.println("Transfer successful.");
                        account = transactionService.getAccount();
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
        scanner.close();
    }

    private static boolean register(AuthenticationService authService) {
        System.out.print("Choose username: ");
        String username = scanner.nextLine();

        System.out.print("Choose password: ");
        String password = scanner.nextLine();

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        return authService.registerNewUser(username, password, fullName);
    }
}


