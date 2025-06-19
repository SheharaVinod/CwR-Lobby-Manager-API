package lk.cwresports.LobbyManager.Data;

import lk.cwresports.LobbyManager.API.LoadBalancingStrategy;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    
    private final CwRLobbyAPI plugin;
    private FileConfiguration config;
    
    public ConfigManager(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }
    
    private void setupDefaultConfig() {
        // Set default values if they don't exist
        if (!config.contains("load-balancing-strategy")) {
            config.set("load-balancing-strategy", "LEAST_PLAYERS");
        }
        
        if (!config.contains("data-cleanup-days")) {
            config.set("data-cleanup-days", 8);
        }
        
        if (!config.contains("cleanup-interval-hours")) {
            config.set("cleanup-interval-hours", 24);
        }
        
        if (!config.contains("blocked-commands")) {
            config.set("blocked-commands", Arrays.asList(
                "gamemode", "gm", "fly", "give", "tp", "teleport", 
                "kill", "suicide", "weather", "time"
            ));
        }
        
        if (!config.contains("messages.no-permission")) {
            config.set("messages.no-permission", "&cYou don't have permission to use this command!");
        }
        
        if (!config.contains("messages.teleporting-to-lobby")) {
            config.set("messages.teleporting-to-lobby", "&aTeleporting you to lobby...");
        }
        
        if (!config.contains("messages.no-lobbies-available")) {
            config.set("messages.no-lobbies-available", "&cNo lobbies are currently available!");
        }
        
        if (!config.contains("messages.command-blocked")) {
            config.set("messages.command-blocked", "&cThis command is blocked in lobbies!");
        }
        
        if (!config.contains("gui.lobby-selector.title")) {
            config.set("gui.lobby-selector.title", "Select Lobby Group");
        }
        
        if (!config.contains("gui.lobby-selector.default-group-name")) {
            config.set("gui.lobby-selector.default-group-name", "Default Lobbies");
        }
        
        if (!config.contains("gui.lobby-selector.default-group-description")) {
            config.set("gui.lobby-selector.default-group-description", Arrays.asList(
                "&7Click to select default lobbies",
                "&7Available to all players"
            ));
        }
        
        plugin.saveConfig();
    }
    
    public LoadBalancingStrategy getLoadBalancingStrategy() {
        String strategyName = config.getString("load-balancing-strategy", "LEAST_PLAYERS");
        try {
            return LoadBalancingStrategy.valueOf(strategyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid load balancing strategy: " + strategyName + ". Using LEAST_PLAYERS");
            return LoadBalancingStrategy.LEAST_PLAYERS;
        }
    }
    
    public int getDataCleanupDays() {
        return config.getInt("data-cleanup-days", 8);
    }
    
    public int getCleanupIntervalHours() {
        return config.getInt("cleanup-interval-hours", 24);
    }
    
    public List<String> getBlockedCommands() {
        return config.getStringList("blocked-commands");
    }
    
    public String getMessage(String path) {
        return config.getString("messages." + path, "Message not found: " + path)
                .replace("&", "§");
    }
    
    public String getGUITitle() {
        return config.getString("gui.lobby-selector.title", "Select Lobby Group")
                .replace("&", "§");
    }
    
    public String getDefaultGroupDisplayName() {
        return config.getString("gui.lobby-selector.default-group-name", "Default Lobbies")
                .replace("&", "§");
    }
    
    public List<String> getDefaultGroupDescription() {
        return config.getStringList("gui.lobby-selector.default-group-description");
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public boolean isCommandBlocked(String command) {
        List<String> blockedCommands = getBlockedCommands();
        
        // Remove leading slash if present
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        
        // Check if command or its base form is blocked
        String baseCommand = command.split(" ")[0].toLowerCase();
        
        for (String blockedCommand : blockedCommands) {
            if (blockedCommand.equalsIgnoreCase(baseCommand)) {
                return true;
            }
        }
        
        return false;
    }
    
    public long getDataCleanupTimeMillis() {
        return getDataCleanupDays() * 24L * 60L * 60L * 1000L; // Convert days to milliseconds
    }
    
    public long getCleanupIntervalMillis() {
        return getCleanupIntervalHours() * 60L * 60L * 1000L; // Convert hours to milliseconds
    }
}