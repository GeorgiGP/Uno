package uno.commands.ingamecommands.place;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

public class PlayNormalCardCommand extends PlaceCardCommand {

    public PlayNormalCardCommand(@NotNull UnoGame game, @NotNull UnoPlayer player, int cardIndex) {
        super(game, player, cardIndex);
    }

    @Override
    public boolean isExecutable() {
        return isOnTurn(player) && !game.isEffectAvailable() && isCardValid() &&
            chosenCard.color() != Color.WILD && canPlayCard(chosenCard);
    }

    @Override
    public void execute() throws IOException {
        game.setColor(chosenCard.color());
        if (chosenCard.value() == UnoCardType.REVERSE) {
            if (game.getActivePlayers().size() == 2) { //special behavior with two players
                placeCard();
                game.goToNextPlayer();
            } else {
                game.changeDirection();
                placeCard();
            }
        } else if (chosenCard.value() == UnoCardType.SKIP) {
            placeCard();
            game.goToNextPlayer();
        } else if (chosenCard.value() == UnoCardType.PLUS_TWO) {
            game.setEffect(true);
            placeCard();
        } else {
            placeCard();
        }
    }
}
