package uno.commands.ingamecommands.place;

import org.jetbrains.annotations.NotNull;
import uno.commands.changingplayermode.SpectatingCommand;
import uno.commands.UnoCommand;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

public abstract class PlaceCardCommand extends UnoCommand {
    protected final int cardIndex;

    protected UnoCard chosenCard;

    public PlaceCardCommand(@NotNull UnoGame game, @NotNull UnoPlayer player, int cardIndex) {
        super(game, player);
        if (cardIndex < 0) {
            throw new IllegalArgumentException("Card index cannot be negative in place command");
        }
        this.cardIndex = cardIndex;
    }

    public boolean isCardValid() {
        if (player.getHand().size() <= cardIndex) {
            return false;
        }

        chosenCard = player.getHand().get(cardIndex);
        return true;
    }

    public void placeCard() throws IOException {
        UnoCard card = player.takeCard(cardIndex);
        game.getDeck().placeCard(card);
        game.addToPlayedCardsHistory(card);
        game.sendMessageToAllPlayers(player.getInGamePlayerName() + " placed card: " + card);

        if (player.getHand().isEmpty()) {
            game.addToFinished(player);
            player.setPlayingMode(false);
            if (game.getActivePlayers().size() == 1) {
                new SpectatingCommand(game, player).execute();
            } else {
                player.sendMessage("Type 'spectate' if you want to continue watching the game");
                if (player.receiveMessage().equalsIgnoreCase("spectate")) {
                    new SpectatingCommand(game, player).execute();
                }
            }
        } else {
            game.goToNextPlayer();
        }
    }
}
