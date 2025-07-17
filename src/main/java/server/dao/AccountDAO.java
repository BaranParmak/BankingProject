package server.dao;

import common.Account;

import java.sql.*;

public class AccountDAO {

    // Fetch an account by its account number
    public Account findByAccountNo(String accountNo) {
        String sql = "SELECT * FROM accounts WHERE account_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                double balance = rs.getDouble("balance");
                int customerNo = rs.getInt("customer_no");
                return new Account(accountNo, fullName, customerNo, balance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ✅ NEW: Fetch an account by customer number
    public Account findByCustomerNo(int customerNo) {
        String sql = "SELECT * FROM accounts WHERE customer_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String accountNo = rs.getString("account_no");
                String fullName = rs.getString("full_name");
                double balance = rs.getDouble("balance");
                return new Account(accountNo, fullName, customerNo, balance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Insert a new account
    public boolean insert(Account account) {
        String sql = "INSERT INTO accounts(account_no, full_name, balance, customer_no) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNo());
            stmt.setString(2, account.getFullName());
            stmt.setDouble(3, account.getBalance());
            stmt.setInt(4, account.getCustomerNo());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update account balance
    public boolean update(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, account.getBalance());
            stmt.setString(2, account.getAccountNo());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete account
    public boolean delete(String accountNo) {
        String sql = "DELETE FROM accounts WHERE account_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNo);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}

