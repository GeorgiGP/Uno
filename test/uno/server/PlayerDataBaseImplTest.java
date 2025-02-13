package uno.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uno.checksumcalculator.SHA256ChecksumCalculator;
import uno.exceptions.signexceptions.InvalidPasswordException;
import uno.exceptions.signexceptions.LoginException;
import uno.exceptions.signexceptions.LogoutException;
import uno.exceptions.signexceptions.RegisterException;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerDataBaseImplTest {
    private PlayerDataBase db;

    @BeforeEach
    void setUp() {
        Map<String, String> map = new HashMap<>();
        map.put("username", new SHA256ChecksumCalculator().calculate(new ByteArrayInputStream("password".getBytes())));
        db = new PlayerDataBaseImpl(map);
    }

    @Test
    void testLoginValidLogoutValid() {
        assertDoesNotThrow(() -> db.login("username", "password"), "Login should succeed with valid username and password");
        assertDoesNotThrow(() -> db.logout("username"), "Logout should succeed for logged-in user");
    }

    @Test
    void testLogoutNotLogged() {
        assertDoesNotThrow(() -> db.login("username", "password"), "Login should succeed with valid username and password");
        assertThrows(LogoutException.class, () -> db.logout("username2"), "Logout should fail if the user is not logged in");
    }

    @Test
    void testLoginTwice() {
        assertDoesNotThrow(() -> db.login("username", "password"), "Login should succeed the first time");
        assertThrows(LoginException.class, () -> db.login("username", "password"), "Login should fail if the user is already logged in");
    }

    @Test
    void testLoginInvalidPassword() {
        assertThrows(InvalidPasswordException.class, () -> db.login("username", "password123"), "Login should fail if the password is incorrect");
    }

    @Test
    void testLoginButNeverRegistered() {
        assertThrows(RegisterException.class, () -> db.login("username123123", "password"), "Login should fail if the username has not been registered");
    }

    @Test
    void testRegistrationValid() {
        assertDoesNotThrow(() -> db.register("username2", "password"), "Registration should succeed with a new username and password");
    }

    @Test
    void testRegistrationAlreadyExists() {
        assertThrows(RegisterException.class, () -> db.register("username", "password"), "Registration should fail if the username already exists");
    }

    @Test
    void testRegistrationInvalidNameWithWhiteSpaces() {
        assertThrows(IllegalArgumentException.class, () -> db.register("user name", "password"), "Registration should fail if the username contains white spaces");
    }

    @Test
    void testRegistrationInvalidPasswordWithWhiteSpaces() {
        assertThrows(IllegalArgumentException.class, () -> db.register("username3", "pass word"), "Registration should fail if the password contains white spaces");
    }

    @Test
    void testRegistrationInvalidNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.register("", "pass"), "Registration should fail if the username is empty");
    }

    @Test
    void testRegistrationInvalidPasswordEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.register("name", ""), "Registration should fail if the password is empty");
    }
}
