package uno.lobby;

import org.jetbrains.annotations.NotNull;
import uno.exceptions.FullGameException;
import uno.game.GameStatus;
import uno.game.UnoGame;
import uno.game.UnoPlayer;
import uno.commands.UnoCommand;

import java.io.IOException;

import static uno.commands.factory.CommandsFactory.getCommand;

public class PreGame {
    @NotNull
    private UnoPlayer player;
    @NotNull
    private final UnoGame game;

    public PreGame(@NotNull UnoPlayer player, @NotNull UnoGame game) {
        this.player = player;
        this.game = game;
    }

    public void introduceToGame() throws IOException {
        if (game.hasDisconnected(player.getRegistrationPlayerName())) {
            player.sendMessage("You reconnected to the game.");
            UnoPlayer toSwitchWith = game.reconnect(player.getRegistrationPlayerName());
            toSwitchWith.setIn(player.in());
            toSwitchWith.setOut(player.out());
            player = toSwitchWith;
        } else {
            try {
                if (!waitToStart()) {
                    return;
                }
            } catch (IOException e) {
                game.removePlayer(player);
                throw e;
            }
            player.setPlayingMode(true);
        }
        goInGame();
        player.sendMessage("You are back in the games lobby!");
    }

    private boolean validateStart() {
        if (!game.getStatus().equals(GameStatus.AVAILABLE)) {
            player.sendMessage("Game is not available.");
            return false;
        }
        if (!game.addPlayer(player)) {
            throw new FullGameException("Lobby is full");
        }
        if (player.getRegistrationPlayerName().equals(game.getCreator())) {
            player.sendMessage("Type 'start' to start the game! You are the host.");
        } else {
            player.sendMessage("Waiting for host to start the game...");
        }
        player.sendMessage("Type 'leave' to leave the game.");
        return true;
    }

    private boolean waitToStart() throws IOException {
        if (!validateStart()) {
            return false;
        }
        while (game.getStatus().equals(GameStatus.AVAILABLE)) {
            String command = player.receiveMessage().toLowerCase().strip();
            if ("start".equals(command)) {
                if (player.getRegistrationPlayerName().equals(game.getCreator())) {
                    game.startGame();
                } else {
                    player.sendMessage("You cannot start the game. You are not the host.");
                }
            } else if ("leave".equals(command)) {
                game.removePlayer(player);
                player.sendMessage("You are back in the games lobby!");
                return false;
            } else {
                if (game.getStatus().equals(GameStatus.AVAILABLE)) {
                    player.sendMessage("Invalid command: " + command);
                }
            }
        }
        return true;
    }

    private void goInGame() throws IOException {
        while (game.getStatus().equals(GameStatus.STARTED)) {
            try {
                String line = player.receiveMessage();
                if (!game.getStatus().equals(GameStatus.STARTED)) {
                    break;
                }
                UnoCommand command = getCommand(line, game, player);
                game.receiveCommand(command, player);

                if (!(player.playing() || player.spectating())) {
                    return;
                }
            } catch (IOException e) {
                game.putDisconnected(player.getRegistrationPlayerName(), player);
                throw e;
            } catch (Exception e) {
                player.sendMessage(e.getMessage());
            }
        }
    }
}
