package uno.deck.card;

public enum Color {
    GREEN,
    BLUE,
    YELLOW,
    RED,
    WILD;

    public static final String RESET_CODE = "\u001B[0m";

    public static final String GREEN_CODE = "\u001B[32m";
    public static final String BLUE_CODE = "\u001B[34m";
    public static final String YELLOW_CODE = "\u001B[33m";
    public static final String RED_CODE = "\u001B[31m";

    public static final String BRIGHT_PURPLE_CODE = "\u001B[95m";
    public static final String CYAN_CODE = "\u001B[36m";

    @Override
    public String toString() {
        return switch (this) {
            case GREEN -> GREEN_CODE + "GREEN" + RESET_CODE;
            case BLUE -> BLUE_CODE + "BLUE" + RESET_CODE;
            case YELLOW -> YELLOW_CODE + "YELLOW" + RESET_CODE;
            case RED -> RED_CODE + "RED" + RESET_CODE;
            case WILD -> BRIGHT_PURPLE_CODE + "WILD" + RESET_CODE;
        };

    }
}