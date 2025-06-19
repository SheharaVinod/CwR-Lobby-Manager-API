package lk.cwresports.LobbyManager.Data;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LobbiesDataManager {
    
    private final CwRLobbyAPI plugin;
    private File lobbiesDataFile;
    private FileConfiguration lobbiesDataConfig;
    
    public LobbiesDataManager(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        setupLobbiesDataFile();
    }
    
    private void setupLobbiesDataFile() {
        lobbiesDataFile = new File(plugin.getDataFolder(), "lobbiesData.yml");
        
        if (!lobbiesDataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                lobbiesDataFile.createNewFile();
                
                lobbiesDataConfig = YamlConfiguration.loadConfiguration(lobbiesDataFile);
                lobbiesDataConfig.createSection("lobbies");
                lobbiesDataConfig.save(lobbiesDataFile);
                
                plugin.getLogger().info("Created lobbiesData.yml");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create lobbiesData.yml", e);
            }
        }
        
        lobbiesDataConfig = YamlConfiguration.loadConfiguration(lobbiesDataFile);
    }
    
    public boolean addLobby(String lobbyName, String groupName, Location location) {
        if (lobbyName == null || location == null) {
            return false;
        }
        
        String basePath = "lobbies." + lobbyName;
        
        if (lobbiesDataConfig.contains(basePath)) {
            return false; // Lobby already exists
        }
        
        lobbiesDataConfig.set(basePath + ".group", groupName != null ? groupName : "default");
        lobbiesDataConfig.set(basePath + ".world", location.getWorld().getName());
        lobbiesDataConfig.set(basePath + ".x", location.getX());
        lobbiesDataConfig.set(basePath + ".y", location.getY());
        lobbiesDataConfig.set(basePath + ".z", location.getZ());
        lobbiesDataConfig.set(basePath + ".yaw", location.getYaw());
        lobbiesDataConfig.set(basePath + ".pitch", location.getPitch());
        
        return saveConfig();
    }
    
    public boolean removeLobby(String lobbyName) {
        if (lobbyName == null) {
            return false;
        }
        
        String basePath = "lobbies." + lobbyName;
        
        if (!lobbiesDataConfig.contains(basePath)) {
            return false; // Lobby doesn't exist
        }
        
        lobbiesDataConfig.set(basePath, null);
        return saveConfig();
    }
    
    public boolean assignLobbyToGroup(String lobbyName, String groupName) {
        if (lobbyName == null || groupName == null) {
            return false;
        }
        
        String basePath = "lobbies." + lobbyName;
        
        if (!lobbiesDataConfig.contains(basePath)) {
            return false; // Lobby doesn't exist
        }
        
        lobbiesDataConfig.set(basePath + ".group", groupName);
        return saveConfig();
    }
    
    public Map<String, String> getAllLobbiesWithGroups() {
        Map<String, String> lobbiesMap = new HashMap<>();
        
        if (!lobbiesDataConfig.contains("lobbies")) {
            return lobbiesMap;
        }
        
        for (String lobbyName : lobbiesDataConfig.getConfigurationSection("lobbies").getKeys(false)) {
            String group = lobbiesDataConfig.getString("lobbies." + lobbyName + ".group", "default");
            lobbiesMap.put(lobbyName, group);
        }
        
        return lobbiesMap;
    }
    
    public List<String> getLobbiesInGroup(String groupName) {
        List<String> lobbies = new ArrayList<>();
        
        if (!lobbiesDataConfig.contains("lobbies")) {
            return lobbies;
        }
        
        for (String lobbyName : lobbiesDataConfig.getConfigurationSection("lobbies").getKeys(false)) {
            String group = lobbiesDataConfig.getString("lobbies." + lobbyName + ".group", "default");
            if (group.equals(groupName)) {
                lobbies.add(lobbyName);
            }
        }
        
        return lobbies;
    }
    
    public Location getLobbyLocation(String lobbyName) {
        if (lobbyName == null) {
            return null;
        }
        
        String basePath = "lobbies." + lobbyName;
        
        if (!lobbiesDataConfig.contains(basePath)) {
            return null;
        }
        
        try {
            String worldName = lobbiesDataConfig.getString(basePath + ".world");
            double x = lobbiesDataConfig.getDouble(basePath + ".x");
            double y = lobbiesDataConfig.getDouble(basePath + ".y");
            double z = lobbiesDataConfig.getDouble(basePath + ".z");
            float yaw = (float) lobbiesDataConfig.getDouble(basePath + ".yaw");
            float pitch = (float) lobbiesDataConfig.getDouble(basePath + ".pitch");
            
            org.bukkit.World world = plugin.getServer().getWorld(worldName);
            if (world != null) {
                return new Location(world, x, y, z, yaw, pitch);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error loading location for lobby " + lobbyName, e);
        }
        
        return null;
    }
    
    public boolean lobbyExists(String lobbyName) {
        return lobbiesDataConfig.contains("lobbies." + lobbyName);
    }
    
    public List<String> getAllLobbyNames() {
        if (!lobbiesDataConfig.contains("lobbies")) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(lobbiesDataConfig.getConfigurationSection("lobbies").getKeys(false));
    }
    
    public void moveLobbiesFromGroupToDefault(String groupName) {
        if (!lobbiesDataConfig.contains("lobbies")) {
            return;
        }
        
        for (String lobbyName : lobbiesDataConfig.getConfigurationSection("lobbies").getKeys(false)) {
            String group = lobbiesDataConfig.getString("lobbies." + lobbyName + ".group", "default");
            if (group.equals(groupName)) {
                lobbiesDataConfig.set("lobbies." + lobbyName + ".group", "default");
            }
        }
        
        saveConfig();
    }
    
    public boolean saveConfig() {
        try {
            lobbiesDataConfig.save(lobbiesDataFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save lobbiesData.yml", e);
            return false;
        }
    }
    
    public void reloadConfig() {
        lobbiesDataConfig = YamlConfiguration.loadConfiguration(lobbiesDataFile);
    }
}