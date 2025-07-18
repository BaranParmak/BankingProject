package service;

import common.Account;
import server.dao.AccountDAO;

public class TransactionService {
    private final AccountDAO accountDAO;
    private Account account;

    public TransactionService(int customerNo) {
        this.accountDAO = new AccountDAO();
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
            account = accountDAO.findByAccountNo(account.getAccountNo()); // güncel bilgiyi al
        }

        return result;
    }

    public boolean withdraw(double amount) {
        if(account.withdraw(amount)){
            boolean result = accountDAO.update(account);
            if (result) {
                account = accountDAO.findByAccountNo(account.getAccountNo()); // güncel bilgisi alınır
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
            account = accountDAO.findByAccountNo(account.getAccountNo()); // güncel hesap bilgisi
        }
        return result;
    }
}

