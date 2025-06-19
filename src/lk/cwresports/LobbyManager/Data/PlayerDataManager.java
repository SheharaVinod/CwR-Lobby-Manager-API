package lk.cwresports.LobbyManager.Data;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDataManager {
    
    private final CwRLobbyAPI plugin;
    private File playerDataFolder;
    
    public PlayerDataManager(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        setupPlayerDataFolder();
    }
    
    private void setupPlayerDataFolder() {
        playerDataFolder = new File(plugin.getDataFolder(), "PlayerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
            plugin.getLogger().info("Created PlayerData folder");
        }
    }
    
    public boolean shouldSavePlayerData(Player player) {
        return player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT);
    }
    
    public void savePlayerData(Player player, String selectedGroup) {
        if (!shouldSavePlayerData(player)) {
            return; // Don't save data for players without permission
        }
        
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        
        try {
            FileConfiguration playerConfig;
            
            if (!playerFile.exists()) {
                playerFile.createNewFile();
                playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            } else {
                playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            }
            
            playerConfig.set("player-uuid", playerUUID.toString());
            playerConfig.set("selected-group", selectedGroup);
            playerConfig.set("last-offline", System.currentTimeMillis());
            
            playerConfig.save(playerFile);
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save player data for " + player.getName(), e);
        }
    }
    
    public String getPlayerSelectedGroup(Player player) {
        if (!shouldSavePlayerData(player)) {
            return "default"; // Default group for players without permission
        }
        
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        
        if (!playerFile.exists()) {
            return "default"; // Default if no data exists
        }
        
        try {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            return playerConfig.getString("selected-group", "default");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not load player data for " + player.getName(), e);
            return "default";
        }
    }
    
    public void updateLastOfflineTime(Player player) {
        if (!shouldSavePlayerData(player)) {
            return;
        }
        
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        
        if (!playerFile.exists()) {
            return; // No data to update
        }
        
        try {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            playerConfig.set("last-offline", System.currentTimeMillis());
            playerConfig.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not update offline time for " + player.getName(), e);
        }
    }
    
    public void cleanupOldPlayerData(long maxOfflineTime) {
        if (!playerDataFolder.exists()) {
            return;
        }
        
        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (playerFiles == null) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        int deletedCount = 0;
        
        for (File playerFile : playerFiles) {
            try {
                FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
                long lastOffline = playerConfig.getLong("last-offline", currentTime);
                
                if (currentTime - lastOffline > maxOfflineTime) {
                    if (playerFile.delete()) {
                        deletedCount++;
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error processing player data file: " + playerFile.getName(), e);
            }
        }
        
        if (deletedCount > 0) {
            plugin.getLogger().info("Cleaned up " + deletedCount + " old player data files");
        }
    }
    
    public List<UUID> getAllPlayerUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        
        if (!playerDataFolder.exists()) {
            return uuids;
        }
        
        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (playerFiles == null) {
            return uuids;
        }
        
        for (File playerFile : playerFiles) {
            try {
                String fileName = playerFile.getName();
                String uuidString = fileName.substring(0, fileName.length() - 4); // Remove .yml extension
                UUID uuid = UUID.fromString(uuidString);
                uuids.add(uuid);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Invalid player data file: " + playerFile.getName(), e);
            }
        }
        
        return uuids;
    }
    
    public void deletePlayerData(UUID playerUUID) {
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }
    
    public int getPlayerDataCount() {
        if (!playerDataFolder.exists()) {
            return 0;
        }
        
        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        return playerFiles != null ? playerFiles.length : 0;
    }
}