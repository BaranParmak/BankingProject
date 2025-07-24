package server.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketHandler {
    // HashMap to track active logged-in users
    private static final ConcurrentHashMap<String, ClientHandler> loggedInUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 12345;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Bank server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                executor.execute(new ClientHandler(clientSocket, loggedInUsers));
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Getter method for logged in users (can be used for debugging)
    public static ConcurrentHashMap<String, ClientHandler> getLoggedInUsers() {
        return loggedInUsers;
    }
}