package uno.game;

import org.jetbrains.annotations.NotNull;
import uno.deck.card.UnoCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnoPlayer {
    @NotNull
    private final List<@NotNull UnoCard> hand;
    @NotNull
    private final String inGameName;
    @NotNull
    private final String registrationName;
    @NotNull
    private PrintWriter out;
    @NotNull
    private BufferedReader in;

    private boolean isPlaying = false;
    private boolean isSpectating = false;

    public UnoPlayer(@NotNull PrintWriter out,
                     @NotNull BufferedReader in,
                     @NotNull String inGameName,
                     @NotNull String registrationName) {
        this.inGameName = inGameName;
        this.registrationName = registrationName;
        this.out = out;
        this.in = in;
        this.hand = new ArrayList<>();
    }

    public void setIn(@NotNull BufferedReader in) {
        this.in = in;
    }

    public BufferedReader in() {
        return in;
    }

    public void setOut(@NotNull PrintWriter out) {
        this.out = out;
    }

    public PrintWriter out() {
        return out;
    }

    public String getInGamePlayerName() {
        return inGameName;
    }

    public String getRegistrationPlayerName() {
        return registrationName;
    }

    public List<UnoCard> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public boolean spectating() {
        return isSpectating;
    }

    public void setSpectatingMode(boolean isSpectating) {
        this.isSpectating = isSpectating;
    }

    public boolean playing() {
        return isPlaying;
    }

    public void setPlayingMode(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void sendMessage(@NotNull String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void giveCards(@NotNull List<@NotNull UnoCard> cards) {
        hand.addAll(cards);
    }

    public void giveCard(@NotNull UnoCard card) {
        hand.add(card);
    }

    public UnoCard takeCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.remove(index);
        }
        throw new IllegalArgumentException("No card with such index");
    }
}
