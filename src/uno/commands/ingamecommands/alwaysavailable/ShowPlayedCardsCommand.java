package uno.commands.ingamecommands.alwaysavailable;

import org.jetbrains.annotations.NotNull;
import uno.commands.UnoCommand;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

public class ShowPlayedCardsCommand extends UnoCommand {

    public ShowPlayedCardsCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return !player.spectating();
    }

    @Override
    public void execute() {
        for (int i = 0; i < game.getPlayedHistory().size(); i++) {
            player.sendMessage("played :" + i + " " + game.getPlayedHistory().get(i));
        }
    }
}
