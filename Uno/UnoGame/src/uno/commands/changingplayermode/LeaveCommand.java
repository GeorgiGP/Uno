package uno.commands.changingplayermode;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;
import uno.commands.UnoCommand;

import java.util.List;

public class LeaveCommand extends UnoCommand {

    public LeaveCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return player.spectating() || player.playing();
    }

    @Override
    public void execute() {
        if (player.playing()) {
            List<UnoCard> cards = player.getHand();
            game.returnCards(cards);
            player.setPlayingMode(false);
            game.sendMessageToAllPlayers("player " + player.getInGamePlayerName() + " has left the game");
            game.leaveGame(player);
        } else if (player.spectating()) {
            game.removeSpectator(player);
            player.setSpectatingMode(false);
        }
    }
}
