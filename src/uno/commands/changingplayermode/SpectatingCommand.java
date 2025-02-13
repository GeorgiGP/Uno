package uno.commands.changingplayermode;

import org.jetbrains.annotations.NotNull;
import uno.game.UnoGame;
import uno.game.UnoPlayer;
import uno.commands.UnoCommand;

public class SpectatingCommand extends UnoCommand {
    public SpectatingCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return !player.spectating() && !player.playing();
    }

    @Override
    public void execute() {
        player.setSpectatingMode(true);
        game.addToSpectators(player);
    }
}
