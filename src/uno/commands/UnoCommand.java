package uno.commands;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

public abstract class UnoCommand {
    @NotNull
    protected UnoGame game;
    @NotNull
    protected UnoPlayer player;

    protected UnoCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        this.game = game;
        this.player = player;
    }

    public boolean canPlayCard(UnoCard card) {
        UnoCard lastPlayed = game.getLastCard();
        return card.color() == game.getColor() || card.color() == Color.WILD ||
                card.value() == lastPlayed.value() || game.getColor() == Color.WILD;
    }

    public boolean isOnTurn(UnoPlayer player) {
        return game.getCurrentPlayer().equals(player);
    }

    public abstract boolean isExecutable();

    public abstract void execute() throws IOException;
}
