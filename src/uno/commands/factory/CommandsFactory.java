package uno.commands.factory;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.Color;
import uno.game.UnoGame;
import uno.game.UnoPlayer;
import uno.commands.ingamecommands.AcceptEffectCommand;
import uno.commands.ingamecommands.DrawCardCommand;
import uno.commands.changingplayermode.LeaveCommand;
import uno.commands.ingamecommands.alwaysavailable.ShowHandCommand;
import uno.commands.ingamecommands.alwaysavailable.ShowLastCard;
import uno.commands.ingamecommands.alwaysavailable.ShowPlayedCardsCommand;
import uno.commands.UnoCommand;
import uno.commands.changingplayermode.SpectatingCommand;
import uno.commands.ingamecommands.place.ChooseColorCommand;
import uno.commands.ingamecommands.place.PlayNormalCardCommand;
import uno.commands.ingamecommands.place.PlusFourCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsFactory {
    private static final int COUNT_ARGUMENTS_WILDCARD = 3;
    private static final int COUNT_ARGUMENTS_PLAY = 2;

    public static int extractCardId(@NotNull String input) {
        Pattern pattern = Pattern.compile("--card-id=<([0-9]+)>");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            try {
                int cardId = Integer.parseInt(matcher.group(1));
                return (cardId >= 0) ? cardId : -1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static Color extractColor(@NotNull String input) {
        Pattern pattern = Pattern.compile("--color=<([a-zA-Z]+)>");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String colorStr = matcher.group(1).toUpperCase();
            try {
                return Color.valueOf(colorStr);
            } catch (IllegalArgumentException e) {
                return Color.WILD;
            }
        }
        return Color.WILD;
    }

    public static UnoCommand getCommand(@NotNull String command, @NotNull UnoGame game, @NotNull UnoPlayer player) {
        String[] commands = command.split(" ");
        CommandOptions options = CommandOptions.fromString(commands[0]);
        return switch (options) {
            case SHOW_HAND -> new ShowHandCommand(game, player);
            case SHOW_LAST_CARD -> new ShowLastCard(game, player);
            case SHOW_PLAYED_CARDS -> new ShowPlayedCardsCommand(game, player);
            case DRAW -> new DrawCardCommand(game, player);
            case ACCEPT_EFFECT -> new AcceptEffectCommand(game, player);
            case PLAY_NORMAL_CARD -> {
                if (commands.length < COUNT_ARGUMENTS_PLAY) {
                    throw new IllegalArgumentException(
                            "Invalid number of arguments for play normal card, must be: " + COUNT_ARGUMENTS_PLAY);
                }
                yield new PlayNormalCardCommand(game, player, extractCardId(commands[1]));
            }
            case PLAY_WILD , PLAY_PLUS_FOUR -> {
                if (commands.length < COUNT_ARGUMENTS_WILDCARD) {
                    throw new IllegalArgumentException(
                            "Invalid number of arguments for wildcard, must be:" + COUNT_ARGUMENTS_WILDCARD);
                }
                int cardId = extractCardId(commands[1]);
                Color color = extractColor(commands[2]);
                if (color == Color.WILD) {
                    throw new IllegalArgumentException("Invalid color!");
                }
                yield options == CommandOptions.PLAY_WILD ? new ChooseColorCommand(game, player, cardId, color) :
                    new PlusFourCommand(game, player, cardId, color);
            }
            case LEAVE -> new LeaveCommand(game, player);
            case SPECTATE -> new SpectatingCommand(game, player);
            case null -> throw new IllegalStateException("Unexpected value: " + commands[0]);
        };
    }
}
