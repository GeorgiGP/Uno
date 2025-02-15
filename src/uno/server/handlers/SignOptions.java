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
            if (value.option.equals(option)) {
                return value;
            }
        }
        return null;
    }

}
