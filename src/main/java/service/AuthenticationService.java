package service;

import common.Account;
import common.User;
import server.dao.UserDAO;
import server.dao.AccountDAO;

public class AuthenticationService {
    private final UserDAO userDAO;
    private final AccountDAO accountDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.accountDAO = new AccountDAO();
    }

    public User login(String username, String password) {
        return userDAO.findByUsernameAndPassword(username, password);
    }

    // NEWLY ADDED REGISTER METHOD
    public boolean registerNewUser(String username, String password, String fullName) {
        int customerNo = (int)(System.currentTimeMillis()/1000); // Simple way to generate a unique ID

        User user = new User(username, password, fullName, customerNo);

        boolean userInserted = userDAO.insert(user);
        if (!userInserted) return false;

        String accountNo = "AC" + customerNo; // Example of simplified account number generation
        Account account = new Account(accountNo, fullName, customerNo, 0.0);
        return accountDAO.insert(account);
    }
}

