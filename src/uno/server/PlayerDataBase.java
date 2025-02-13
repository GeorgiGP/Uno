package uno.server;

import org.jetbrains.annotations.NotNull;
import uno.exceptions.signexceptions.LoginException;
import uno.exceptions.signexceptions.LogoutException;
import uno.exceptions.signexceptions.RegisterException;

public interface PlayerDataBase {
    void register(@NotNull String username, @NotNull String password) throws RegisterException;

    void login(@NotNull String username, @NotNull String password) throws LoginException, RegisterException;

    void logout(@NotNull String username) throws LogoutException;

    void save();
}
