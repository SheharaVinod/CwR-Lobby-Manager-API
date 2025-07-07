package lk.cwresports.LobbyManager.API;

import lk.cwresports.LobbyManager.ConfigAndData.LobbyDataManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class LobbyManager {
    private static LobbyManager manager;
    public Map<String, LobbyGroup> lobbyGroupMap = new HashMap<>();
    public Map<String, Lobby> lobbyNameMap = new HashMap<>();

    public Lobby defaultSelectedLobby;
    private final CwRLobbyAPI plugin;
    private static final Set<Player> spawnBlockedPlayers = new HashSet<>();
    private static final List<EventLobbies> eventLobbies = new ArrayList<>();


    public LobbyManager() {
        plugin = CwRLobbyAPI.getPlugin();

        // creating instance.
        // getDefaultGroup();
    }

    public void registerNameFor(Lobby lobby) {
        String name = lobby.getWorld().getName();
        lobbyNameMap.put(name, lobby);
    }

    public Lobby getLobbyByName(String name) {
        // can be null.
        return lobbyNameMap.get(name);
    }

    public List<EventLobbies> getEventLobbies() {
        return eventLobbies;
    }

    public List<String> getGroupedLobbies() {
        List<String> nameList = new ArrayList<>();
        for (LobbyGroup lobbyGroup : lobbyGroupMap.values()) {
            for (Lobby lobby : lobbyGroup.getLobbies()) {
                nameList.add(lobby.getWorld().getName());
            }
        }
        return nameList;
    }

    public List<String> getAllLobbies() {
        return new ArrayList<>(lobbyNameMap.keySet());
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

    public void change_group_of(String lobby_name, String group, Player admin) {
        Lobby lobby = getLobbyByName(lobby_name);
        LobbyGroup previous_group = getCurrentGroupOf(lobby);
        LobbyGroup new_group = lobbyGroupMap.get(group);

        if (lobby == null || new_group == null || lobby instanceof EventLobbies || previous_group == null) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return;
        }
        previous_group.removeLobby(lobby);
        new_group.addLobby(lobby);

        admin.sendMessage(TextStrings.colorize("Lobby name: " + lobby_name));
        admin.sendMessage(TextStrings.colorize("Previous group: " + previous_group.getName()));
        admin.sendMessage(TextStrings.colorize("New group: " + new_group.getName()));
        admin.sendMessage(TextStrings.colorize(TextStrings.CHANGE_GROUP_SUCCESSFULLY));
    }

    public void sendToLobby(Player player) {
        player.sendMessage(TextStrings.colorize("You are warping to lobby."));

        if (EventManager.getInstance().isInEvent()) {
            EventLobbies eventLobby = EventManager.getInstance().getEventLobby();
            if (eventLobby == null) return;
            eventLobby.send(player);
            return;
        }

        Lobby selectedLobbyOf = getSelectedLobbyOf(player);
        if (selectedLobbyOf == null) {
            player.sendMessage(TextStrings.colorize("Error."));
            return;
        }
        selectedLobbyOf.send(player);
    }

    public LobbyGroup getCurrentGroupOf(Lobby lobby) {
        if (lobby == null) return null;

        for (LobbyGroup lobbyGroup : lobbyGroupMap.values()) {
            if (lobbyGroup.getLobbies().contains(lobby)) {
                return lobbyGroup;
            }
        }
        return null;
    }

    public LobbyGroup getLobbyGroup(String name) {
        return this.lobbyGroupMap.get(name);
    }

    public boolean unregisterLobbyGroup(String name) {
        LobbyGroup group = this.lobbyGroupMap.get(name);
        if (group == null) return false;
        for (Lobby lobby : group.getLobbies()) {
            deleteLobby(lobby.getWorld().getName());
        }
        this.lobbyGroupMap.remove(name);
        return true;
    }

    public void deleteLobby(String name) {
        lobbyNameMap.remove(name);
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
            // TODO: read player data and get what category he selected.

            return getDefaultGroup();
        }
        return getDefaultGroup();
    }

    public void save() {
        new LobbyDataManager(plugin).saveData();
    }

    public void load() {
        new LobbyDataManager(plugin).loadData();
    }

    final String defaultKey = "default";
    public LobbyGroup getDefaultGroup() {
        if (!this.lobbyGroupMap.containsKey(defaultKey)) {
            LobbyGroup default_group = new LobbyGroup(defaultKey);
            this.lobbyGroupMap.put(default_group.getName(), default_group);
        }
        Bukkit.getLogger().info("default lobby sending.");
        return this.lobbyGroupMap.get(defaultKey);
    }
}
