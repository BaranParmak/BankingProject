package service;

import common.Account;
import common.Transaction;
import server.dao.AccountDAO;
import server.dao.TransactionDAO;

import java.util.List;

public class TransactionService {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO; // New field
    private Account account;

    public TransactionService(int customerNo) {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO(); // Initialize TransactionDAO
        this.account = accountDAO.findByCustomerNo(customerNo);

        if (this.account == null){
            System.out.println("Account not found for customerNo: " + customerNo);
        }
    }

    public Account getAccount() {
        return account;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;

        account.deposit(amount);
        boolean result = accountDAO.update(account);

        if (result) {
            // Record transaction
            Transaction transaction = new Transaction("DEPOSIT", amount, account.getAccountNo());
            transactionDAO.insert(transaction);

            account = accountDAO.findByAccountNo(account.getAccountNo()); // get updated information
        }

        return result;
    }

    public boolean withdraw(double amount) {
        if(account.withdraw(amount)){
            boolean result = accountDAO.update(account);

            if (result) {
                // Record transaction
                Transaction transaction = new Transaction("WITHDRAW", amount, account.getAccountNo());
                transactionDAO.insert(transaction);

                account = accountDAO.findByAccountNo(account.getAccountNo()); // updated information is retrieved
            }
            return result;
        }
        return false;
    }

    public boolean transfer(String receiverAccountNo, double amount){
        if(amount <= 0 || account.getAccountNo().equals(receiverAccountNo)){
            return false;
        }
        boolean result = accountDAO.transfer(account.getAccountNo(), receiverAccountNo, amount);

        if(result){
            // Record transaction
            Transaction transaction = new Transaction("TRANSFER", amount, account.getAccountNo(), receiverAccountNo);
            transactionDAO.insert(transaction);

            account = accountDAO.findByAccountNo(account.getAccountNo()); // updated account information
        }
        return result;
    }

    // New method to get transaction history
    public List<Transaction> getLastTransactions(int limit) {
        return transactionDAO.findLastTransactions(account.getAccountNo(), limit);
    }
}

