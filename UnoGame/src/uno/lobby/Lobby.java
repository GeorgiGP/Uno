package uno.lobby;

import org.jetbrains.annotations.NotNull;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.util.Map;

public interface Lobby {
    void createGame(int maxPlayersCount, @NotNull String idGame, @NotNull String creator);

    UnoGame joinGame(@NotNull String idGame, @NotNull UnoPlayer player);

    Map<String, UnoGame> listGames(@NotNull String option);

    UnoGame getSummaryOfGame(@NotNull String idGame);
}
