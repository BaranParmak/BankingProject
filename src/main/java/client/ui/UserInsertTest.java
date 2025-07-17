package client.ui;

import common.User;
import server.dao.UserDAO;

public class UserInsertTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        // Create a sample user
        User user = new User("baran123", "password123", "Baran Parmak", 1);

        boolean success = userDAO.insert(user);

        if (success) {
            System.out.println("User inserted successfully!");
        } else {
            System.out.println("Failed to insert user.");
        }
    }
}
