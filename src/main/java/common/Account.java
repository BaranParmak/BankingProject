package common;

public class Account {
    private final String accountNo;
    private final String fullName;
    private final int customerNo; // 👈 NEW FIELD
    private double balance;

    public Account(String accountNo, String fullName, int customerNo, double balance) {
        this.accountNo = accountNo;
        this.fullName = fullName;
        this.customerNo = customerNo;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public int getCustomerNo() {
        return customerNo;
    }

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getFullName() {
        return fullName;
    }
}

