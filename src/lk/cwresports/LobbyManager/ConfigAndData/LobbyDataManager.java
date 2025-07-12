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
import java.util.*;

public class LobbyDataManager {
    private final JavaPlugin plugin;
    private final LobbyManager lobbyManager;
    private final File dataFile;

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

            // Clear previous data
            for (String key : config.getKeys(false)) config.set(key, null);

            // Save lobbies
            ConfigurationSection lobbiesSection = config.createSection("lobbies");
            for (Map.Entry<String, Lobby> entry : lobbyManager.lobbyNameMap.entrySet()) {
                Lobby lobby = entry.getValue();
                ConfigurationSection lobbySection = lobbiesSection.createSection(lobby.getWorld().getName());

                // Always save all spawn locations
                List<String> serializedLocations = new ArrayList<>();
                for (Location loc : lobby.getSpawnLocations()) {
                    serializedLocations.add(serializeLocation(loc));
                }
                lobbySection.set("spawn_locations", serializedLocations);

                // Save location type
                lobbySection.set("location_type", lobby.getLocationTypes().name());

                // Save event lobby data if applicable
                if (lobby instanceof EventLobbies event) {
                    lobbySection.set("event_lobby", true);
                    lobbySection.set("event_date", event.getEventDate());
                    lobbySection.set("expire_days", event.getExpireDays());
                }
            }

            // Save groups
            ConfigurationSection groupsSection = config.createSection("groups");
            for (Map.Entry<String, LobbyGroup> entry : lobbyManager.lobbyGroupMap.entrySet()) {
                LobbyGroup group = entry.getValue();
                ConfigurationSection groupSection = groupsSection.createSection(group.getName());

                List<String> lobbyNames = new ArrayList<>();
                for (Lobby lobby : group.getLobbies()) {
                    lobbyNames.add(lobby.getWorld().getName());
                }
                groupSection.set("lobbies", lobbyNames);

                if (group.getCurrentLobby() != null)
                    groupSection.set("current_lobby", group.getCurrentLobby().getWorld().getName());

                groupSection.set("rotation_time_unit", group.getLobbyRotationTimeUnit().name());
                if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL)
                    groupSection.set("next_rotation_time", group.getNextRotationTime());
            }

            // Save default lobby
            if (lobbyManager.defaultSelectedLobby != null)
                config.set("default_lobby", lobbyManager.defaultSelectedLobby.getWorld().getName());

            // Save event lobby names for reference
            List<String> eventLobbyNames = new ArrayList<>();
            for (EventLobbies eventLobby : lobbyManager.getEventLobbies())
                eventLobbyNames.add(eventLobby.getWorld().getName());
            config.set("event_lobbies", eventLobbyNames);

            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save lobby data: " + e.getMessage());
        }
    }


    public void loadData() {
        lobbyManager.isLoading = true;
        lobbyManager.lobbyNameMap.clear();
        lobbyManager.lobbyGroupMap.clear();
        lobbyManager.getEventLobbies().clear();
        lobbyManager.defaultSelectedLobby = null;
        if (!dataFile.exists()) {
            lobbyManager.isLoading = false;
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        Map<String, Lobby> loadedLobbies = new HashMap<>();
        ConfigurationSection lobbiesSection = config.getConfigurationSection("lobbies");
        if (lobbiesSection != null) {
            for (String lobbyName : lobbiesSection.getKeys(false)) {
                ConfigurationSection lobbySection = lobbiesSection.getConfigurationSection(lobbyName);
                World world = Bukkit.getWorld(lobbyName);
                if (world == null) continue;
                List<String> serializedLocations = lobbySection.getStringList("spawn_locations");
                if (serializedLocations.isEmpty()) continue;
                Location firstLoc = deserializeLocation(serializedLocations.get(0));
                if (firstLoc == null) continue;
                Lobby lobby;
                if (lobbySection.getBoolean("event_lobby", false)) {
                    EventLobbies eventLobby = new EventLobbies(firstLoc);
                    eventLobby.getSpawnLocations().clear();
                    for (String serializedLoc : serializedLocations) {
                        Location loc = deserializeLocation(serializedLoc);
                        if (loc != null) eventLobby.addSpawnLocation(loc);
                    }
                    eventLobby.setLocationTypes(NextLocationTypes.valueOf(lobbySection.getString("location_type", "DEFAULT")));
                    eventLobby.setPeriod(
                            lobbySection.getString("event_date", null),
                            lobbySection.getString("expire_days", "0")
                    );
                    lobby = eventLobby;
                    // No need to manually add to eventLobbies - constructor already did it
                } else {
                    lobby = new Lobby(firstLoc);
                    lobby.getSpawnLocations().clear();
                    for (String serializedLoc : serializedLocations) {
                        Location loc = deserializeLocation(serializedLoc);
                        if (loc != null) lobby.addSpawnLocation(loc);
                    }
                    lobby.setLocationTypes(NextLocationTypes.valueOf(lobbySection.getString("location_type", "DEFAULT")));
                }
                loadedLobbies.put(lobbyName, lobby);
                lobbyManager.registerNameFor(lobby);
            }
        }

        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
                LobbyGroup group = new LobbyGroup(groupName);
                List<String> lobbyNames = groupSection.getStringList("lobbies");
                for (String lobbyName : lobbyNames) {
                    Lobby lobby = loadedLobbies.get(lobbyName);
                    if (lobby != null) group.addLobby(lobby);
                }
                String currentLobbyName = groupSection.getString("current_lobby");
                if (currentLobbyName != null) {
                    Lobby currentLobby = loadedLobbies.get(currentLobbyName);
                    if (currentLobby != null) group.setCurrentLobby(currentLobby);
                }
                lobbyManager.registerLobbyGroup(group);
                String rotationUnit = groupSection.getString("rotation_time_unit");
                if (rotationUnit != null) {
                    group.setLobbyRotationTimeUnit(TimeUnits.valueOf(rotationUnit));
                    if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL)
                        group.setNextRotationTime(groupSection.getLong("next_rotation_time", -1));
                }
            }
        }

        String defaultLobbyName = config.getString("default_lobby");
        if (defaultLobbyName != null) {
            Lobby defaultLobby = loadedLobbies.get(defaultLobbyName);
            if (defaultLobby != null)
                lobbyManager.defaultSelectedLobby = defaultLobby;
        }

        // Removed the duplicate event lobby loading section
        lobbyManager.isLoading = false;
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
