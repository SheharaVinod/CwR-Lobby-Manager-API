package lk.cwresports.LobbyManager.API;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LobbyManager {
    private static LobbyManager manager;
    Map<String, LobbyGroup> lobbyGroupMap = new HashMap<>();
    private Lobby defaultSelectedLobby;
    private final CwRLobbyAPI plugin;
    private static final Set<Player> spawnBlockedPlayers = new HashSet<>();

    public LobbyManager() {
        plugin = CwRLobbyAPI.getPlugin();

        // creating instance.
        getDefaultGroup();

    }

    public static void blockSpawnCommand(Player player) {
        spawnBlockedPlayers.add(player);
    }

    public static void unBlockSpawnCommand(Player player) {
        spawnBlockedPlayers.remove(player);
    }

    public static boolean isBlockedSpawnCommand(Player player) {
        return spawnBlockedPlayers.contains(player);
    }

    public void sendToLobby(Player player) {
        getSelectedLobbyOf(player).send(player);
    }

    public LobbyGroup getLobbyGroup(String name) {
        return this.lobbyGroupMap.get(name);
    }

    public void registerLobbyGroup(LobbyGroup lobbyGroup) {
        this.lobbyGroupMap.put(lobbyGroup.getName(), lobbyGroup);
    }

    private Lobby getSelectedLobbyOf(Player player) {
        LobbyGroup group = getLobbyGroupOf(player);
        return group.getCurrentLobby();
    }

    public boolean playerIsMoreThanDefault(Player player) {
        return player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT);
    }

    public static LobbyManager getInstance() {
        if (manager == null) {
            manager = new LobbyManager();
        }
        return manager;
    }


    private LobbyGroup getLobbyGroupOf(Player player) {
        if (playerIsMoreThanDefault(player)) {
            // TODO: read config and get what category he selected.

            return null;
        }
        return getDefaultGroup();
    }


    final String defaultKey = "default";
    public LobbyGroup getDefaultGroup() {
        if (!this.lobbyGroupMap.containsKey(defaultKey)) {
            LobbyGroup default_group = new LobbyGroup(defaultKey);
            this.lobbyGroupMap.put(default_group.getName(), default_group);
        }
        return this.lobbyGroupMap.get(defaultKey);
    }
}
