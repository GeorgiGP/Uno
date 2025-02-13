package uno.lobby;

import org.jetbrains.annotations.NotNull;
import uno.exceptions.IDNotExistException;
import uno.exceptions.IDTakenException;
import uno.game.GameStatus;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LobbyImpl implements Lobby {
    @NotNull
    private final Map<@NotNull String, @NotNull UnoGame> games;

    private static final int MAX_PLAYERS_SIZE = 9;
    private static final int MIN_PLAYERS_SIZE = 2;
    public LobbyImpl() {
        games = new ConcurrentHashMap<>();
    }

    public LobbyImpl(@NotNull Map<@NotNull String, @NotNull UnoGame> games) {
        this.games = new ConcurrentHashMap<>(games);
    }

    @Override
    public void createGame(int maxPlayersCount, @NotNull String idGame, @NotNull String creator) {
        if (games.containsKey(idGame)) {
            throw new IDTakenException("Game already exists with id: " + idGame);
        }
        if (!(MIN_PLAYERS_SIZE <= maxPlayersCount && maxPlayersCount <= MAX_PLAYERS_SIZE)) {
            throw new IllegalArgumentException("Max players count must be between " + MIN_PLAYERS_SIZE +
                    " and " + MAX_PLAYERS_SIZE);
        }
        games.put(idGame, new UnoGame(maxPlayersCount, creator));
    }

    @Override
    public UnoGame joinGame(@NotNull String idGame, @NotNull UnoPlayer player) {
        if (!games.containsKey(idGame)) {
            throw new IDNotExistException("Game does not exist with id: " + idGame);
        }
        return games.get(idGame);
    }

    @Override
    public Map<String, UnoGame> listGames(@NotNull String option) {
        GameStatus status;
        switch (option.toLowerCase().strip()) {
            case "started" -> status = GameStatus.STARTED;
            case "ended" -> status = GameStatus.ENDED;
            case "available" -> status = GameStatus.AVAILABLE;
            default -> {
                return Collections.unmodifiableMap(games);
            }
        }
        return games.entrySet().parallelStream()
                .filter(game -> game.getValue().getStatus().equals(status))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    @Override
    public UnoGame getSummaryOfGame(@NotNull String idGame) {
        if (!games.containsKey(idGame)) {
            throw new IDNotExistException("Game does not exist with id: " + idGame);
        }
        return games.get(idGame);
    }
}
