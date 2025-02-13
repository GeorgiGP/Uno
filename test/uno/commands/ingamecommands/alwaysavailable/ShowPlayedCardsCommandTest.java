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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShowPlayedCardsCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
        command = new ShowPlayedCardsCommand(game, player);
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
        ShowPlayedCardsCommand command = new ShowPlayedCardsCommand(game, playerStub);
        when(game.getPlayedHistory()).thenReturn(
                List.of(new UnoCard(UnoCardType.ZERO, Color.RED), new UnoCard(UnoCardType.ONE, Color.BLUE)));

        command.execute();

        String expectedOutput = "played :0 >> Type: ZERO + Color: \u001B[31mRED\u001B[0m\u001B[0m" + System.lineSeparator() +
                "played :1 >> Type: ONE + Color: \u001B[34mBLUE\u001B[0m\u001B[0m" + System.lineSeparator();
        assertEquals(expectedOutput, out.toString(), "The output should correctly show the played cards and their respective colors.");
    }
}
