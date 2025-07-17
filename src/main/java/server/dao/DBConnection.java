package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    // Path to SQLite database file
    private static final String DB_URL = "jdbc:sqlite:bank.db";

    // Static block: Runs once when the class is loaded
    static {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create 'accounts' table with customer_no included
            String createAccountsTable = """
                CREATE TABLE IF NOT EXISTS accounts (
                    account_no TEXT PRIMARY KEY,
                    full_name TEXT NOT NULL,
                    balance REAL NOT NULL,
                    customer_no INTEGER NOT NULL
                );
                """;
            stmt.execute(createAccountsTable);

            // Create 'users' table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    customer_no INTEGER PRIMARY KEY,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    full_name TEXT NOT NULL
                );
                """;
            stmt.execute(createUsersTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to return a connection to the SQLite database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

