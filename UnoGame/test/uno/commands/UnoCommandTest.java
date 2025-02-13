package uno.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uno.commands.ingamecommands.alwaysavailable.ShowLastCard;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnoCommandTest {

    private UnoGame game;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        UnoPlayer player = mock(UnoPlayer.class);
        command = new ShowLastCard(game, player);
    }

    @Test
    void canPlayCardSameColorTest() {
        UnoCard newCard = new UnoCard(UnoCardType.ONE, Color.BLUE);

        when(game.getColor()).thenReturn(Color.BLUE);

        assertTrue(command.canPlayCard(newCard), "The card should be playable when it matches the color of the last played card.");
    }

    @Test
    void canPlayCardWildTest() {
        UnoCard newCard = new UnoCard(UnoCardType.WILD, Color.WILD);

        when(game.getColor()).thenReturn(Color.BLUE);

        assertTrue(command.canPlayCard(newCard), "A wild card should always be playable regardless of the current color.");
    }

    @Test
    void canPlayCardSameValueTest() {
        UnoCard newCard = new UnoCard(UnoCardType.THREE, Color.BLUE);
        UnoCard lastPlayed = new UnoCard(UnoCardType.THREE, Color.GREEN);

        when(game.getColor()).thenReturn(Color.GREEN);
        when(game.getLastCard()).thenReturn(lastPlayed);

        assertTrue(command.canPlayCard(newCard), "The card should be playable when it matches the value of the last played card.");
    }

    @Test
    void canPlayCardLastWildTest() {
        UnoCard newCard = new UnoCard(UnoCardType.THREE, Color.BLUE);
        UnoCard lastPlayed = new UnoCard(UnoCardType.WILD, Color.WILD);

        when(game.getColor()).thenReturn(Color.WILD);
        when(game.getLastCard()).thenReturn(lastPlayed);

        assertTrue(command.canPlayCard(newCard), "Any card should be playable after a wild card.");
    }

    @Test
    void canPlayCardCannotTest() {
        UnoCard newCard = new UnoCard(UnoCardType.THREE, Color.BLUE);
        UnoCard lastPlayed = new UnoCard(UnoCardType.FIVE, Color.GREEN);

        when(game.getColor()).thenReturn(Color.GREEN);
        when(game.getLastCard()).thenReturn(lastPlayed);

        assertFalse(command.canPlayCard(newCard), "The card should not be playable when it neither matches the color nor the value of the last played card.");
    }

    @Test
    void isOnTurnTest(){
        UnoPlayer player = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player);
        assertTrue(command.isOnTurn(player), "The command should return true if it's the player's turn.");
    }

    @Test
    void isOnTurnNotTest(){
        UnoPlayer player = mock(UnoPlayer.class);
        UnoPlayer player2 = mock(UnoPlayer.class);
        when(game.getCurrentPlayer()).thenReturn(player2);
        assertFalse(command.isOnTurn(player), "The command should return false if it's not the player's turn.");
    }
}
