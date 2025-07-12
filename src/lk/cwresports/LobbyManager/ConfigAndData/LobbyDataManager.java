package lk.cwresports.LobbyManager.ConfigAndData;

import lk.cwresports.LobbyManager.API.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
            for (String key : config.getKeys(false)) {
                config.set(key, null);
            }

            // Save lobbies
            ConfigurationSection lobbiesSection = config.createSection("lobbies");
            for (Lobby lobby : lobbyManager.lobbyNameMap.values()) {
                ConfigurationSection lobbySection = lobbiesSection.createSection(lobby.getWorld().getName());

                // Save spawn locations
                List<String> serializedLocations = new ArrayList<>();
                for (Location loc : lobby.getSpawnLocations()) {
                    serializedLocations.add(serializeLocation(loc));
                }
                lobbySection.set("spawn_locations", serializedLocations);

                // Save location type
                lobbySection.set("location_type", lobby.getLocationTypes().name());
                // Save hunger and damage settings
                lobbySection.set("disabled_hunger", lobby.isDisabledHunger());
                lobbySection.set("disabled_damage", lobby.isDisabledDamage());
                lobbySection.set("game_mode", lobby.getGameMode().name());
                lobbySection.set("cansel_interaction", lobby.isCanselInteraction());

                // Save event lobby data
                if (lobby instanceof EventLobbies event) {
                    lobbySection.set("event_lobby", true);
                    lobbySection.set("event_date", event.getEventDate());
                    lobbySection.set("expire_days", event.getExpireDays());
                }
            }

            // Save groups
            ConfigurationSection groupsSection = config.createSection("groups");
            for (LobbyGroup group : lobbyManager.lobbyGroupMap.values()) {
                ConfigurationSection groupSection = groupsSection.createSection(group.getName());

                List<String> lobbyNames = new ArrayList<>();
                for (Lobby lobby : group.getLobbies()) {
                    lobbyNames.add(lobby.getWorld().getName());
                }
                groupSection.set("lobbies", lobbyNames);

                if (group.getCurrentLobby() != null) {
                    groupSection.set("current_lobby", group.getCurrentLobby().getWorld().getName());
                }

                groupSection.set("rotation_time_unit", group.getLobbyRotationTimeUnit().name());
                if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL) {
                    groupSection.set("next_rotation_time", group.getNextRotationTime());
                }
            }

            // Save default lobby
            if (lobbyManager.defaultSelectedLobby != null) {
                config.set("default_lobby", lobbyManager.defaultSelectedLobby.getWorld().getName());
            }

            // Save unique event lobby names
            List<String> eventLobbyNames = lobbyManager.getEventLobbies().stream()
                    .map(lobby -> lobby.getWorld().getName())
                    .distinct()
                    .collect(Collectors.toList());
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

        // Load lobbies first
        ConfigurationSection lobbiesSection = config.getConfigurationSection("lobbies");
        if (lobbiesSection != null) {
            for (String lobbyName : lobbiesSection.getKeys(false)) {
                ConfigurationSection lobbySection = lobbiesSection.getConfigurationSection(lobbyName);
                World world = Bukkit.getWorld(lobbyName);
                if (world == null) {
                    plugin.getLogger().warning("World not found for lobby: " + lobbyName);
                    continue;
                }

                List<String> serializedLocations = lobbySection.getStringList("spawn_locations");
                if (serializedLocations.isEmpty()) {
                    plugin.getLogger().warning("No spawn locations found for lobby: " + lobbyName);
                    continue;
                }

                Location firstLoc = deserializeLocation(serializedLocations.get(0));
                if (firstLoc == null) {
                    plugin.getLogger().warning("Failed to deserialize first location for lobby: " + lobbyName);
                    continue;
                }

                Lobby lobby;
                if (lobbySection.getBoolean("event_lobby", false)) {
                    EventLobbies eventLobby = new EventLobbies(firstLoc);
                    // Remove potential duplicate added by constructor
                    lobbyManager.getEventLobbies().removeIf(l ->
                            l.getWorld().getName().equalsIgnoreCase(lobbyName));

                    eventLobby.getSpawnLocations().clear();
                    for (String serializedLoc : serializedLocations) {
                        Location loc = deserializeLocation(serializedLoc);
                        if (loc != null) eventLobby.addSpawnLocation(loc);
                    }
                    eventLobby.setLocationTypes(NextLocationTypes.valueOf(
                            lobbySection.getString("location_type", "DEFAULT")));
                    eventLobby.setPeriod(
                            lobbySection.getString("event_date", null),
                            lobbySection.getString("expire_days", "0"));
                    lobby = eventLobby;
                } else {
                    lobby = new GroupLobbies(firstLoc);
                    lobby.getSpawnLocations().clear();
                    for (String serializedLoc : serializedLocations) {
                        Location loc = deserializeLocation(serializedLoc);
                        if (loc != null) lobby.addSpawnLocation(loc);
                    }
                    lobby.setLocationTypes(NextLocationTypes.valueOf(
                            lobbySection.getString("location_type", "DEFAULT")));
                }
                lobby.setDisabledHunger(lobbySection.getBoolean("disabled_hunger", true));
                lobby.setDisabledDamage(lobbySection.getBoolean("disabled_damage", true));

                try {
                    GameMode gameMode = GameMode.valueOf(lobbySection.getString("game_mode", "ADVENTURE"));
                    lobby.setGameMode(gameMode);
                } catch (IllegalArgumentException e) {
                    lobby.setGameMode(GameMode.ADVENTURE);
                }
                lobby.setCanselInteraction(lobbySection.getBoolean("cansel_interaction", false));

                loadedLobbies.put(lobbyName, lobby);
                lobbyManager.registerNameFor(lobby);
            }
        }

        // Load groups and their lobby assignments
        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
                LobbyGroup group = lobbyManager.getLobbyGroup(groupName);
                if (group == null) {
                    group = new LobbyGroup(groupName);
                }

                List<String> lobbyNames = groupSection.getStringList("lobbies");
                for (String lobbyName : lobbyNames) {
                    Lobby lobby = loadedLobbies.get(lobbyName);
                    if (lobby != null && !group.getLobbies().contains(lobby)) {
                        group.addLobby(lobby);
                    }
                }

                String currentLobbyName = groupSection.getString("current_lobby");
                if (currentLobbyName != null) {
                    Lobby currentLobby = loadedLobbies.get(currentLobbyName);
                    if (currentLobby != null) {
                        group.setCurrentLobby(currentLobby);
                    }
                }

                String rotationUnit = groupSection.getString("rotation_time_unit");
                if (rotationUnit != null) {
                    try {
                        group.setLobbyRotationTimeUnit(TimeUnits.valueOf(rotationUnit));
                        if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL) {
                            group.setNextRotationTime(groupSection.getLong("next_rotation_time", -1));
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid rotation time unit for group " + groupName + ": " + rotationUnit);
                    }
                }

                lobbyManager.registerLobbyGroup(group);
            }
        }

        // Load default lobby
        String defaultLobbyName = config.getString("default_lobby");
        if (defaultLobbyName != null) {
            Lobby defaultLobby = loadedLobbies.get(defaultLobbyName);
            if (defaultLobby != null) {
                lobbyManager.defaultSelectedLobby = defaultLobby;
            }
        }

        // Handle event_lobbies section (remove duplicates)
        Set<String> uniqueEventLobbyNames = new HashSet<>(config.getStringList("event_lobbies"));
        for (String lobbyName : uniqueEventLobbyNames) {
            Lobby lobby = loadedLobbies.get(lobbyName);
            if (lobby instanceof EventLobbies) {
                // Ensure we don't add duplicates
                boolean alreadyExists = lobbyManager.getEventLobbies().stream()
                        .anyMatch(l -> l.getWorld().getName().equalsIgnoreCase(lobbyName));
                if (!alreadyExists) {
                    lobbyManager.getEventLobbies().add((EventLobbies) lobby);
                }
            }
        }

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
