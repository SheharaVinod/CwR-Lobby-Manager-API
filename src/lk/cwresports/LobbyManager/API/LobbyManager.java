package lk.cwresports.LobbyManager.API;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyManager {
    private static LobbyManager manager;
    Map<LobbyGroup, List<Lobby>> lobbyGroupAndLobbyMap = new HashMap<>();
    private Lobby defaultSelectedLobby;
    private Lobby specialSelectedLobby;

    public void sendToLobby(Player player) {
        getSelectedLobbyOf(player).send(player);
    }

    private Lobby getSelectedLobbyOf(Player player) {
        LobbyGroup group = getLobbyGroupOf(player);

        return
    }

    public static LobbyManager getInstance() {
        if (manager == null) {
            manager = new LobbyManager();
        }
        return manager;
    }



    private LobbyGroup getLobbyGroupOf(Player player) {
        if (player.hasPermission("cwr-core.lobby-manager.special")) {
            return LobbyGroup.SPECIAL;
        } else {
            return LobbyGroup.DEFAULT;
        }
    }
}
