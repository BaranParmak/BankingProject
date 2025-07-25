package server.dao;

import common.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDAO {

    // Record a new transaction in the database
    public boolean insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (type, amount, timestamp, account_no, target_account_no) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getType());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setLong(3, transaction.getTimestamp().getTime()); // Store as milliseconds
            stmt.setString(4, transaction.getAccountNo());
            stmt.setString(5, transaction.getTargetAccountNo());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get the last N transactions for an account
    public List<Transaction> findLastTransactions(String accountNo, int limit) {
        List<Transaction> transactions = new ArrayList<>();

        // Get transactions where this account is either sender or receiver
        String sql = "SELECT * FROM transactions WHERE account_no = ? OR target_account_no = ? " +
                "ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNo);
            stmt.setString(2, accountNo);
            stmt.setInt(3, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Date timestamp = new Date(rs.getLong("timestamp"));
                String accNo = rs.getString("account_no");
                String targetAccNo = rs.getString("target_account_no");

                Transaction transaction = new Transaction(type, amount, timestamp, accNo, targetAccNo);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}