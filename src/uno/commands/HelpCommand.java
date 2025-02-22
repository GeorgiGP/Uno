package uno.commands;

import org.jetbrains.annotations.NotNull;
import uno.commands.factory.CommandOptions;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

public class HelpCommand extends UnoCommand {
    public HelpCommand(@NotNull UnoGame game, @NotNull UnoPlayer player) {
        super(game, player);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public void execute() throws IOException {
        player.sendMessage(CommandOptions.help());
    }
}
