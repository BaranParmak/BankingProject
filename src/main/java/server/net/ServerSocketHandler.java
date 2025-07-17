package server.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, waiting for client...");

            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected.");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message = in.readLine();
                System.out.println("Received from client: " + message);

                String response = "Server received: " + message;
                out.println(response);

                System.out.println("Response sent to client.");
            }

            System.out.println("Client disconnected, server closing.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
