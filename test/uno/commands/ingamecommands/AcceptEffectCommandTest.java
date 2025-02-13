package uno.commands.ingamecommands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.commands.UnoCommand;
import uno.deck.Deck;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AcceptEffectCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    void isExecutableEffectOnTurnTest() {
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(true);
        command = new AcceptEffectCommand(game, player);

        assertTrue(command.isExecutable(), "Command should be executable when an effect is active and it's the player's turn.");
    }

    @Test
    void isExecutableEffectNotOnTurnTest() {
        UnoPlayer player2 = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player2);
        when(game.isEffectAvailable()).thenReturn(true);
        command = new AcceptEffectCommand(game, player);

        assertFalse(command.isExecutable(), "Command should not be executable when an effect is active but it's not the player's turn.");
    }

    @Test
    void isExecutableNoEffectOnTurnTest() {
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(false);
        command = new AcceptEffectCommand(game, player);

        assertFalse(command.isExecutable(), "Command should not be executable when no effect is active, even if it's the player's turn.");
    }

    @Test
    void isExecutableNoEffectNotOnTurnTest() {
        UnoPlayer player2 = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player2);
        when(game.isEffectAvailable()).thenReturn(false);
        command = new AcceptEffectCommand(game, player);

        assertFalse(command.isExecutable(), "Command should not be executable when no effect is active and it's not the player's turn.");
    }

    @Test
    void executeCommandTest() throws IOException {
        when(game.getLastCard()).thenReturn(new UnoCard(UnoCardType.PLUS_TWO, Color.RED));
        Deck deck = mock(Deck.class);
        when(game.getDeck()).thenReturn(deck);
        when(game.getCurrentPlayer()).thenReturn(player);
        when(player.getInGamePlayerName()).thenReturn("test");
        doNothing().when(player).giveCards(any());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).setEffect(any(Boolean.class));
        doNothing().when(game).goToNextPlayer();

        command = new AcceptEffectCommand(game, player);
        command.execute();

        verify(game, times(1)).getLastCard();
        verify(game, times(1)).getDeck();
        verify(game, times(2)).getCurrentPlayer();
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(1)).setEffect(any(Boolean.class));
        verify(game, times(1)).goToNextPlayer();
    }
}
