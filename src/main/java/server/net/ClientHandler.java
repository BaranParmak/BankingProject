package server.net;

import common.Account;
import common.User;
import service.AuthenticationService;
import service.TransactionService;
import server.dao.AccountDAO;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private AuthenticationService authService;
    private AccountDAO accountDAO;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.authService = new AuthenticationService();
        this.accountDAO = new AccountDAO();
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
                default:
                    return "FAIL:Unknown command";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL:" + e.getMessage();
        }
    }

    private String handleLogin(String username, String password) {
        User user = authService.login(username, password);
        if (user != null) {
            return "SUCCESS:" + user.getCustomerNo() + ":" + user.getFullName();
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
}