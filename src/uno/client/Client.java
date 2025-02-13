package uno.client;

import uno.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final int SERVER_PORT = 2525;
    private static final String HOST = "localhost";

    private static final String SERVER_ERROR_MESSAGE =
        "There is a problem with the network communication.Try again later or contact administrator" +
            " by providing the logs in " + Server.LOG_FILE_PATH;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Connected to the server.");
            Thread listenerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println(SERVER_ERROR_MESSAGE);
                }
            });
            listenerThread.start();
            while (true) {
                String userInput = scanner.nextLine();
                writer.println(userInput);
            }

        } catch (IOException e) {
            System.out.println(SERVER_ERROR_MESSAGE);
        }
    }
}
