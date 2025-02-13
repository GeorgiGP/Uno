package uno.commands.ingamecommands.alwaysavailable;

import org.jetbrains.annotations.NotNull;
import uno.commands.UnoCommand;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

public class ShowLastCard extends UnoCommand {

    public ShowLastCard(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return !player.spectating();
    }

    @Override
    public void execute() {
        player.sendMessage("last played card: " + game.getLastCard() +
                "  Color is: " + game.getColor());
    }
}
