package uno.commands.ingamecommands.place;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.deck.Deck;
import uno.deck.card.UnoCardType;
import uno.deck.card.Color;
import uno.deck.card.UnoCard;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlaceCardCommandTest {
    private UnoGame game;
    private UnoPlayer player;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    void constructorNegativeCardIndexTest() {
        assertThrows(IllegalArgumentException.class, () -> new PlayNormalCardCommand(game, player, -1),
                "Card index cannot be negative");
    }

    @Test
    void constructorOkTest() {
        assertDoesNotThrow(() -> new PlayNormalCardCommand(game, player, 0),
                "Card index should be valid (>= 0)");
    }

    @Test
    void isCardValidBigIndexTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ZERO, Color.RED), new UnoCard(UnoCardType.ONE, Color.BLUE)));
        PlayNormalCardCommand command = new PlayNormalCardCommand(game, player, 4);
        assertFalse(command.isCardValid(), "Card index 4 should be invalid since the player only has 2 cards");
    }

    @Test
    void isCardValidNormalTest() {
        when(player.getHand()).thenReturn(
                List.of(new UnoCard(UnoCardType.ZERO, Color.RED), new UnoCard(UnoCardType.ONE, Color.BLUE)));
        PlayNormalCardCommand command = new PlayNormalCardCommand(game, player, 1);
        assertTrue(command.isCardValid(), "Card index 1 should be valid");
    }

    @Test
    void placeCardNormalTest() throws IOException {
        when(player.getHand()).thenReturn(List.of(new UnoCard(UnoCardType.ZERO, Color.RED)));
        when(player.takeCard(any(Integer.class))).thenReturn(new UnoCard(UnoCardType.ZERO, Color.RED));
        PlayNormalCardCommand command = new PlayNormalCardCommand(game, player, 0);

        Deck deck = new Deck();

        when(game.getDeck()).thenReturn(deck);
        doNothing().when(game).goToNextPlayer();
        doNothing().when(game).addToPlayedCardsHistory(any());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).addToFinished(any());

        when(game.getActivePlayers()).thenReturn(List.of(player, mock(UnoPlayer.class)));

        doNothing().when(player).setPlayingMode(any(Boolean.class));
        when(player.getInGamePlayerName()).thenReturn("ddz");
        doNothing().when(player).setPlayingMode(any(Boolean.class));

        command.placeCard();
        verify(game, times(1)).addToPlayedCardsHistory(any());
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(1)).goToNextPlayer();
        assertEquals(new UnoCard(UnoCardType.ZERO, Color.RED), deck.peekLastPlacedCard(),
                "The last placed card should be the one the player played");
    }

    @Test
    void placeCardLastCardNotEndingGameTest() throws IOException {
        when(player.getHand()).thenReturn(Collections.emptyList());
        when(player.takeCard(any(Integer.class))).thenReturn(new UnoCard(UnoCardType.ZERO, Color.RED));
        PlayNormalCardCommand command = new PlayNormalCardCommand(game, player, 0);

        Deck deck = new Deck();

        when(game.getDeck()).thenReturn(deck);
        doNothing().when(game).goToNextPlayer();
        doNothing().when(game).addToPlayedCardsHistory(any());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).addToFinished(any());

        when(game.getActivePlayers()).thenReturn(List.of(player, mock(UnoPlayer.class)));

        doNothing().when(player).setPlayingMode(any(Boolean.class));
        when(player.getInGamePlayerName()).thenReturn("ddz");
        when(player.receiveMessage()).thenReturn("ddz");
        doNothing().when(player).sendMessage(any());
        doNothing().when(player).setPlayingMode(any(Boolean.class));

        command.placeCard();
        verify(game, times(1)).addToPlayedCardsHistory(any());
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(0)).goToNextPlayer();
        verify(game, times(1)).addToFinished(any());
        verify(player, times(1)).setPlayingMode(any(Boolean.class));
        assertEquals(new UnoCard(UnoCardType.ZERO, Color.RED), deck.peekLastPlacedCard(),
                "The last placed card should be the one the player played");
    }

    @Test
    void placeCardLastCardEndingGameTest() throws IOException {
        when(player.getHand()).thenReturn(Collections.emptyList());
        when(player.takeCard(any(Integer.class))).thenReturn(new UnoCard(UnoCardType.ZERO, Color.RED));
        PlayNormalCardCommand command = new PlayNormalCardCommand(game, player, 0);

        Deck deck = new Deck();

        when(game.getDeck()).thenReturn(deck);
        doNothing().when(game).goToNextPlayer();
        doNothing().when(game).addToPlayedCardsHistory(any());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).addToFinished(any());

        when(game.getActivePlayers()).thenReturn(List.of(player));
        doNothing().when(player).setPlayingMode(any(Boolean.class));
        doNothing().when(game).addToSpectators(any());

        doNothing().when(player).setPlayingMode(any(Boolean.class));
        when(player.getInGamePlayerName()).thenReturn("ddz");
        when(player.receiveMessage()).thenReturn("ddz");
        doNothing().when(player).sendMessage(any());
        doNothing().when(player).setPlayingMode(any(Boolean.class));

        command.placeCard();
        verify(game, times(1)).addToPlayedCardsHistory(any());
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(0)).goToNextPlayer();
        verify(game, times(1)).addToFinished(any());
        verify(game, times(1)).addToSpectators(any());
        verify(game, times(1)).addToFinished(any());
        verify(player, times(1)).setSpectatingMode(any(Boolean.class));
        assertEquals(new UnoCard(UnoCardType.ZERO, Color.RED), deck.peekLastPlacedCard(),
                "The last placed card should be the one the player played");
    }

    @Test
    void placeCardLastCardSpectatingTest() throws IOException {
        when(player.getHand()).thenReturn(Collections.emptyList());
        when(player.takeCard(any(Integer.class))).thenReturn(new UnoCard(UnoCardType.ZERO, Color.RED));
        PlayNormalCardCommand command = new PlayNormalCardCommand(game, player, 0);

        Deck deck = new Deck();

        when(game.getDeck()).thenReturn(deck);
        doNothing().when(game).goToNextPlayer();
        doNothing().when(game).addToPlayedCardsHistory(any());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).addToFinished(any());

        when(game.getActivePlayers()).thenReturn(List.of(player, mock(UnoPlayer.class)));
        doNothing().when(player).setPlayingMode(any(Boolean.class));
        doNothing().when(game).addToSpectators(any());

        doNothing().when(player).setPlayingMode(any(Boolean.class));
        when(player.getInGamePlayerName()).thenReturn("ddz");
        when(player.receiveMessage()).thenReturn("spectate");
        doNothing().when(player).sendMessage(any());
        doNothing().when(player).setPlayingMode(any(Boolean.class));

        command.placeCard();
        verify(game, times(1)).addToPlayedCardsHistory(any());
        verify(game, times(1)).sendMessageToAllPlayers(any());
        verify(game, times(0)).goToNextPlayer();
        verify(game, times(1)).addToFinished(any());
        verify(game, times(1)).addToSpectators(any());
        verify(game, times(1)).addToFinished(any());
        verify(player, times(1)).setSpectatingMode(any(Boolean.class));
        assertEquals(new UnoCard(UnoCardType.ZERO, Color.RED), deck.peekLastPlacedCard(),
                "The last placed card should be the one the player played");
    }
}
