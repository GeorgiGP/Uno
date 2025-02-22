package uno.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import uno.game.UnoGame;
import uno.game.UnoPlayer;
import uno.lobby.Lobby;
import uno.lobby.PreGame;

public class LobbyHandler implements Handler {
    @NotNull
    private final PrintWriter out;
    @NotNull
    private final BufferedReader in;
    @NotNull
    private final Lobby lobby;
    @NotNull
    private final String registrationName;

    public LobbyHandler(@NotNull PrintWriter out, @NotNull BufferedReader in,
                        @NotNull Lobby lobby, @NotNull String playerName) {
        this.out = out;
        this.in = in;
        this.lobby = lobby;
        this.registrationName = playerName;
    }

    public void handle() throws IOException {
        String cur;
        while ((cur = in.readLine()) != null) {
            try {
                String[] args = cur.strip().split("\\s+");
                String mode = args[0].toLowerCase();
                switch (LobbyOptions.fromString(mode)) {
                    case CREATE_GAME -> createGame(args);
                    case JOIN_GAME -> join(args);
                    case LIST_GAMES -> listGames(args);
                    case SUMMARY_GAME -> summary(args);
                    case LOGOUT -> {
                        return;
                    }
                    case null, default -> {
                        out.println("Unknown command: " + cur);
                        out.println(LobbyOptions.help());
                    }
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                out.println(e.getMessage());
                out.println(LobbyOptions.help());
                System.out.println(e.getMessage());
            }
        }
    }

    private void summary(@NotNull String[] args) {
        if (args.length == 1) {
            throw new IllegalArgumentException("Summary must contain --game-id=<game-id>");
        }
        out.println(lobby.getSummaryOfGame(extractBetween(args[1])));
    }

    private void createGame(@NotNull String[] args) {
        if (args.length == 1) {
            throw new IllegalArgumentException("Creating game must contain --game-id=<game-id>");
        }
        int playersCount;
        if (args.length == 2) {
            playersCount = 2;
        } else {
            playersCount = Integer.parseInt(extractBetween(args[2]));
        }
        lobby.createGame(playersCount, extractBetween(args[1]), registrationName);
    }

    private void listGames(@NotNull String[] args) {
        String option;
        if (args.length == 1) {
            option = "all";
        } else {
            option = extractBetween(args[1]);
        }
        out.println(gamesInfo(lobby.listGames(option)));
    }

    private void join(@NotNull String[] args) throws IOException {
        if (args.length == 1) {
            throw new IllegalArgumentException("Joining game must contain --game-id=<game-id>");
        }
        String displayName;
        if (args.length <= 2) {
            displayName = registrationName;
        } else {
            displayName = extractBetween(args[2]);
        }
        UnoPlayer player = new UnoPlayer(out, in, displayName, registrationName);
        var game = lobby.joinGame(extractBetween(args[1]), player);
        new PreGame(player, game).introduceToGame();
    }

    private String gamesInfo(@NotNull Map<@NotNull String, @NotNull UnoGame> games) {
        StringBuilder res = new StringBuilder();
        res.append("List of games: ").append(System.lineSeparator());
        for (Map.Entry<String, UnoGame> entry : games.entrySet()) {
            res.append("Game id: ").append(entry.getKey()).append(" -> ").append(entry.getValue())
                .append(System.lineSeparator());
        }
        res.deleteCharAt(res.length() - 1);
        res.deleteCharAt(res.length() - 1);
        return res.toString();

    }
}
