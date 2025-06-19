package lk.cwresports.LobbyManager.Data;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GroupDataManager {
    
    private final CwRLobbyAPI plugin;
    private File groupDataFile;
    private FileConfiguration groupDataConfig;
    
    public GroupDataManager(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        setupGroupDataFile();
    }
    
    private void setupGroupDataFile() {
        groupDataFile = new File(plugin.getDataFolder(), "groupData.yml");
        
        if (!groupDataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                groupDataFile.createNewFile();
                
                // Create default group data
                groupDataConfig = YamlConfiguration.loadConfiguration(groupDataFile);
                List<String> defaultGroups = new ArrayList<>();
                defaultGroups.add("default");
                groupDataConfig.set("groups", defaultGroups);
                groupDataConfig.save(groupDataFile);
                
                plugin.getLogger().info("Created groupData.yml with default group");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create groupData.yml", e);
            }
        }
        
        groupDataConfig = YamlConfiguration.loadConfiguration(groupDataFile);
    }
    
    public List<String> getAllGroups() {
        List<String> groups = groupDataConfig.getStringList("groups");
        if (groups.isEmpty()) {
            groups.add("default");
        }
        return groups;
    }
    
    public boolean addGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return false;
        }
        
        List<String> groups = getAllGroups();
        if (groups.contains(groupName.toLowerCase())) {
            return false; // Group already exists
        }
        
        groups.add(groupName.toLowerCase());
        groupDataConfig.set("groups", groups);
        return saveConfig();
    }
    
    public boolean removeGroup(String groupName) {
        if (groupName == null || groupName.equals("default")) {
            return false; // Cannot remove default group
        }
        
        List<String> groups = getAllGroups();
        boolean removed = groups.remove(groupName.toLowerCase());
        
        if (removed) {
            groupDataConfig.set("groups", groups);
            return saveConfig();
        }
        
        return false;
    }
    
    public boolean groupExists(String groupName) {
        return getAllGroups().contains(groupName.toLowerCase());
    }
    
    public boolean saveConfig() {
        try {
            groupDataConfig.save(groupDataFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save groupData.yml", e);
            return false;
        }
    }
    
    public void reloadConfig() {
        groupDataConfig = YamlConfiguration.loadConfiguration(groupDataFile);
    }
}