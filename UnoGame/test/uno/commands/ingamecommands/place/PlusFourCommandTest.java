package uno.commands.ingamecommands.place;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.commands.UnoCommand;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlusFourCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    public void constructorThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlusFourCommand(game, player, 1, Color.WILD),
                "Expected IllegalArgumentException when creating PlusFourCommand with WILD color.");
    }

    @Test
    public void constructorDoesNotThrow() {
        assertDoesNotThrow(() -> new PlusFourCommand(game, player, 1, Color.BLUE),
                "PlusFourCommand should be created successfully with a valid color.");
    }

    @Test
    void isExecutableNotOnTurnTest() {
        UnoPlayer player2 = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player2);
        command = new PlusFourCommand(game, player, 1, Color.BLUE);

        assertFalse(command.isExecutable(), "Command should not be executable when it's not the player's turn.");
    }

    @Test
    void isExecutableCardNotValidTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.TWO, Color.RED)));
        when(game.getCurrentPlayer()).thenReturn(player);
        command = new PlusFourCommand(game, player, 3, Color.BLUE);

        assertFalse(command.isExecutable(), "Command should not be executable when the selected card ID is invalid.");
    }

    @Test
    void isExecutableEffectAvailableTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.TWO, Color.RED)));
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(true);
        command = new PlusFourCommand(game, player, 1, Color.BLUE);

        assertFalse(command.isExecutable(), "Command should not be executable when an effect is already available.");
    }

    @Test
    void isExecutableNotPlusFourCardTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.TWO, Color.RED)));
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(false);
        command = new PlusFourCommand(game, player, 1, Color.BLUE);

        assertFalse(command.isExecutable(), "Command should not be executable when the selected card is not a PLUS_FOUR card.");
    }

    @Test
    void isExecutableTrueTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.PLUS_FOUR, Color.WILD)));
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(false);
        command = new PlusFourCommand(game, player, 1, Color.BLUE);

        assertTrue(command.isExecutable(), "Command should be executable when the player has a valid PLUS_FOUR card and no effect is active.");
    }

    @Test
    void executeTest() {
        command = new PlusFourCommand(game, player, 1, Color.BLUE);
        doNothing().when(game).setColor(Color.BLUE);
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).setEffect(true);
        when(player.takeCard(any(Integer.class))).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(),
                "Executing the command should throw IllegalArgumentException when an invalid card index is used.");

        verify(game, times(1)).setColor(Color.BLUE);
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(1)).setEffect(true);
    }
}
