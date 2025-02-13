package uno.server.handlers;

import org.jetbrains.annotations.NotNull;

public interface Handler {
    default String extractBetween(@NotNull String input) {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Empty or null input");
        }

        String regex = "<([^>]+)>";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new IllegalArgumentException("Illegal input command: " + input);
    }
}
