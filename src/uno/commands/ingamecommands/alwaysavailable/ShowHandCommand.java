package uno.commands.ingamecommands.alwaysavailable;

import org.jetbrains.annotations.NotNull;
import uno.commands.UnoCommand;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

public class ShowHandCommand extends UnoCommand {

    public ShowHandCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return !player.spectating();
    }

    @Override
    public void execute() {
        for (int i = 0; i < player.getHand().size(); i++) {
            player.sendMessage("index : " + i + " " + player.getHand().get(i));
        }
    }
}
