package uno.commands.changingplayermode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.commands.UnoCommand;
import uno.game.UnoGame;
import uno.game.UnoPlayer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpectatingCommandTest {

    private UnoGame game;
    private UnoPlayer player;
    private UnoCommand command;

    @BeforeEach
    public void setUp() {
        game = mock(UnoGame.class);
        player = mock(UnoPlayer.class);
    }

    @Test
    void isExecutableValidTest(){
        when(player.spectating()).thenReturn(false);
        when(player.playing()).thenReturn(false);
        command = new SpectatingCommand(game, player);
        assertTrue(command.isExecutable(), "Expected command to be executable when the player is neither spectating nor playing.");
    }

    @Test
    void isExecutableAlreadySpectatingTest(){
        when(player.spectating()).thenReturn(true);
        command = new SpectatingCommand(game, player);
        assertFalse(command.isExecutable(), "Expected command to not be executable when the player is already spectating.");
    }

    @Test
    void isExecutableStillPlayingTest(){
        when(player.spectating()).thenReturn(false);
        when(player.playing()).thenReturn(true);
        command = new SpectatingCommand(game, player);
        assertFalse(command.isExecutable(), "Expected command to be NOT executable when the player is still playing.");
    }

    @Test
    void executeTest() throws IOException {
        command = new SpectatingCommand(game, player);
        doNothing().when(player).setSpectatingMode(true);
        doNothing().when(game).addToSpectators(player);

        command.execute();

        verify(player).setSpectatingMode(true);
        verify(game).addToSpectators(player);
    }
}
