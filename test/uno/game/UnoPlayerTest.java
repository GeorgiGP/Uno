package uno.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.deck.card.UnoCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnoPlayerTest {
    private static final PrintWriter out = mock(PrintWriter.class);
    private static final BufferedReader in = mock(BufferedReader.class);
    private static UnoPlayer player;

    @BeforeEach
    void setUpBeforeEach() {
        player = new UnoPlayer(out, in, "inGame", "registration");
    }

    @Test
    void testUnmodifiableHandShouldNotBeAbleToModifyTheHand() {
        assertThrows(UnsupportedOperationException.class, () -> player.getHand().clear(),
                "When getting the player's hand, it should be unmodifiable.");
    }

    @Test
    void testReadingCorrectMessage() throws IOException {
        when(in.readLine()).thenReturn("The next line");
        String receivedMessage = player.receiveMessage();
        assertEquals("The next line", receivedMessage, "The message received from the input stream should be read correctly.");
    }

    @Test
    void testWritingCorrectMessage() {
        StringWriter writer = new StringWriter();
        UnoPlayer player = new UnoPlayer(new PrintWriter(writer), in, "inGame", "registration");
        String message = "Some message";
        player.sendMessage(message);
        assertEquals(message + System.lineSeparator(), writer.toString(),
                "The sent message should match the expected message with correct line separator.");
    }

    @Test
    void testGive2CardsPreviousHandWasEmpty() {
        UnoCard card1 = mock(UnoCard.class);
        UnoCard card2 = mock(UnoCard.class);
        List<UnoCard> listCards = List.of(card1, card2);
        player.giveCards(listCards);
        assertIterableEquals(listCards, player.getHand(),
                "After giving the player 2 cards, their hand should contain exactly the given cards in the correct order.");
    }

    @Test
    void testGive2CardsOneByOnePreviousHandWasEmpty() {
        UnoCard card1 = mock(UnoCard.class);
        UnoCard card2 = mock(UnoCard.class);
        List<UnoCard> listCards = List.of(card1, card2);
        player.giveCard(card1);
        assertEquals(1, player.getHand().size(),
                "After giving one card, the player should have 1 card in hand.");
        assertEquals(card1, player.getHand().getFirst(), "The first card in the player's hand should be the card just given.");

        player.giveCard(card2);
        assertIterableEquals(listCards, player.getHand(),
                "After giving the second card, the player's hand should contain both cards in the order they were given.");
    }

    @Test
    void testTakingCardWhenEmptyHand() {
        assertThrows(IllegalArgumentException.class, () -> player.takeCard(0),
                "The player should not be able to take a card from an empty hand.");
    }

    @Test
    void testTakingCard() {
        UnoCard card1 = mock(UnoCard.class);
        player.giveCard(card1);
        assertEquals(card1, player.getHand().getFirst(),
                "The first card in the player's hand should be the card just given.");

        UnoCard takenCard = player.takeCard(0);
        assertEquals(card1, takenCard, "The card taken from the hand should be the same as the one given.");
        assertTrue(player.getHand().isEmpty(), "The player's hand should be empty after taking the only card.");
    }
}
