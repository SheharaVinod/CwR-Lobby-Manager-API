package lk.cwresports.LobbyManager.API;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.entity.Player;

import java.util.*;

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

    public Lobby getLobbyByName(String name) {
        // can be null.
        for (LobbyGroup lobbyGroup : lobbyGroupMap.values()) {
            for (Lobby lobby : lobbyGroup.getLobbies()) {
                if (lobby.getWorld().getName().equalsIgnoreCase(name)) {
                    return lobby;
                }
            }
        }
        return null;
    }

    public List<String> getLobbies() {
        List<String> nameList = new ArrayList<>();
        for (LobbyGroup lobbyGroup : lobbyGroupMap.values()) {
            for (Lobby lobby : lobbyGroup.getLobbies()) {
                nameList.add(lobby.getWorld().getName());
            }
        }
        return nameList;
    }

    public List<String> getGroups() {
        List<String> nameList = new ArrayList<>();
        for (LobbyGroup lobbyGroup : lobbyGroupMap.values()) {
            nameList.add(lobbyGroup.getName());
        }
        return nameList;
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

    public void change_group_of(String lobby, String group, Player admin) {
        Lobby lobby_ = getLobbyByName(lobby);
        LobbyGroup lobbyGroup_ = lobbyGroupMap.get(group);

        if (lobby_ == null || lobbyGroup_ == null) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return;
        }

        getDefaultGroup().removeLobby(lobby_);
        lobbyGroup_.addLobby(lobby_);
        admin.sendMessage(TextStrings.colorize(TextStrings.CHANGE_GROUP_SUCCESSFULLY));
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

            return getDefaultGroup();
        }
        return getDefaultGroup();
    }

    public void save() {
        // TODO: save lobbies and groups.

    }

    public void load() {
        // TODO: load existing data from config and other data files.

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
