package uno.server.handlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.exceptions.signexceptions.RegisterException;
import uno.exceptions.signexceptions.SignException;
import uno.lobby.Lobby;
import uno.lobby.LobbyImpl;
import uno.server.PlayerDataBase;
import uno.server.PlayerDataBaseImpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientRequestHandlerTest {
    private PlayerDataBase playerDataBase;
    private Lobby lobby;
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    private ClientRequestHandler clientRequestHandler;

    Socket socket;

    private static PrintStream systemOut;

    @BeforeAll
    static void setUp() {
        systemOut = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) { }
        }));
    }

    @BeforeEach
    void setUpBeforeEach() {
        playerDataBase = mock(PlayerDataBaseImpl.class);
        lobby = mock(LobbyImpl.class);
        socket = mock(Socket.class);
    }

    @AfterAll
    static void tearDown() {
        System.setOut(systemOut);
    }

    @Test
    void testValidExit() throws IOException {
        try (InputStream clientIn = new ByteArrayInputStream(("exit" + System.lineSeparator()).getBytes());
             ByteArrayOutputStream clientOut = new ByteArrayOutputStream()) {
            when(socket.getInputStream()).thenReturn(clientIn);
            when(socket.getOutputStream()).thenReturn(clientOut);

            clientRequestHandler = new ClientRequestHandler(socket, lobby, playerDataBase, atomicBoolean);
            doNothing().when(socket).close();
            clientRequestHandler.run();

            assertNull(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientOut.toByteArray()))).readLine(),
                    "After 'exit' command, nothing should be printed to the client output.");
        }
    }

    @Test
    void testValidLoginDoNothingLogout() throws IOException, SignException {
        try (InputStream clientIn = new ByteArrayInputStream(("login --username=<username> --password=<password>" + System.lineSeparator()).getBytes());
             ByteArrayOutputStream clientOut = new ByteArrayOutputStream()) {
            when(socket.getInputStream()).thenReturn(clientIn);
            when(socket.getOutputStream()).thenReturn(clientOut);

            clientRequestHandler = new ClientRequestHandler(socket, lobby, playerDataBase, atomicBoolean);
            doNothing().when(socket).close();
            doNothing().when(playerDataBase).login(any(), any());
            doNothing().when(playerDataBase).logout(any());
            clientRequestHandler.run();

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientOut.toByteArray())));
            assertEquals("Hello username! Log-in successful!", reader.readLine(),
                    "The successful login should return a greeting and confirmation message.");
            assertEquals("username logged out!", reader.readLine(),
                    "The successful logout should return a logout confirmation message.");
            assertNull(reader.readLine(), "There should be no further output after logout.");
        }
    }

    @Test
    void testLoginInvalidArguments() throws IOException, SignException {
        try (InputStream clientIn = new ByteArrayInputStream(("login --username=<username>" + System.lineSeparator()).getBytes());
             ByteArrayOutputStream clientOut = new ByteArrayOutputStream()) {
            when(socket.getInputStream()).thenReturn(clientIn);
            when(socket.getOutputStream()).thenReturn(clientOut);

            clientRequestHandler = new ClientRequestHandler(socket, lobby, playerDataBase, atomicBoolean);
            doNothing().when(socket).close();
            clientRequestHandler.run();

            verify(playerDataBase, never()).login(any(), any());
            verify(playerDataBase, never()).logout(any());

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientOut.toByteArray())));
            assertEquals("When calling login command should have a username and a password", reader.readLine(),
                    "If login arguments are missing, it should print an error message indicating missing credentials.");
            assertNull(reader.readLine(), "There should be no further output after the error message.");
        }
    }

    @Test
    void testValidRegistration() throws IOException, SignException {
        try (InputStream clientIn = new ByteArrayInputStream(("register --username=<username> --password=<password>" + System.lineSeparator()).getBytes());
             ByteArrayOutputStream clientOut = new ByteArrayOutputStream()) {
            when(socket.getInputStream()).thenReturn(clientIn);
            when(socket.getOutputStream()).thenReturn(clientOut);

            clientRequestHandler = new ClientRequestHandler(socket, lobby, playerDataBase, atomicBoolean);
            doNothing().when(socket).close();
            doNothing().when(playerDataBase).register(any(), any());
            clientRequestHandler.run();

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientOut.toByteArray())));
            assertEquals("Registration successful!", reader.readLine(),
                    "After a successful registration, a success message should be printed.");
            assertNull(reader.readLine(), "There should be no further output after the success message.");
        }
    }

    @Test
    void testRegistrationInvalidArguments() throws IOException, SignException {
        try (InputStream clientIn = new ByteArrayInputStream(("register --password=<password>" + System.lineSeparator()).getBytes());
             ByteArrayOutputStream clientOut = new ByteArrayOutputStream()) {
            when(socket.getInputStream()).thenReturn(clientIn);
            when(socket.getOutputStream()).thenReturn(clientOut);

            clientRequestHandler = new ClientRequestHandler(socket, lobby, playerDataBase, atomicBoolean);
            doNothing().when(socket).close();
            clientRequestHandler.run();

            verify(playerDataBase, never()).register(any(), any());

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientOut.toByteArray())));
            assertEquals("When calling register command should have a username and a password", reader.readLine(),
                    "If registration arguments are missing, it should print an error message indicating missing credentials.");
            assertNull(reader.readLine(), "There should be no further output after the error message.");
        }
    }

    @Test
    void testSignExceptionRightMessage() throws IOException, SignException {
        try (InputStream clientIn = new ByteArrayInputStream(("register --username=<username> --password=<password>" + System.lineSeparator()).getBytes());
             ByteArrayOutputStream clientOut = new ByteArrayOutputStream()) {
            when(socket.getInputStream()).thenReturn(clientIn);
            when(socket.getOutputStream()).thenReturn(clientOut);

            clientRequestHandler = new ClientRequestHandler(socket, lobby, playerDataBase, atomicBoolean);
            doNothing().when(socket).close();
            doThrow(new RegisterException("Error Registration")).when(playerDataBase).register(any(), any());

            assertDoesNotThrow(() -> clientRequestHandler.run(),
                    "Running the client handler after a register exception should not throw additional exceptions.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientOut.toByteArray())));
            assertEquals("Error Registration", reader.readLine(),
                    "The exception thrown during registration should print the correct error message.");
            assertNull(reader.readLine(), "There should be no further output after the error message.");
        }
    }
}
