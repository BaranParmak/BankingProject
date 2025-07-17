package client.ui;

import common.User;
import service.AuthenticationService;

import java.util.Scanner;

public class AuthTest {
    public static void main(String[] args) {
        AuthenticationService authService = new AuthenticationService();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authService.login(username, password);

        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.getFullName());
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }

        scanner.close();
    }
}
