package uno.game;

import org.jetbrains.annotations.NotNull;
import uno.deck.Deck;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.exceptions.NameTakenException;
import uno.commands.UnoCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class UnoGame {
    private static final int STARTING_CARDS_COUNT = 7;
    @NotNull
    private final String creator;
    private final int maxPlayers;
    @NotNull
    private GameStatus status;

    @NotNull
    private final Map<@NotNull String, @NotNull UnoPlayer> disconnectedPlayers;
    @NotNull
    private final List<@NotNull UnoPlayer> players;
    @NotNull
    private final Set<@NotNull UnoPlayer> spectators;
    @NotNull
    private final List<@NotNull UnoPlayer> finished;
    @NotNull
    private final Set<@NotNull String> names;

    @NotNull
    private final Deck deck;
    private boolean clockWise;
    private int currentPlayerIndex;
    private boolean isEffectAvailable;
    @NotNull
    private final List<@NotNull UnoCard> playedHistory;
    private Color color;

    public UnoGame(int maxPlayers, @NotNull String creator) {
        deck = new Deck();
        this.maxPlayers = maxPlayers;
        clockWise = true;
        status = GameStatus.AVAILABLE;
        playedHistory = new ArrayList<>();
        this.creator = creator;
        disconnectedPlayers = new ConcurrentHashMap<>();
        players = new CopyOnWriteArrayList<>();
        spectators = new CopyOnWriteArraySet<>();
        finished = new CopyOnWriteArrayList<>();
        names = new ConcurrentSkipListSet<>();
    }

    public boolean hasDisconnected(@NotNull String registrationName) {
        return disconnectedPlayers.containsKey(registrationName);
    }

    public void putDisconnected(@NotNull String registrationName, @NotNull UnoPlayer disconnectedPlayer) {
        disconnectedPlayers.put(registrationName, disconnectedPlayer);
        if (disconnectedPlayer.playing()) {
            leaveGame(disconnectedPlayer);
        } else if (disconnectedPlayer.spectating()) {
            spectators.remove(disconnectedPlayer);
        }
    }

    public UnoPlayer reconnect(@NotNull String registrationName) {
        UnoPlayer disconnectedPlayer = disconnectedPlayers.remove(registrationName);
        if (disconnectedPlayer.playing()) {
            players.add(disconnectedPlayer);
        } else {
            spectators.add(disconnectedPlayer);
        }
        return disconnectedPlayer;
    }

    public void removeSpectator(@NotNull UnoPlayer spectator) {
        spectators.remove(spectator);
    }

    public void addToSpectators(@NotNull UnoPlayer player) {
        spectators.add(player);
    }

    public UnoPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    @NotNull
    public String getCreator() {
        return creator;
    }

    @NotNull
    public GameStatus getStatus() {
        return status;
    }

    public synchronized boolean addPlayer(@NotNull UnoPlayer player) {
        if (status != GameStatus.AVAILABLE || players.size() >= maxPlayers) {
            return false;
        }
        if (names.contains(player.getInGamePlayerName())) {
            throw new NameTakenException("Name: " + player.getInGamePlayerName() + " already exists in the game");
        }
        names.add(player.getInGamePlayerName());
        return players.add(player);
    }

    public synchronized UnoPlayer removePlayer(@NotNull UnoPlayer player) {
        int index = players.indexOf(player);
        if (index < currentPlayerIndex) {
            currentPlayerIndex--;
        } else if (currentPlayerIndex == index) {
            if (clockWise) {
                if (currentPlayerIndex == players.size() - 1) {
                    currentPlayerIndex = 0;
                }
            } else {
                if (currentPlayerIndex != 0) {
                    currentPlayerIndex--;
                } else {
                    currentPlayerIndex = players.size() - 2;
                }
            }
        }
        names.remove(player.getInGamePlayerName());
        return players.remove(index);
    }

    public void sendMessageToAllPlayers(@NotNull String message) {
        for (UnoPlayer player : players) {
            player.sendMessage(message);
        }
        for (UnoPlayer player : spectators) {
            player.sendMessage(Color.CYAN_CODE + message + Color.RESET_CODE);
        }
    }

    public void addToFinished(@NotNull UnoPlayer player) {
        finished.add(removePlayer(player));
        sendMessageToAllPlayers("Player " + player.getInGamePlayerName() + " finished the game.");
        sendMessageToAllPlayers("it's " + players.get(currentPlayerIndex).getInGamePlayerName() + "'s turn");
    }

    public void leaveGame(@NotNull UnoPlayer player) {
        removePlayer(player);
        sendMessageToAllPlayers("it's " + players.get(currentPlayerIndex).getInGamePlayerName() + "'s turn");
    }

    private void dealCards() {
        for (UnoPlayer player : players) {
            player.giveCards(deck.drawCards(STARTING_CARDS_COUNT));
        }
    }

    private void prepGame() {
        status = GameStatus.STARTED;
        dealCards();
        currentPlayerIndex = 0;
        isEffectAvailable = false;
        color = deck.peekLastPlacedCard().color();
    }

    public void startGame() {
        if (players.size() < 2) {
            sendMessageToAllPlayers("game needs at least 2 players");
            return;
        }
        prepGame();
        sendMessageToAllPlayers("Game started");
        sendMessageToAllPlayers("it's " + players.get(currentPlayerIndex).getInGamePlayerName() + "'s turn");

        for (UnoPlayer player : players) {
            if (!player.getRegistrationPlayerName().equals(creator)) {
                player.sendMessage("Press any key to start executing commands");
            }
        }
    }

    public void receiveCommand(@NotNull UnoCommand command, @NotNull UnoPlayer player) throws IOException {
        if (command.isExecutable()) {
            command.execute();
        } else {
            player.sendMessage("cannot execute this command");
        }
        if (players.size() <= 1) {
            status = GameStatus.ENDED;
            disconnectedPlayers.clear();
            printEndGameScreen();
        }
    }

    private void printEndGameScreen() {
        sendMessageToAllPlayers("Game has finished!");
        if (finished.isEmpty()) {
            sendMessageToAllPlayers("There are no winners!");

        } else {
            sendMessageToAllPlayers("The winners are : ");
            for (int i = 0; i < finished.size(); i++) {
                sendMessageToAllPlayers(Color.GREEN_CODE + (i + 1) + ". place : " +
                    finished.get(i).getInGamePlayerName() + Color.RESET_CODE);
            }
            sendMessageToAllPlayers(Color.RED_CODE + "Big loser is :" +
                players.get(currentPlayerIndex).getInGamePlayerName() + Color.RESET_CODE);
        }
        sendMessageToAllPlayers("Press any key to return to game lobby...");
    }

    public List<UnoPlayer> getActivePlayers() {
        return players;
    }

    public UnoCard getLastCard() {
        return deck.peekLastPlacedCard();
    }

    public boolean isEffectAvailable() {
        return isEffectAvailable;
    }

    public void setEffect(boolean isEffectAvailable) {
        this.isEffectAvailable = isEffectAvailable;
    }

    @NotNull
    public Deck getDeck() {
        return deck;
    }

    public void changeDirection() {
        clockWise = !clockWise;
    }

    public void goToNextPlayer() {
        currentPlayerIndex += clockWise ? 1 : -1;
        currentPlayerIndex += players.size();
        currentPlayerIndex %= players.size();
        sendMessageToAllPlayers("it's " + players.get(currentPlayerIndex).getInGamePlayerName() + "'s turn");
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addToPlayedCardsHistory(UnoCard card) {
        playedHistory.add(card);
    }

    @NotNull
    public List<@NotNull UnoCard> getPlayedHistory() {
        return playedHistory;
    }

    public void returnCards(@NotNull List<@NotNull UnoCard> cards) {
        deck.returnCardsToDrawPile(cards);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Game status: ").append(status).append(" |");
        result.append("Players: ").append(players.size() + finished.size()).append("/").append(maxPlayers).append(" |");
        for (UnoPlayer player : players) {
            result.append(player.getInGamePlayerName()).append(", ");
        }

        result.append("Finished: ");
        for (UnoPlayer finished : finished) {
            result.append(finished.getInGamePlayerName()).append(", ");
        }

        result.append(" |");
        if (status == GameStatus.ENDED) {
            if (!finished.isEmpty()) {
                result.append("Winner is: ").append(finished.getFirst().getInGamePlayerName());
            } else {
                result.append("Game was aborted, no winners!");
            }
        }
        return result.toString();
    }
}
