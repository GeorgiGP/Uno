package uno.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.jetbrains.annotations.NotNull;
import uno.exceptions.signexceptions.SignException;
import uno.lobby.Lobby;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import uno.server.PlayerDataBase;

public class ClientRequestHandler implements Runnable, Handler {
    @NotNull
    private final Lobby lobby;
    @NotNull
    private final Socket socket;
    @NotNull
    private final PlayerDataBase logger;
    @NotNull
    private final AtomicBoolean serverStopped;
    private static final int COUNT_ATTRIBUTES_SIGN = 3;

    public ClientRequestHandler(@NotNull Socket socket,
                                @NotNull Lobby lobby,
                                @NotNull PlayerDataBase logger,
                                @NotNull AtomicBoolean serverStopped) {
        this.socket = socket;
        this.lobby = lobby;
        this.logger = logger;
        this.serverStopped = serverStopped;
    }

    @Override
    public void run() {
        if (serverStopped.get()) {
            return;
        }
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            communicate(in, out);

        } catch (IOException | RuntimeException e) {
            System.out.println(e.getMessage() + " occurred by socket address: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage() + " occurred by socket address: " + socket.getInetAddress());
            }
        }
    }

    private void communicate(@NotNull BufferedReader in, @NotNull PrintWriter out) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            String[] attributes = inputLine.split("\\s++");
            String mode = attributes[0].toLowerCase();
            try {
                if ("login".equals(mode)) {
                    if (attributes.length < COUNT_ATTRIBUTES_SIGN) {
                        out.println("When calling login command should have a username and a password");
                        continue;
                    }
                    login(in, out, attributes);
                } else if ("register".equals(mode)) {
                    if (attributes.length < COUNT_ATTRIBUTES_SIGN) {
                        out.println("When calling register command should have a username and a password");
                        continue;
                    }
                    logger.register(extractBetween(attributes[1]), extractBetween(attributes[2]));
                    out.println("Registration successful!");
                } else if ("exit".equals(mode)) {
                    break;
                } else {
                    out.println("Invalid command");
                }
            } catch (SignException | IllegalArgumentException e) {
                out.println(e.getMessage());
            }
        }
    }

    private void login(@NotNull BufferedReader in, @NotNull PrintWriter out,
                       @NotNull String[] attributes) throws SignException, IOException {
        var username = extractBetween(attributes[1]);
        logger.login(username, extractBetween(attributes[2]));
        out.println("Hello " + username + "! Log-in successful!");
        try {
            new LobbyHandler(out, in, lobby, username).handle();
        } finally {
            logger.logout(username);
        }
        out.println(username + " logged out!");
    }
}