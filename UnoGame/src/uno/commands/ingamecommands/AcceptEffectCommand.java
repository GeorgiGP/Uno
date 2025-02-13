package uno.commands.ingamecommands;

import org.jetbrains.annotations.NotNull;
import uno.commands.UnoCommand;
import uno.deck.card.UnoCardType;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;
import java.util.List;

public class AcceptEffectCommand extends UnoCommand {
    private static final int CARDS_TO_DRAW_PLUS_4 = 4;
    private static final int CARDS_TO_DRAW_PLUS_2 = 2;
    public AcceptEffectCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return isOnTurn(player) && game.isEffectAvailable();
    }

    @Override
    public void execute() throws IOException {
        UnoCard lastCard = game.getLastCard();
        int cardsToDraw;
        if (lastCard.value() == UnoCardType.PLUS_FOUR) {
            cardsToDraw = CARDS_TO_DRAW_PLUS_4;
        } else {
            cardsToDraw = CARDS_TO_DRAW_PLUS_2;
        }
        List<UnoCard> cardsToDrawList = game.getDeck().drawCards(cardsToDraw);
        game.getCurrentPlayer().giveCards(cardsToDrawList);
        game.sendMessageToAllPlayers(game.getCurrentPlayer().getInGamePlayerName() + " drew " + cardsToDraw);
        game.setEffect(false);
        game.goToNextPlayer();

    }
}
