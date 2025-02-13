package uno.commands.factory;

import org.junit.jupiter.api.Test;
import uno.commands.ingamecommands.AcceptEffectCommand;
import uno.commands.ingamecommands.DrawCardCommand;
import uno.commands.ingamecommands.alwaysavailable.ShowHandCommand;
import uno.commands.ingamecommands.alwaysavailable.ShowLastCard;
import uno.commands.ingamecommands.alwaysavailable.ShowPlayedCardsCommand;
import uno.commands.changingplayermode.LeaveCommand;
import uno.commands.changingplayermode.SpectatingCommand;
import uno.commands.ingamecommands.place.ChooseColorCommand;
import uno.commands.ingamecommands.place.PlayNormalCardCommand;
import uno.commands.ingamecommands.place.PlusFourCommand;
import uno.deck.card.Color;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static uno.commands.factory.CommandsFactory.*;

public class CommandsFactoryTest {
    private final UnoPlayer player = mock();
    private final UnoGame game = mock();

    @Test
    void testExtractValidCardId() {
        assertEquals(2, extractCardId("--card-id=<2>"), "Failed to extract a valid card ID.");
    }

    @Test
    void testExtractInvalidCardIdWords() {
        assertEquals(-1, extractCardId("--card-id=<A5>"), "Card ID extraction should fail for non-numeric values.");
    }

    @Test
    void testExtractInvalidCardIdNegative() {
        assertEquals(-1, extractCardId("--card-id=<-2>"), "Card ID extraction should fail for negative values.");
    }

    @Test
    void testExtractInvalidCardIdNotInPattern() {
        assertEquals(-1, extractCardId("5"), "Card ID extraction should fail for incorrect patterns.");
    }

    @Test
    void testExtractInvalidColorNumber() {
        assertEquals(Color.WILD, extractColor("--color=<4>"), "Color extraction should default to WILD for invalid color numbers.");
    }

    @Test
    void testExtractInvalidColorNotInPattern() {
        assertEquals(Color.WILD, extractColor("--card-id=<blue>"), "Color extraction should default to WILD for incorrectly formatted input.");
    }

    @Test
    void testExtractValidColor() {
        assertEquals(Color.RED, extractColor("--color=<red>"), "Failed to extract a valid color.");
    }

    @Test
    void testExtractCommandShowHand() {
        assertEquals(ShowHandCommand.class, getCommand("show-hand", game, player).getClass(), "Failed to extract ShowHandCommand.");
    }

    @Test
    void testExtractCommandShowLastCard() {
        assertEquals(ShowLastCard.class, getCommand("show-last-card", game, player).getClass(), "Failed to extract ShowLastCard.");
    }

    @Test
    void testExtractCommandDrawCard() {
        assertEquals(DrawCardCommand.class, getCommand("draw", game, player).getClass(), "Failed to extract DrawCardCommand.");
    }

    @Test
    void testExtractCommandShowPlayedCardsCommand() {
        assertEquals(ShowPlayedCardsCommand.class, getCommand("show-played-cards", game, player).getClass(), "Failed to extract ShowPlayedCardsCommand.");
    }

    @Test
    void testExtractCommandAcceptEffect() {
        assertEquals(AcceptEffectCommand.class, getCommand("accept-effect", game, player).getClass(), "Failed to extract AcceptEffectCommand.");
    }

    @Test
    void testExtractInvalidCommandPlayNormalCard() {
        assertThrows(IllegalArgumentException.class,
                () -> getCommand("play", game, player).getClass(),
                "Expected IllegalArgumentException for invalid PlayNormalCard command.");
    }

    @Test
    void testExtractValidCommandPlayNormalCard() {
        assertEquals(PlayNormalCardCommand.class, getCommand("play --card-id=<0>", game, player).getClass(), "Failed to extract PlayNormalCardCommand.");
    }

    @Test
    void testExtractInvalidCommandPlayWildCard() {
        assertThrows(IllegalArgumentException.class,
                () -> getCommand("play-choose", game, player).getClass(),
                "Expected IllegalArgumentException for invalid PlayWildCard command.");
    }

    @Test
    void testExtractValidCommandPlayWildCard() {
        assertEquals(ChooseColorCommand.class, getCommand("play-choose --card-id=<0> --color=<green>", game, player).getClass(), "Failed to extract ChooseColorCommand.");
    }

    @Test
    void testExtractInvalidCommandPlayWildPlus4Card() {
        assertThrows(IllegalArgumentException.class,
                () -> getCommand("play-plus-four", game, player).getClass(),
                "Expected IllegalArgumentException for invalid PlayWildPlus4 command.");
    }

    @Test
    void testExtractValidCommandPlayWildPlus4Card() {
        assertEquals(PlusFourCommand.class, getCommand("play-plus-four --card-id=<0> --color=<blue>", game, player).getClass(), "Failed to extract PlusFourCommand.");
    }

    @Test
    void testExtractInvalidCommandChooseColorWild() {
        assertThrows(IllegalArgumentException.class,
                () -> getCommand("play-choose --card-id=<0> --color=<wild>", game, player).getClass(),
                "Expected IllegalArgumentException for invalid ChooseColorWild command.");
    }

    @Test
    void testExtractCommandLeave() {
        assertEquals(LeaveCommand.class, getCommand("leave", game, player).getClass(), "Failed to extract LeaveCommand.");
    }

    @Test
    void testExtractCommandSpectate() {
        assertEquals(SpectatingCommand.class, getCommand("spectate", game, player).getClass(), "Failed to extract SpectatingCommand.");
    }

    @Test
    void testExtractCommandInvalid() {
        assertThrows(IllegalStateException.class,
                () -> getCommand("invalid", game, player).getClass(),
                "Expected IllegalStateException for an invalid command.");
    }
}
