package uno.server.handlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.game.UnoGame;
import uno.lobby.Lobby;
import uno.lobby.LobbyImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LobbyHandlerTest {
    private StringWriter stringWriter;
    private static BufferedReader in;
    private static Lobby lobby;

    private static LobbyHandler lobbyHandler;

    private static UnoGame mockedGame;

    private static PrintStream systemOut;

    @BeforeAll
    static void setUp() {
        systemOut = System.out;
        //nowhere
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));
    }

    @BeforeEach
    void setUpEach() {
        in = mock();
        stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        lobby = mock(LobbyImpl.class);

        lobbyHandler = new LobbyHandler(out, in, lobby, "Name");

        mockedGame = mock(UnoGame.class);
        when(mockedGame.toString()).thenReturn("The GAME");
    }

    @AfterAll
    static void tearDownEach() {
        System.setOut(systemOut);
    }

    @Test
    void testInvalidCommandForHandle() throws IOException {
        when(in.readLine()).thenReturn("invalid command123").thenReturn(null);
        lobbyHandler.handle();
        assertEquals("Unknown command: invalid command123" + System.lineSeparator() +
                "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator() + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testValidCommandCreateGame() throws IOException {
        when(in.readLine()).thenReturn("create-game --game-id=<id123> --number-of-players=<5>").thenReturn(null);
        doNothing().when(lobby).createGame(any(Integer.class), anyString(), anyString());
        lobbyHandler.handle();
        verify(lobby, times(1)).createGame(5, "id123", "Name");
    }

    @Test
    void testValidCommandCreateGameWithoutSpecifiedPlayersCountShouldMakeAGameWith2Players() throws IOException {
        when(in.readLine()).thenReturn("create-game --game-id=<id123>").thenReturn(null);
        doNothing().when(lobby).createGame(any(Integer.class), anyString(), anyString());
        lobbyHandler.handle();
        verify(lobby, times(1)).createGame(2, "id123", "Name");
    }

    @Test
    void testInvalidCreateGameWithoutArgs() throws IOException {
        when(in.readLine()).thenReturn("create-game").thenReturn(null);
        assertDoesNotThrow(() -> lobbyHandler.handle());
        verify(lobby, never()).createGame(any(Integer.class), any(String.class), any(String.class));
        assertEquals("Creating game must contain --game-id=<game-id>" + System.lineSeparator() +
                "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator() + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testSomeOneDisconnected() throws IOException {
        when(in.readLine()).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> lobbyHandler.handle());
    }

    @Test
    void testInvalidSummaryWithoutArgs() throws IOException {
        when(in.readLine()).thenReturn("summary").thenReturn(null);
        assertDoesNotThrow(() -> lobbyHandler.handle());
        verify(lobby, never()).getSummaryOfGame(any(String.class));
        assertEquals("Summary must contain --game-id=<game-id>" + System.lineSeparator() +
                "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator() + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testValidSummaryGame() throws IOException {
        when(in.readLine()).thenReturn("summary --game-id=<id123>").thenReturn(null);
        UnoGame mockedGame = mock();
        when(mockedGame.toString()).thenReturn("The GAME");
        when(lobby.getSummaryOfGame(any(String.class))).thenReturn(mockedGame);

        lobbyHandler.handle();
        verify(lobby, times(1)).getSummaryOfGame("id123");
        assertEquals("The GAME" + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testValidListGamesWithUnknownStatusShouldReturnAllGames() throws IOException {
        when(in.readLine()).thenReturn("list-games --status=<id123>").thenReturn(null);
        when(lobby.listGames(any(String.class))).thenReturn(Map.of("id123", mockedGame));
        lobbyHandler.handle();
        verify(lobby, times(1)).listGames(any());
        assertEquals("List of games: " + System.lineSeparator() +
                "Game id: id123 -> The GAME" + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testListGamesEmptyArgReturnsAllGames() throws IOException {
        when(in.readLine()).thenReturn("list-games").thenReturn(null);

        when(lobby.listGames(any(String.class))).thenReturn(Map.of("id123", mockedGame));

        lobbyHandler.handle();
        verify(lobby, times(1)).listGames("all");
        assertEquals("List of games: " + System.lineSeparator() +
                "Game id: id123 -> The GAME" + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testValidCommandJoinGameDefaultName() throws IOException {
        when(in.readLine()).thenReturn("join --game-id=<id123>").thenReturn(null);

        when(mockedGame.hasDisconnected(anyString())).thenThrow(RuntimeException.class);

        when(lobby.joinGame(eq("id123"), any())).thenReturn(mockedGame);
        lobbyHandler.handle();
        verify(lobby, times(1)).joinGame(eq("id123"), any());
        //invalid introduce to game, to not test PreGame
        assertEquals("null" + System.lineSeparator() +
                "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator() + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testValidCommandJoinGame() throws IOException {
        when(in.readLine()).thenReturn("join --game-id=<id123> --display-name=<display-name>").thenReturn(null);

        when(mockedGame.hasDisconnected(anyString())).thenThrow(RuntimeException.class);

        when(lobby.joinGame(eq("id123"), any())).thenReturn(mockedGame);
        lobbyHandler.handle();
        verify(lobby, times(1)).joinGame(eq("id123"), any());
        //invalid introduce to game, to not test PreGame
        assertEquals("null" + System.lineSeparator() +
                "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator() + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    void testInvalidArgsJoin() throws IOException {
        when(in.readLine()).thenReturn("join").thenReturn(null);
        assertDoesNotThrow(() -> lobbyHandler.handle());
        verify(lobby, never()).joinGame(any(), any());
        assertEquals("Joining game must contain --game-id=<game-id>" + System.lineSeparator() +
                "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator() + System.lineSeparator(), stringWriter.toString());
    }
}
