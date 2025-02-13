package uno.deck.card;

import org.jetbrains.annotations.NotNull;

import static uno.deck.card.Color.RESET_CODE;

public record UnoCard(@NotNull UnoCardType value, @NotNull Color color) {
    @Override
    public String toString() {
        return (">> Type: "  + value + " + " +
                "Color: " + color + RESET_CODE);
    }
}

