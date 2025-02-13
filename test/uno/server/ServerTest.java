package uno.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private Server server;

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        server = new Server();
    }

    @Test
    void testStopMethod() throws IOException {
        PrintStream curSystemOut = System.out;
        File fileResult = Files.createTempFile("someFile", "txt").toFile();
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileResult))) {
            System.setOut(new PrintStream(outputStream));

            assertDoesNotThrow(server::stop, "Server stop method should not throw any exception");

            outputStream.flush();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileResult))) {
                assertEquals("Server stopped", reader.readLine(), "Expected message 'Server stopped' after stopping the server");
                assertEquals("Server hasn't started yet", reader.readLine(), "Expected message 'Server hasn't started yet' when server is not started yet");
            }
            System.setOut(curSystemOut);
        }
        fileResult.deleteOnExit();
    }
}
