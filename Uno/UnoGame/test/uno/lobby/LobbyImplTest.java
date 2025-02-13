package uno.lobby;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uno.exceptions.IDNotExistException;
import uno.exceptions.IDTakenException;
import uno.game.GameStatus;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LobbyImplTest {
    private final static UnoGame mockedGameAvailable = mock(UnoGame.class);
    private final static UnoGame mockedGameStarted = mock(UnoGame.class);
    private final static UnoGame mockedGameEnded = mock(UnoGame.class);
    private Map<String, UnoGame> map;

    @BeforeAll
    static void setUp() {
        when(mockedGameAvailable.getStatus()).thenReturn(GameStatus.AVAILABLE);
        when(mockedGameStarted.getStatus()).thenReturn(GameStatus.STARTED);
        when(mockedGameEnded.getStatus()).thenReturn(GameStatus.ENDED);
        when(mockedGameAvailable.toString()).thenReturn("Available Game");
        when(mockedGameStarted.toString()).thenReturn("Started Game");
        when(mockedGameEnded.toString()).thenReturn("Ended Game");
    }

    @Test
    void createGameExistingIdTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        LobbyImpl lobby = new LobbyImpl(map);
        assertThrows(IDTakenException.class, () ->
                        lobby.createGame(3, mockedGameAvailable.toString(), "creator"),
                "Creating a game with an existing ID should throw IDTakenException.");
    }

    @Test
    void createGameLessThanMinPlayersTest() {
        LobbyImpl lobby = new LobbyImpl();
        assertThrows(IllegalArgumentException.class, () ->
                        lobby.createGame(1, mockedGameAvailable.toString(), "creator"),
                "Creating a game with fewer than the minimum number of players should throw IllegalArgumentException.");
    }

    @Test
    void createGameMoreThanMaxPlayersTest() {
        LobbyImpl lobby = new LobbyImpl();
        assertThrows(IllegalArgumentException.class, () ->
                        lobby.createGame(11, mockedGameAvailable.toString(), "creator"),
                "Creating a game with more than the maximum number of players should throw IllegalArgumentException.");
    }

    @Test
    void createGameValidTest() {
        LobbyImpl lobby = new LobbyImpl();
        assertDoesNotThrow(() -> lobby.createGame(3, mockedGameAvailable.toString(), "creator"),
                "Creating a valid game should not throw any exception.");
    }

    @Test
    void joinGameNoSuchGameTest() {
        LobbyImpl lobby = new LobbyImpl();
        assertThrows(IDNotExistException.class, () -> lobby.joinGame("abcdefgh", mock(UnoPlayer.class)),
                "Attempting to join a non-existing game should throw IDNotExistException.");
    }

    @Test
    void joinGameValidTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        LobbyImpl lobby = new LobbyImpl(map);
        assertDoesNotThrow(() -> lobby.joinGame(mockedGameAvailable.toString(), mock(UnoPlayer.class)),
                "Joining an existing game should not throw any exception.");
    }

    @Test
    void getSummaryOfGameNoSuchGameTest() {
        LobbyImpl lobby = new LobbyImpl();
        assertThrows(IDNotExistException.class, () -> lobby.getSummaryOfGame(mockedGameAvailable.toString()),
                "Attempting to get the summary of a non-existing game should throw IDNotExistException.");
    }

    @Test
    void getSummaryOfGameValidTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        LobbyImpl lobby = new LobbyImpl(map);
        assertDoesNotThrow(() -> lobby.getSummaryOfGame(mockedGameAvailable.toString()),
                "Getting the summary of an existing game should not throw any exception.");
    }

    @Test
    void listGamesAvailableTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        map.put(mockedGameStarted.toString(), mockedGameStarted);
        map.put(mockedGameEnded.toString(), mockedGameEnded);
        LobbyImpl lobby = new LobbyImpl(map);
        assertEquals(lobby.listGames("Available"),
                Map.of(mockedGameAvailable.toString(), mockedGameAvailable),
                "The list of 'Available' games should return only the available games.");
    }

    @Test
    void listGamesStartedTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        map.put(mockedGameStarted.toString(), mockedGameStarted);
        map.put(mockedGameEnded.toString(), mockedGameEnded);
        LobbyImpl lobby = new LobbyImpl(map);
        assertEquals(lobby.listGames("Started"),
                Map.of(mockedGameStarted.toString(), mockedGameStarted),
                "The list of 'Started' games should return only the started games.");
    }

    @Test
    void listGamesEndedTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        map.put(mockedGameStarted.toString(), mockedGameStarted);
        map.put(mockedGameEnded.toString(), mockedGameEnded);
        LobbyImpl lobby = new LobbyImpl(map);
        assertEquals(lobby.listGames("Ended"),
                Map.of(mockedGameEnded.toString(), mockedGameEnded),
                "The list of 'Ended' games should return only the ended games.");
    }

    @Test
    void listGamesRandomStringTest() {
        map = new HashMap<>();
        map.put(mockedGameAvailable.toString(), mockedGameAvailable);
        map.put(mockedGameStarted.toString(), mockedGameStarted);
        map.put(mockedGameEnded.toString(), mockedGameEnded);
        LobbyImpl lobby = new LobbyImpl(map);
        assertEquals(lobby.listGames("abcdefg"),
                Map.of(mockedGameEnded.toString(), mockedGameEnded,
                        mockedGameStarted.toString(), mockedGameStarted,
                        mockedGameAvailable.toString(), mockedGameAvailable),
                "The list of games with an invalid status string should return all games.");
    }
}
