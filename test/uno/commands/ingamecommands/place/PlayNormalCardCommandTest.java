package uno.commands.ingamecommands.place;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayNormalCardCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private PlayNormalCardCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    void isExecutableNotOnTurnTest() {
        UnoPlayer player2 = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player2);
        command = new PlayNormalCardCommand(game, player, 1);

        assertFalse(command.isExecutable(), "Command should not be executable when it's not the player's turn.");
    }

    @Test
    void isExecutableCardNotValidTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.TWO, Color.RED)));
        when(game.getCurrentPlayer()).thenReturn(player);
        command = new PlayNormalCardCommand(game, player, 3);

        assertFalse(command.isExecutable(), "Command should not be executable when the selected card ID is invalid.");
    }

    @Test
    void isExecutableEffectAvailableTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.TWO, Color.RED)));
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(true);
        command = new PlayNormalCardCommand(game, player, 1);

        assertFalse(command.isExecutable(), "Command should not be executable when an effect is already available.");
    }

    @Test
    void isExecutableWildCardTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(false);
        command = new PlayNormalCardCommand(game, player, 1);

        assertFalse(command.isExecutable(), "Command should not be executable when the selected card is a WILD card.");
    }

    @Test
    void isExecutableValidTest() {
        when(game.getLastCard()).thenReturn(new UnoCard(UnoCardType.ONE, Color.GREEN));
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        when(game.getCurrentPlayer()).thenReturn(player);
        when(game.isEffectAvailable()).thenReturn(false);
        command = new PlayNormalCardCommand(game, player, 0);

        assertTrue(command.isExecutable(), "Command should be executable when the player has a matching card and no effect is active.");
    }

    @Test
    void executeNormalTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ONE, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        command = new PlayNormalCardCommand(game, player, 0);
        doNothing().when(game).setColor(Color.GREEN);
        when(game.getDeck()).thenThrow(new RuntimeException());

        command.isCardValid();
        assertThrows(RuntimeException.class, () -> command.execute(), "Executing the command should throw RuntimeException for an invalid deck state.");

        verify(game, times(1)).setColor(Color.GREEN);
    }

    @Test
    void executeSkipTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.SKIP, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        command = new PlayNormalCardCommand(game, player, 0);
        doNothing().when(game).setColor(Color.GREEN);
        when(game.getDeck()).thenThrow(new RuntimeException());

        command.isCardValid();
        assertThrows(RuntimeException.class, () -> command.execute(), "Executing a Skip card should throw RuntimeException for an invalid deck state.");

        verify(game, times(1)).setColor(Color.GREEN);
    }

    @Test
    void executePlusTwoTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.PLUS_TWO, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        command = new PlayNormalCardCommand(game, player, 0);
        doNothing().when(game).setColor(Color.GREEN);
        when(game.getDeck()).thenThrow(new RuntimeException());

        command.isCardValid();
        assertThrows(RuntimeException.class, () -> command.execute(), "Executing a Plus Two card should throw RuntimeException for an invalid deck state.");

        verify(game, times(1)).setColor(Color.GREEN);
        verify(game, times(1)).setEffect(true);
    }

    @Test
    void executeReverseNormalTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.REVERSE, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        command = new PlayNormalCardCommand(game, player, 0);
        doNothing().when(game).setColor(Color.GREEN);
        when(game.getDeck()).thenThrow(new RuntimeException());
        when(game.getActivePlayers()).thenReturn(
                List.of(mock(UnoPlayer.class), mock(UnoPlayer.class), mock(UnoPlayer.class)));

        command.isCardValid();
        assertThrows(RuntimeException.class, () -> command.execute(), "Executing a Reverse card should throw RuntimeException for an invalid deck state.");

        verify(game, times(1)).setColor(Color.GREEN);
        verify(game, times(1)).changeDirection();
    }

    @Test
    void executeReverseTwoPlayersTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.REVERSE, Color.GREEN), new UnoCard(UnoCardType.WILD, Color.WILD)));
        command = new PlayNormalCardCommand(game, player, 0);
        doNothing().when(game).setColor(Color.GREEN);
        when(game.getDeck()).thenThrow(new RuntimeException());
        when(game.getActivePlayers()).thenReturn(
                List.of(mock(UnoPlayer.class), mock(UnoPlayer.class)));

        command.isCardValid();
        assertThrows(RuntimeException.class, () -> command.execute(), "Executing a Reverse card should not change direction when only two players are present.");

        verify(game, times(1)).setColor(Color.GREEN);
        verify(game, times(0)).changeDirection();
    }
}
