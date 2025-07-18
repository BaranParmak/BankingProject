package server.dao;

import common.Account;
import java.sql.*;

public class AccountDAO {

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

    //✅ Transfer metodu düzenlenmiş son hali (temiz ve çalışır durumda)
    public boolean transfer(String senderAccountNo, String receiverAccountNo, double amount) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement withdrawStmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_no = ? AND balance >= ?");
                 PreparedStatement depositStmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_no = ?")) {

                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, senderAccountNo);
                withdrawStmt.setDouble(3, amount);
                int withdrawResult = withdrawStmt.executeUpdate();

                if (withdrawResult == 0) {
                    conn.rollback();
                    return false;
                }

                depositStmt.setDouble(1, amount);
                depositStmt.setString(2, receiverAccountNo);
                int depositResult = depositStmt.executeUpdate();
                if (depositResult == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
