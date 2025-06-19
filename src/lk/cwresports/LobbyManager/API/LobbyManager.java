package lk.cwresports.LobbyManager.API;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyManager {
    private static LobbyManager manager;
    Map<String, LobbyGroup> lobbyGroupMap = new HashMap<>();
    private Lobby defaultSelectedLobby;
    private Lobby specialSelectedLobby;
    private final CwRLobbyAPI plugin;

    public LobbyManager() {
        plugin = CwRLobbyAPI.getPlugin();
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

        return
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
        if (player.hasPermission("cwr-core.lobby-manager.special")) {
            return LobbyGroups.SPECIAL;
        } else {
            return LobbyGroups.DEFAULT;
        }
    }

    public void saveLobbyToConfig(Lobby lobby, LobbyGroups group) {
        FileConfiguration config = plugin.getConfig();
        String basePath = "lobbies." + lobby.getWorldUID().toString();

        config.set(basePath + ".group", lobby.get.name());
        config.set(basePath + ".next-location-type", lobby.getLocationTypes().name());
        config.set(basePath + ".world", lobby.getWorld().getName());
        config.set(basePath + ".default-spawn-location", locationToString(lobby.getDefaultSpawnLocation()));

        List<String> spawnLocations = new ArrayList<>();
        for (Location loc : lobby.getSpawnLocations()) {
            spawnLocations.add(locationToString(loc));
        }
        config.set(basePath + ".spawn-locations", spawnLocations);

        plugin.saveConfig();
    }

    private String locationToString(Location location) {
        return String.format("%.2f,%.2f,%.2f,%.2f,%.2f",
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }
}
