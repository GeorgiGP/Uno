package uno.server.handlers;

import org.jetbrains.annotations.NotNull;

public enum SignOptions {
    LOGIN("login"),
    REGISTER("register"),
    EXIT("exit");

    @NotNull
    private final String option;

    SignOptions(@NotNull String option) {
        this.option = option;
    }

    public static SignOptions fromString(@NotNull String option) {
        for (SignOptions value : SignOptions.values()) {
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
                "register --username=<username> --password=<password>" + System.lineSeparator() +
                "login --username=<username> --password=<password>" + System.lineSeparator() +
                "exit" + System.lineSeparator();
    }
}
