package uno.commands.ingamecommands.alwaysavailable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.commands.UnoCommand;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShowLastCardTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
        command = new ShowLastCard(game, player);
    }

    @Test
    void isExecutablePositiveTest() {
        when(game.getCurrentPlayer()).thenReturn(player);
        assertTrue(command.isExecutable(), "The command should be executable when the current player is the player.");
    }

    @Test
    void isExecutableNegativeTest() {
        when(player.spectating()).thenReturn(true);
        when(game.getCurrentPlayer()).thenReturn(player);
        assertFalse(command.isExecutable(), "The command should not be executable when the player is spectating.");
    }

    @Test
    void executeTest() {
        StringWriter out = new StringWriter();
        UnoPlayer playerStub = new UnoPlayer(new PrintWriter(out), mock(BufferedReader.class), "name1", "name2");
        ShowLastCard command = new ShowLastCard(game, playerStub);
        UnoCard lastStub = new UnoCard(UnoCardType.ONE, Color.GREEN);
        when(game.getLastCard()).thenReturn(lastStub);
        when(game.getColor()).thenReturn(Color.GREEN);
        command.execute();

        String expectedOutput = "last played card: " + game.getLastCard() +
                "  Color is: " + game.getColor() + System.lineSeparator();
        assertEquals(expectedOutput, out.toString(), "The execute method should display the correct last played card and its color.");
    }
}
