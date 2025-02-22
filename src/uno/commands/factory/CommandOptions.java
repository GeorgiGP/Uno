package uno.commands.factory;

import org.jetbrains.annotations.NotNull;

public enum CommandOptions {
    SHOW_HAND("show-hand"),
    SHOW_LAST_CARD("show-last-card"),
    SHOW_PLAYED_CARDS("show-played-cards"),
    DRAW("draw"),
    ACCEPT_EFFECT("accept-effect"),
    PLAY_NORMAL_CARD("play"),
    PLAY_WILD("play-choose"),
    PLAY_PLUS_FOUR("play-plus-four"),
    LEAVE("leave"),
    SPECTATE("spectate"),
    HELP("help");

    @NotNull
    private final String option;

    CommandOptions(@NotNull String option) {
        this.option = option;
    }

    public static CommandOptions fromString(@NotNull String option) {
        for (CommandOptions value : CommandOptions.values()) {
            if (value.option.equalsIgnoreCase(option)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return option;
    }

    public static String help() {
        return "List of all commands: " + System.lineSeparator() +
                "show-hand" + System.lineSeparator() +
                "show-last-card" + System.lineSeparator() +
                "show-played-cards" + System.lineSeparator() +
                "draw" + System.lineSeparator() +
                "accept-effect" + System.lineSeparator() +
                "play --card-id=<card-id>" + System.lineSeparator() +
                "play-choose --card-id=<card-id> --color=<red/green/blue/yellow>" + System.lineSeparator() +
                "play-plus-four --card-id=<card-id> --color=<color>" + System.lineSeparator() +
                "leave" + System.lineSeparator();
    }
}
