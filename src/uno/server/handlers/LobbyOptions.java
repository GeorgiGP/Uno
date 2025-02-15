package uno.server.handlers;

import org.jetbrains.annotations.NotNull;

public enum LobbyOptions {
    CREATE_GAME("create-game"),
    JOIN_GAME("join"),
    LIST_GAMES("list-games"),
    SUMMARY_GAME("summary"),
    LOGOUT("logout");

    @NotNull
    private final String option;

    LobbyOptions(@NotNull String option) {
        this.option = option;
    }

    public static LobbyOptions fromString(@NotNull String option) {
        for (LobbyOptions value : LobbyOptions.values()) {
            if (value.option.equals(option)) {
                return value;
            }
        }
        return null;
    }
}
