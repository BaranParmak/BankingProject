package service;

import common.Account;
import server.dao.AccountDAO;

public class TransactionService {
    private final AccountDAO accountDAO;
    private Account account;

    // Constructor takes customerNo and fetches the account from DB
    public TransactionService(int customerNo) {
        this.accountDAO = new AccountDAO();
        this.account = accountDAO.findByCustomerNo(customerNo);

        if (account == null) {
            System.out.println("No account found for customerNo: " + customerNo);
            // You could throw an exception here if needed
        }
    }

    public Account getAccount() {
        return account;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        account.deposit(amount);
        return accountDAO.update(account); // Update DB
    }

    public boolean withdraw(double amount) {
        if (account.withdraw(amount)) {
            return accountDAO.update(account); // Update DB
        }
        return false;
    }
}

