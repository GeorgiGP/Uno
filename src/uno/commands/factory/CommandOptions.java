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
    SPECTATE("spectate");

    @NotNull
    private final String option;

    CommandOptions(@NotNull String option) {
        this.option = option;
    }

    public static CommandOptions fromString(@NotNull String option) {
        for (CommandOptions value : CommandOptions.values()) {
            if (value.option.equals(option)) {
                return value;
            }
        }
        return null;
    }
}
