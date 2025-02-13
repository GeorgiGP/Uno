package uno.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.exceptions.NameTakenException;
import uno.commands.UnoCommand;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnoGameTest {
    private UnoGame game;
    private UnoPlayer player1, player2;

    @BeforeEach
    void setUp() {
        game = new UnoGame(4, "Creator");
        player1 = mock(UnoPlayer.class);
        player2 = mock(UnoPlayer.class);
        when(player1.getInGamePlayerName()).thenReturn("Player1");
        when(player1.getRegistrationPlayerName()).thenReturn("Player1");
        when(player2.getInGamePlayerName()).thenReturn("Player2");
        when(player2.getRegistrationPlayerName()).thenReturn("Player2");
    }

    @Test
    void addPlayerTest() {
        assertTrue(game.addPlayer(player1), "The player should be added successfully to the game.");
        assertEquals(1, game.getActivePlayers().size(), "The active player count should be 1 after adding a player.");
    }

    @Test
    void addPlayerGameNotAvailableTest() throws IOException {
        UnoCommand command = mock(UnoCommand.class);
        UnoPlayer player = mock(UnoPlayer.class);
        when(command.isExecutable()).thenReturn(false);
        doNothing().when(player).sendMessage(any());

        game.receiveCommand(command, player); // to simulate a game with status ended

        assertFalse(game.addPlayer(player1), "The player should not be added when the game status is not available.");
    }

    @Test
    void addPlayerWithDuplicateNameTest() {
        game.addPlayer(player1);
        when(player2.getInGamePlayerName()).thenReturn("Player1");
        assertThrows(NameTakenException.class, () -> game.addPlayer(player2),
                "A NameTakenException should be thrown if a player with the same name tries to join.");
    }

    @Test
    void startGameWithNotEnoughPlayersTest() {
        game.startGame();
        assertEquals(GameStatus.AVAILABLE, game.getStatus(),
                "The game should remain in 'AVAILABLE' status when there are not enough players.");
    }

    @Test
    void startGameTest() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.startGame();
        assertEquals(GameStatus.STARTED, game.getStatus(),
                "The game status should change to 'STARTED' when there are enough players and the game is started.");
    }

    @Test
    void hasDisconnectedTest() {
        game.putDisconnected("Player1", player1);
        assertTrue(game.hasDisconnected("Player1"), "The game should report that Player1 has disconnected.");
    }

    @Test
    void reconnectTest() {
        game.putDisconnected("Player1", player1);
        UnoPlayer reconnectedPlayer = game.reconnect("Player1");
        assertEquals(player1, reconnectedPlayer, "The player who was disconnected should be reconnected successfully.");
    }

    @Test
    void changeDirectionTest() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.changeDirection();
        game.goToNextPlayer();
        // No assertion, but ensures no exceptions occur when changing direction and going to the next player.
    }

    @Test
    void setAndGetColorTest() {
        game.setColor(Color.RED);
        assertEquals(Color.RED, game.getColor(), "The color of the game should be set and retrieved correctly.");
    }

    @Test
    void addToPlayedCardsHistoryTest() {
        UnoCard card = mock(UnoCard.class);
        game.addToPlayedCardsHistory(card);

        assertEquals(1, game.getPlayedHistory().size(),
                "The played history should contain one card after adding a card to the history.");
    }

    @Test
    void receiveCommandTest() throws IOException {
        UnoCommand command = mock(UnoCommand.class);
        when(command.isExecutable()).thenReturn(true);

        game.receiveCommand(command, player1);
        verify(command, times(1)).execute();
    }
}
