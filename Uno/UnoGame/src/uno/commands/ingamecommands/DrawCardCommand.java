package uno.commands.ingamecommands;

import org.jetbrains.annotations.NotNull;
import uno.commands.UnoCommand;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;
import java.util.List;

public class DrawCardCommand extends UnoCommand {

    public DrawCardCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        if (!isOnTurn(player)) {
            return false;
        }
        if (game.isEffectAvailable()) {
            return false;
        }
        List<UnoCard> cards = game.getCurrentPlayer().getHand();

        for (UnoCard card : cards) {
            if (canPlayCard(card)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void execute() throws IOException {
        UnoCard card = game.getDeck().drawCard();
        game.getCurrentPlayer().giveCard(card);
        game.sendMessageToAllPlayers(
            "player " + game.getCurrentPlayer().getInGamePlayerName() + " has drawn a card ");
        game.goToNextPlayer();

    }
}
