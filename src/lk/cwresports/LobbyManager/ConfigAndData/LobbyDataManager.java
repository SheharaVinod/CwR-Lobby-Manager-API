package lk.cwresports.LobbyManager.ConfigAndData;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.API.Lobby;
import lk.cwresports.LobbyManager.API.LobbyGroup;
import lk.cwresports.LobbyManager.API.EventLobbies;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LobbyDataManager {
    private final JavaPlugin plugin;
    private final LobbyManager lobbyManager;
    private File dataFile;

    public LobbyDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lobbyManager = LobbyManager.getInstance();
        this.dataFile = new File(plugin.getDataFolder(), "lobby_data.yml");
    }

    public void saveData() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

            // Clear existing data
            for (String key : config.getKeys(false)) {
                config.set(key, null);
            }

            // Save lobby groups and their lobbies
            ConfigurationSection groupsSection = config.createSection("groups");
            for (Map.Entry<String, LobbyGroup> entry : lobbyManager.lobbyGroupMap.entrySet()) {
                LobbyGroup group = entry.getValue();
                ConfigurationSection groupSection = groupsSection.createSection(group.getName());

                // Save lobbies in this group
                List<String> lobbyNames = new ArrayList<>();
                for (Lobby lobby : group.getLobbies()) {
                    lobbyNames.add(lobby.getWorld().getName());
                }
                groupSection.set("lobbies", lobbyNames);

                // Save current lobby index if applicable
                if (group.getCurrentLobby() != null) {
                    groupSection.set("current_lobby", group.getCurrentLobby().getWorld().getName());
                }
            }

            // Save default selected lobby
            if (lobbyManager.defaultSelectedLobby != null) {
                config.set("default_lobby", lobbyManager.defaultSelectedLobby.getWorld().getName());
            }

            // Save event lobbies
            List<String> eventLobbyNames = new ArrayList<>();
            for (EventLobbies eventLobby : lobbyManager.getEventLobbies()) {
                eventLobbyNames.add(eventLobby.getWorld().getName());
            }
            config.set("event_lobbies", eventLobbyNames);

            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save lobby data: " + e.getMessage());
        }
    }

    public void loadData() {
        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        // Load groups and their lobbies
        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
                LobbyGroup group = new LobbyGroup(groupName);

                // Load lobbies for this group
                List<String> lobbyNames = groupSection.getStringList("lobbies");
                for (String lobbyName : lobbyNames) {
                    Lobby lobby = lobbyManager.getLobbyByName(lobbyName);
                    if (lobby != null) {
                        group.addLobby(lobby);
                    }
                }

                // Set current lobby if specified
                String currentLobbyName = groupSection.getString("current_lobby");
                if (currentLobbyName != null) {
                    Lobby currentLobby = lobbyManager.getLobbyByName(currentLobbyName);
                    if (currentLobby != null) {
                        group.setCurrentLobby(currentLobby);
                    }
                }

                lobbyManager.registerLobbyGroup(group);
            }
        }

        // Load default selected lobby
        String defaultLobbyName = config.getString("default_lobby");
        if (defaultLobbyName != null) {
            Lobby defaultLobby = lobbyManager.getLobbyByName(defaultLobbyName);
            if (defaultLobby != null) {
                lobbyManager.defaultSelectedLobby = defaultLobby;
            }
        }

        // Load event lobbies
        List<String> eventLobbyNames = config.getStringList("event_lobbies");
        for (String lobbyName : eventLobbyNames) {
            Lobby lobby = lobbyManager.getLobbyByName(lobbyName);
            if (lobby instanceof EventLobbies) {
                lobbyManager.getEventLobbies().add((EventLobbies) lobby);
            }
        }
    }
}