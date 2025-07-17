package client.net;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientNetworkHandler {
    public static void main(String[] args) {
        String host = "localhost";  // Server IP or hostname
        int port = 12345;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server.");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter message to send: ");
            String message = scanner.nextLine();

            out.println(message);

            String response = in.readLine();
            System.out.println("Response from server: " + response);

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

