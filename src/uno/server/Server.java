package uno.server;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import uno.lobby.LobbyImpl;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import uno.server.handlers.ClientRequestHandler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    public static final Path LOG_FILE_PATH = Path.of("exceptions.log");

    private static final int SERVER_PORT = 2525;

    @NotNull
    private final PlayerDataBase playersDataBase;
    private final LobbyImpl lobby = new LobbyImpl();
    @NotNull
    private final AtomicBoolean serverStopped;
    private ExecutorService executor;

    private ServerSocket serverSocket;

    public Server() throws IOException {
        this.playersDataBase = PlayerDataBaseImpl.load();
        serverStopped = new AtomicBoolean(false);
    }

    public void start() {
        Thread.currentThread().setName("Server Thread");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            this.executor = executor;
            this.serverSocket = serverSocket;

            System.out.println("Server started and listening for connect requests");
            Socket clientSocket;

            while (!serverStopped.get()) {
                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

                ClientRequestHandler clientHandler =
                    new ClientRequestHandler(clientSocket, lobby, playersDataBase, serverStopped);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("There is a problem with the server socket" + e);
        }
    }

    public void stop() {
        System.out.println("Server stopped");
        playersDataBase.save();
        try {
            if (serverSocket != null && executor != null &&
                !executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            } else {
                System.out.println("Server hasn't started yet");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            System.out.println("Server cannot close: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        File logFile = new File(LOG_FILE_PATH.toString());
        try (PrintStream writer = new PrintStream(logFile, StandardCharsets.UTF_16)) {
            System.setOut(writer);

            Server server = new Server();

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
                server.stop();
                System.exit(0);
            }).start();
            server.start();

        } catch (IOException e) {
            System.out.println("Failed to load accounts info: " + e.getMessage());
        }

    }
}