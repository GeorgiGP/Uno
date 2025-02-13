package uno.commands.ingamecommands.place;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

public class PlusFourCommand extends PlaceCardCommand {
    @NotNull
    private final Color color;

    public PlusFourCommand(@NotNull UnoGame game, @NotNull UnoPlayer player, int cardIndex, @NotNull Color color) {
        super(game, player, cardIndex);
        if (color == Color.WILD) {
            throw new IllegalArgumentException();
        }
        this.color = color;
    }

    @Override
    public boolean isExecutable() {
        return isOnTurn(player) && isCardValid() &&
            !game.isEffectAvailable() && chosenCard.value() == UnoCardType.PLUS_FOUR;
    }

    @Override
    public void execute() throws IOException {
        game.setColor(color);
        game.sendMessageToAllPlayers("New color is: " + color);
        game.setEffect(true);
        placeCard();
    }
}
