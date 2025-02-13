package uno.server;

import org.jetbrains.annotations.NotNull;
import uno.checksumcalculator.ChecksumCalculator;
import uno.checksumcalculator.SHA256ChecksumCalculator;
import uno.exceptions.signexceptions.InvalidPasswordException;
import uno.exceptions.signexceptions.LoginException;
import uno.exceptions.signexceptions.LogoutException;
import uno.exceptions.signexceptions.RegisterException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class PlayerDataBaseImpl implements PlayerDataBase {
    private static final String ATTRIBUTES_SEPARATOR = " ";
    private static final Path ACCOUNTS_INFO = Path.of("users.txt");
    @NotNull
    private final Map<@NotNull String, @NotNull String> playersInfoRegistries;
    @NotNull
    private final Set<@NotNull String> loggedPlayers;

    private static final ChecksumCalculator CALCULATOR = new SHA256ChecksumCalculator();

    public PlayerDataBaseImpl(@NotNull Map<@NotNull String, @NotNull String> playersInfoRegistries) {
        this.playersInfoRegistries = new ConcurrentHashMap<>(playersInfoRegistries);
        loggedPlayers = new ConcurrentSkipListSet<>();
    }

    @Override
    public void login(@NotNull String username, @NotNull String password) throws LoginException, RegisterException {
        if (!isRegistered(username)) {
            throw new RegisterException("Username " + username + " is not registered.");
        }
        if (loggedPlayers.contains(username)) {
            throw new LoginException("Username " + username + " is already logged.");
        }
        if (!playersInfoRegistries.get(username).equals(
                CALCULATOR.calculate(new ByteArrayInputStream(password.getBytes())))) {
            throw new InvalidPasswordException("Username " + username + " does not match password.");
        }
        loggedPlayers.add(username);
    }

    @Override
    public void logout(@NotNull String username) throws LogoutException {
        if (!loggedPlayers.contains(username)) {
            throw new LogoutException("Cannot logout username " + username + " that hasn't been logged.");
        }
        loggedPlayers.remove(username);
    }

    @Override
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(ACCOUNTS_INFO)) {
            for (Map.Entry<String, String> entry : playersInfoRegistries.entrySet()) {
                writer.write(entry.getKey() + ATTRIBUTES_SEPARATOR + entry.getValue() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load accounts info", e);
        }
    }

    @Override
    public void register(@NotNull String userName, @NotNull String password) throws RegisterException {
        userName = userName.strip();
        password = password.strip();
        if (isRegistered(userName)) {
            throw new RegisterException("User:" + userName + " already registered!");
        }
        if (userName.contains(" ") || password.contains(" ") || userName.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("User name and password contains whitespaces or are blank");
        }
        playersInfoRegistries.put(userName, CALCULATOR.calculate(new ByteArrayInputStream(password.getBytes())));
    }

    private boolean isRegistered(@NotNull String userName) {
        return playersInfoRegistries.containsKey(userName);
    }

    public static PlayerDataBaseImpl load() throws IOException {
        Map<String, String> result = new HashMap<>();
        if (Files.exists(ACCOUNTS_INFO)) {
            try (BufferedReader reader = Files.newBufferedReader(ACCOUNTS_INFO)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(ATTRIBUTES_SEPARATOR);
                    result.put(parts[0], parts[1]);
                }
            }
        }
        return new PlayerDataBaseImpl(result);
    }
}
