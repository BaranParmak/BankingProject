package common;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Transaction implements Serializable {
    private String type;        // "DEPOSIT", "WITHDRAW", "TRANSFER"
    private double amount;
    private Date timestamp;
    private String accountNo;
    private String targetAccountNo;  // For transfer operations

    // Constructor for deposit/withdraw operations
    public Transaction(String type, double amount, String accountNo) {
        this.type = type;
        this.amount = amount;
        this.accountNo = accountNo;
        this.timestamp = new Date();
    }

    // Constructor for transfer operations
    public Transaction(String type, double amount, String accountNo, String targetAccountNo) {
        this(type, amount, accountNo);
        this.targetAccountNo = targetAccountNo;
    }

    // Constructor for loading from database
    public Transaction(String type, double amount, Date timestamp, String accountNo, String targetAccountNo) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.accountNo = accountNo;
        this.targetAccountNo = targetAccountNo;
    }

    // Getters
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getTimestamp() { return timestamp; }
    public String getAccountNo() { return accountNo; }
    public String getTargetAccountNo() { return targetAccountNo; }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(timestamp);

        StringBuilder sb = new StringBuilder();
        sb.append(formattedDate).append("\n");

        if (type.equals("DEPOSIT")) {
            sb.append("Deposit: +$").append(String.format("%.2f", amount));
        } else if (type.equals("WITHDRAW")) {
            sb.append("Withdrawal: -$").append(String.format("%.2f", amount));
        } else if (type.equals("TRANSFER")) {
            sb.append("Transfer: -$").append(String.format("%.2f", amount));
            if (targetAccountNo != null) {
                sb.append(" to ").append(targetAccountNo);
            }
        }

        return sb.toString();
    }
}