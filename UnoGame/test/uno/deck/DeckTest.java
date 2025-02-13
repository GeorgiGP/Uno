package uno.deck;

import org.junit.jupiter.api.Test;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.exceptions.EmptyDeckException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeckTest {

    static List<UnoCard> threeCards =
            List.of(new UnoCard(UnoCardType.ZERO, Color.GREEN), new UnoCard(UnoCardType.ONE, Color.GREEN),
                    new UnoCard(UnoCardType.REVERSE, Color.GREEN));

    static List<UnoCard> fourCards =
            List.of(new UnoCard(UnoCardType.PLUS_FOUR, Color.WILD), new UnoCard(UnoCardType.ZERO, Color.RED),
                    new UnoCard(UnoCardType.ONE, Color.RED),
                    new UnoCard(UnoCardType.SKIP, Color.RED));

    @Test
    void placeCardTest() {
        Deck deck = new Deck(threeCards, fourCards);
        UnoCard toPlace = new UnoCard(UnoCardType.SEVEN, Color.RED);
        deck.placeCard(toPlace);
        UnoCard lastPlaced = deck.peekLastPlacedCard();

        assertEquals(lastPlaced, toPlace, "The newly placed card expected to be " + toPlace + " but was " + lastPlaced);
    }

    @Test
    void drawCardNormalTest() {
        Deck deck = new Deck(threeCards, fourCards);
        UnoCard drawnCard = deck.drawCard();
        UnoCard expectedCard = new UnoCard(UnoCardType.REVERSE, Color.GREEN);
        assertEquals(drawnCard, expectedCard, "Expected card to be drawn " + expectedCard + " but was " + drawnCard);
    }

    @Test
    void drawCardEmptyDeckTest() {
        Deck deck = new Deck(Collections.emptyList(), fourCards);
        UnoCard drawnCard = deck.drawCard();
        UnoCard expectedCard = new UnoCard(UnoCardType.PLUS_FOUR, Color.WILD);

        assertEquals(drawnCard, expectedCard, "Expected card to be drawn " + expectedCard + " but was " + drawnCard);
    }

    @Test
    void drawCardThrowsEmptyDeckExceptionTest() {
        Deck deck = new Deck(Collections.emptyList(), fourCards);
        assertDoesNotThrow(deck::drawCard, "There are still cards to be drawn, no exception should be thrown");
        assertDoesNotThrow(deck::drawCard, "There are still cards to be drawn, no exception should be thrown");
        assertDoesNotThrow(deck::drawCard, "There are still cards to be drawn, no exception should be thrown");
        assertThrows(EmptyDeckException.class, deck::drawCard,
                "When the deck is empty and only 1 card is left in the played pile, an EmptyDeckException should be thrown");
    }

    @Test
    void drawCardsNegativeAmountTest() {
        Deck deck = new Deck(threeCards, fourCards);
        assertThrows(IllegalArgumentException.class, () -> deck.drawCards(-3),
                "When the amount of cards to draw is negative, IllegalArgumentException should be thrown");
    }

    @Test
    void drawCardsEmptyDeckExceptionTest() {
        Deck deck = new Deck(Collections.emptyList(), fourCards);
        assertThrows(EmptyDeckException.class, () -> deck.drawCards(7),
                "There are not enough cards to be drawn, EmptyDeckException should be thrown");
    }

    @Test
    void drawCardsNormalTest() {
        Deck deck = new Deck(threeCards, fourCards);
        assertDoesNotThrow(() -> deck.drawCards(5), "Deck has enough cards to be drawn, no exception should be thrown");
    }

    @Test
    void deckConstructorArgsTooSmallTest() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(threeCards, Collections.emptyList()),
                "IllegalArgumentException should be thrown because cards must be at least 4");
    }

    @Test
    void deckConstructorArgsEmptyPlayedPileTest() {
        assertDoesNotThrow(() -> new Deck(fourCards, Collections.emptyList()),
                "Constructor should not throw an exception when the played pile is empty");
    }

    @Test
    void deckConstructorArgsOkTest() {
        assertDoesNotThrow(() -> new Deck(fourCards, threeCards),
                "Constructor should not throw an exception when the arguments are valid");
    }

    @Test
    void deckConstructorNoArgsTest() {
        assertDoesNotThrow(() -> new Deck(), "Constructor should not throw an exception when no arguments are passed");
    }
}
