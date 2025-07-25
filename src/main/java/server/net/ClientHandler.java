package server.net;

import common.Account;
import common.User;
import common.Transaction;
import service.AuthenticationService;
import service.TransactionService;
import server.dao.AccountDAO;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private AuthenticationService authService;
    private AccountDAO accountDAO;
    private ConcurrentHashMap<String, ClientHandler> loggedInUsers;
    private String currentUsername = null;  // Track the current logged in user

    public ClientHandler(Socket socket, ConcurrentHashMap<String, ClientHandler> loggedInUsers) {
        this.clientSocket = socket;
        this.authService = new AuthenticationService();
        this.accountDAO = new AccountDAO();
        this.loggedInUsers = loggedInUsers;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("📩 Received: " + inputLine);
                String response = processRequest(inputLine);
                out.println(response);
                System.out.println("📨 Sent: " + response);
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                // Mark user as logged out when connection is closed
                if (currentUsername != null) {
                    loggedInUsers.remove(currentUsername);
                    System.out.println("User logged out due to disconnection: " + currentUsername);
                }

                clientSocket.close();
                System.out.println("🔒 Client connection closed: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.out.println("Error closing client connection: " + e.getMessage());
            }
        }
    }

    private String processRequest(String request) {
        String[] parts = request.split(":");
        String command = parts[0];

        try {
            switch (command) {
                case "LOGIN":
                    return handleLogin(parts[1], parts[2]);
                case "REGISTER":
                    return handleRegister(parts[1], parts[2], parts[3]);
                case "GETACCOUNT":
                    return handleGetAccount(Integer.parseInt(parts[1]));
                case "DEPOSIT":
                    return handleDeposit(parts[1], Double.parseDouble(parts[2]));
                case "WITHDRAW":
                    return handleWithdraw(parts[1], Double.parseDouble(parts[2]));
                case "TRANSFER":
                    return handleTransfer(parts[1], parts[2], Double.parseDouble(parts[3]));
                case "LOGOUT":
                    return handleLogout(parts[1]);
                case "GETLASTTRANSACTIONS":
                    return handleGetLastTransactions(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                default:
                    return "FAIL:Unknown command";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL:" + e.getMessage();
        }
    }

    private String handleLogin(String username, String password) {
        // Check if user is already logged in
        if (loggedInUsers.containsKey(username)) {
            return "ERROR:USER_ALREADY_LOGGED_IN";
        }

        User user = authService.login(username, password);
        if (user != null) {
            // If login successful, add user to active users list
            loggedInUsers.put(username, this);
            currentUsername = username;
            System.out.println("User logged in: " + username);
            return "SUCCESS:" + user.getCustomerNo() + ":" + user.getFullName();
        }
        return "FAIL";
    }

    private String handleLogout(String username) {
        if (username != null && username.equals(currentUsername)) {
            loggedInUsers.remove(username);
            currentUsername = null;
            System.out.println("User logged out: " + username);
            return "SUCCESS";
        }
        return "FAIL";
    }

    private String handleRegister(String username, String password, String fullName) {
        boolean success = authService.registerNewUser(username, password, fullName);
        return success ? "SUCCESS" : "FAIL";
    }

    private String handleGetAccount(int customerNo) {
        Account account = accountDAO.findByCustomerNo(customerNo);
        if (account != null) {
            return "SUCCESS:" + account.getAccountNo() + ":" + account.getFullName() + ":" + account.getBalance();
        }
        return "FAIL:Account not found";
    }

    private String handleDeposit(String accountNo, double amount) {
        Account account = accountDAO.findByAccountNo(accountNo);
        if (account != null) {
            TransactionService service = new TransactionService(account.getCustomerNo());
            boolean success = service.deposit(amount);
            return success ? "SUCCESS" : "FAIL:Deposit failed";
        }
        return "FAIL:Account not found";
    }

    private String handleWithdraw(String accountNo, double amount) {
        Account account = accountDAO.findByAccountNo(accountNo);
        if (account != null) {
            TransactionService service = new TransactionService(account.getCustomerNo());
            boolean success = service.withdraw(amount);
            return success ? "SUCCESS" : "FAIL:Insufficient funds";
        }
        return "FAIL:Account not found";
    }

    private String handleTransfer(String senderAccountNo, String receiverAccountNo, double amount) {
        Account account = accountDAO.findByAccountNo(senderAccountNo);
        if (account != null) {
            TransactionService service = new TransactionService(account.getCustomerNo());
            boolean success = service.transfer(receiverAccountNo, amount);
            return success ? "SUCCESS" : "FAIL:Transfer failed";
        }
        return "FAIL:Account not found";
    }

    private String handleGetLastTransactions(int customerNo, int limit) {
        Account account = accountDAO.findByCustomerNo(customerNo);
        if (account == null) {
            return "FAIL:Account not found";
        }

        TransactionService service = new TransactionService(customerNo);
        List<Transaction> transactions = service.getLastTransactions(limit);

        if (transactions.isEmpty()) {
            return "SUCCESS:NO_TRANSACTIONS";
        }

        StringBuilder response = new StringBuilder("SUCCESS");
        for (Transaction txn : transactions) {
            // Base64 kodlama kullanarak özel karakterleri güvenli hale getiriyoruz
            String encodedTxn = Base64.getEncoder().encodeToString(txn.toString().getBytes());
            response.append(":").append(encodedTxn);
        }

        return response.toString();
    }
}