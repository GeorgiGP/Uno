package uno.deck;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.exceptions.EmptyDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck implements DeckApi {

    private static final int MIN_NUMBER_OF_CARDS = 4;

    @NotNull
    private List<@NotNull UnoCard> drawPile;
    @NotNull
    private final List<@NotNull UnoCard> playedPile;

    public Deck() {
        drawPile = new ArrayList<>();
        playedPile = new ArrayList<>();
        initializeDeck();
        playedPile.add(drawPile.removeLast());
    }

    public Deck(@NotNull List<@NotNull UnoCard> drawPile, @NotNull List<@NotNull UnoCard> playedPile) {
        if (drawPile.size() + playedPile.size() < MIN_NUMBER_OF_CARDS) {
            throw new IllegalArgumentException("drawPile and playedPile cannot contain less than 4 cards");
        }
        this.drawPile = new ArrayList<>(drawPile);
        this.playedPile = new ArrayList<>(playedPile);

        if (playedPile.isEmpty()) {
            this.playedPile.add(this.drawPile.removeLast());
        }
    }

    private void initializeDeck() {
        for (Color color : Color.values()) {
            if (color == Color.WILD) {
                continue;
            }

            for (UnoCardType value : UnoCardType.values()) {
                if (value == UnoCardType.ZERO || value == UnoCardType.WILD || value == UnoCardType.PLUS_FOUR) {
                    continue;
                }
                drawPile.add(new UnoCard(value, color));
                drawPile.add(new UnoCard(value, color));
            }

            drawPile.add(new UnoCard(UnoCardType.ZERO, color));
            drawPile.add(new UnoCard(UnoCardType.WILD, Color.WILD));
            drawPile.add(new UnoCard(UnoCardType.PLUS_FOUR, Color.WILD));
        }

        Collections.shuffle(drawPile);
    }

    @Override
    public void placeCard(@NotNull UnoCard card) {
        playedPile.add(card);
    }

    @Override
    public UnoCard peekLastPlacedCard() {
        return playedPile.getLast();
    }

    private void flipDeck() {
        UnoCard lastCard = playedPile.removeLast();

        List<UnoCard> reversedPlayedPile = new ArrayList<>(playedPile);
        Collections.reverse(reversedPlayedPile);
        drawPile = reversedPlayedPile;

        playedPile.clear();
        playedPile.add(lastCard);
    }

    @Override
    public UnoCard drawCard() {
        if (drawPile.isEmpty()) {
            if (playedPile.size() == 1) {
                throw new EmptyDeckException(
                    "The deck is empty and there is only one placed card. Drawing is impossible.");
            } else {
                flipDeck();
            }
        }

        return drawPile.removeLast();
    }

    @Override
    public List<UnoCard> drawCards(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount cards to draw cannot be negative!");
        }

        if (drawPile.size() + playedPile.size() - 1 < amount) {
            throw new EmptyDeckException("Not enough cards to draw!");
        }

        List<UnoCard> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            result.add(drawCard());
        }
        return result;
    }

    public void returnCardsToDrawPile(@NotNull List<@NotNull UnoCard> cards) {
        for (UnoCard card : cards) {
            int randomIndex = (int) (Math.random() * (drawPile.size())); // Insert at random position
            drawPile.add(randomIndex, card);
        }
    }
}

