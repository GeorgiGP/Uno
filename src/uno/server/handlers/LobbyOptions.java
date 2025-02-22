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
        return "Options: " + System.lineSeparator() +
                "create-game --game-id=<game-id> --number-of-players=<number>" + System.lineSeparator() +
                "join --game-id=<game-id> --display-name=<display-name>" + System.lineSeparator() +
                "list-games --status=<started/ended/available/all>" + System.lineSeparator() +
                "summary --game-id=<game-id>" + System.lineSeparator() +
                "logout" + System.lineSeparator();
    }
}
