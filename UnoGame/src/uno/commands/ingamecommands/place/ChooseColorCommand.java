package uno.commands.ingamecommands.place;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

public class ChooseColorCommand extends PlaceCardCommand {
    @NotNull
    private final Color color;

    public ChooseColorCommand(@NotNull UnoGame game, @NotNull UnoPlayer player, int cardIndex, @NotNull Color color) {
        super(game, player, cardIndex);
        this.color = color;
    }

    @Override
    public boolean isExecutable() {
        return isOnTurn(player) && isCardValid() &&
                !game.isEffectAvailable() && chosenCard.value() == UnoCardType.WILD;
    }

    @Override
    public void execute() throws IOException {
        game.setColor(color);
        game.sendMessageToAllPlayers("New color is: " + color);
        placeCard();
    }
}
