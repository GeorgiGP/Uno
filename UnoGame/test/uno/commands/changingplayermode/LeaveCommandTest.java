package uno.commands.changingplayermode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.commands.UnoCommand;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LeaveCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    void isExecutableAlreadySpectatingTest(){
        when(player.spectating()).thenReturn(true);
        command = new LeaveCommand(game, player);
        assertTrue(command.isExecutable(), "Expected command to be executable when the player is already spectating.");
    }

    @Test
    void executeSpectatingTest() throws IOException {
        when(player.playing()).thenReturn(false);
        when(player.spectating()).thenReturn(true);
        doNothing().when(game).removeSpectator(player);
        doNothing().when(player).setSpectatingMode(false);

        command = new LeaveCommand(game, player);
        command.execute();

        verify(game).removeSpectator(player);
        verify(player).setSpectatingMode(false);
    }

    @Test
    void executePlayingTest() throws IOException {
        when(player.playing()).thenReturn(true);
        when(player.getHand()).thenReturn(Collections.emptyList());

        doNothing().when(game).returnCards(Collections.emptyList());
        doNothing().when(game).sendMessageToAllPlayers(any());
        doNothing().when(game).leaveGame(any());
        doNothing().when(player).setPlayingMode(false);

        command = new LeaveCommand(game, player);
        command.execute();
        verify(game).returnCards(Collections.emptyList());
        verify(game).sendMessageToAllPlayers(any());
        verify(game).leaveGame(any());
        verify(player).setPlayingMode(false);
        verify(player).getHand();
    }

    @Test
    void isExecutableStillPlayingTest(){
        when(player.spectating()).thenReturn(false);
        when(player.playing()).thenReturn(true);
        command = new LeaveCommand(game, player);
        assertTrue(command.isExecutable(), "Expected command to be executable when the player is still playing.");
    }

    @Test
    void isExecutableValidTest(){
        when(player.spectating()).thenReturn(false);
        when(player.playing()).thenReturn(false);
        command = new LeaveCommand(game, player);
        assertFalse(command.isExecutable(),
                "Expected command to not be executable when the player is neither playing nor spectating.");
    }
}
