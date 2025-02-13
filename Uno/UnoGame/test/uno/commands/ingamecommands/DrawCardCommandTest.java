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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DrawCardCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    void isExecutableNotOnTurnTest() {
        UnoPlayer player2 = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player2);
        command = new DrawCardCommand(game, player);

        assertFalse(command.isExecutable(), "Command should not be executable when it's not the player's turn.");
    }

    @Test
    void isExecutableThereIsEffectTest() {
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(true);
        command = new DrawCardCommand(game, player);

        assertFalse(command.isExecutable(), "Command should not be executable when an effect is active.");
    }

    @Test
    void isExecutableCanPlayCardTest() {
        when(game.getCurrentPlayer()).thenReturn(player);
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ZERO, Color.BLUE), new UnoCard(UnoCardType.ONE, Color.RED)));
        when(game.isEffectAvailable()).thenReturn(false);
        when(game.getLastCard()).thenReturn(new UnoCard(UnoCardType.ONE, Color.GREEN));
        command = new DrawCardCommand(game, player);

        assertFalse(command.isExecutable(), "Command should not be executable when the player has a playable card.");
    }

    @Test
    void isExecutableCanNotPlayCardTest() {
        when(game.getCurrentPlayer()).thenReturn(player);
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ZERO, Color.BLUE), new UnoCard(UnoCardType.ONE, Color.RED)));
        when(game.isEffectAvailable()).thenReturn(false);
        when(game.getLastCard()).thenReturn(new UnoCard(UnoCardType.THREE, Color.GREEN));
        command = new DrawCardCommand(game, player);

        assertTrue(command.isExecutable(), "Command should be executable when the player has no playable card.");
    }

    @Test
    void executeTest() throws IOException {
        Deck deck = mock(Deck.class);
        command = new DrawCardCommand(game, player);
        when(game.getDeck()).thenReturn(deck);
        when(game.getCurrentPlayer()).thenReturn(player);

        doNothing().when(player).giveCard(any());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).goToNextPlayer();

        command.execute();

        verify(player, times(1)).giveCard(any());
        verify(deck, times(1)).drawCard();
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(1)).goToNextPlayer();
    }
}
