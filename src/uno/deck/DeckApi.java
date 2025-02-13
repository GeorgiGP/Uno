package uno.deck;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCard;

import java.util.List;

public interface DeckApi {

    void placeCard(@NotNull UnoCard card);

    UnoCard peekLastPlacedCard();

    UnoCard drawCard();

    List<UnoCard> drawCards(int amount);
}
