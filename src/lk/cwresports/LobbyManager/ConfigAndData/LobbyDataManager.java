package lk.cwresports.LobbyManager.ConfigAndData;

import lk.cwresports.LobbyManager.API.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

            // Save all lobbies with their spawn locations
            ConfigurationSection lobbiesSection = config.createSection("lobbies");
            for (Map.Entry<String, Lobby> entry : lobbyManager.lobbyNameMap.entrySet()) {
                Lobby lobby = entry.getValue();
                ConfigurationSection lobbySection = lobbiesSection.createSection(lobby.getWorld().getName());

                // Save spawn locations
                List<String> serializedLocations = new ArrayList<>();
                for (Location loc : lobby.getSpawnLocations()) {
                    serializedLocations.add(serializeLocation(loc));
                }
                lobbySection.set("spawn_locations", serializedLocations);
                lobbySection.set("location_type", lobby.getLocationTypes().name());
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

                // Save current lobby
                if (group.getCurrentLobby() != null) {
                    groupSection.set("current_lobby", group.getCurrentLobby().getWorld().getName());
                }

                groupSection.set("rotation_time_unit", group.getLobbyRotationTimeUnit().name());
                if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL) {
                    groupSection.set("next_rotation_time", group.getNextRotationTime());
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

        // First load all lobbies with their spawn locations
        ConfigurationSection lobbiesSection = config.getConfigurationSection("lobbies");
        if (lobbiesSection != null) {
            for (String lobbyName : lobbiesSection.getKeys(false)) {
                ConfigurationSection lobbySection = lobbiesSection.getConfigurationSection(lobbyName);
                World world = Bukkit.getWorld(lobbyName);
                if (world == null) continue;

                // Load spawn locations first
                List<String> serializedLocations = lobbySection.getStringList("spawn_locations");
                if (serializedLocations.isEmpty()) continue;

                // Use the first saved location as the initial location
                Location firstLoc = deserializeLocation(serializedLocations.get(0));
                if (firstLoc == null) continue;

                // Create lobby with the saved location
                Lobby lobby = new GroupLobbies(firstLoc);

                // Clear the default spawn location added in constructor
                lobby.getSpawnLocations().clear();

                // Add all spawn locations
                for (String serializedLoc : serializedLocations) {
                    Location loc = deserializeLocation(serializedLoc);
                    if (loc != null) {
                        lobby.addSpawnLocation(loc);
                    }
                }

                // Load location type
                String locationType = lobbySection.getString("location_type");
                if (locationType != null) {
                    try {
                        lobby.setLocationTypes(NextLocationTypes.valueOf(locationType));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid location type for lobby " + lobbyName);
                    }
                }
            }
        }

        // Then load groups and assign lobbies to them
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

                String rotationUnit = groupSection.getString("rotation_time_unit");
                if (rotationUnit != null) {
                    try {
                        group.setLobbyRotationTimeUnit(TimeUnits.valueOf(rotationUnit));
                        if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL) {
                            group.setNextRotationTime(groupSection.getLong("next_rotation_time", -1));
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid rotation unit for group " + groupName);
                    }
                }
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

    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    private Location deserializeLocation(String serialized) {
        try {
            String[] parts = serialized.split(",");
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to deserialize location: " + serialized);
            return null;
        }
    }
}