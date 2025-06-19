package lk.cwresports.LobbyManager.API;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Data.ConfigManager;
import lk.cwresports.LobbyManager.Data.GroupDataManager;
import lk.cwresports.LobbyManager.Data.LobbiesDataManager;
import lk.cwresports.LobbyManager.Data.PlayerDataManager;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LobbyManager {
    private static LobbyManager manager;
    
    // Plugin instance and managers
    private CwRLobbyAPI plugin;
    private ConfigManager configManager;
    private GroupDataManager groupDataManager;
    private LobbiesDataManager lobbiesDataManager;
    private PlayerDataManager playerDataManager;
    
    // Cache for loaded lobbies
    private Map<String, LobbyGroup> lobbyGroupMap = new HashMap<>();
    private Map<String, Location> lobbyLocations = new HashMap<>();
    
    private LobbyManager() {
        // Private constructor for singleton
    }
    
    public void initialize(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.groupDataManager = plugin.getGroupDataManager();
        this.lobbiesDataManager = plugin.getLobbiesDataManager();
        this.playerDataManager = plugin.getPlayerDataManager();
        
        loadAllLobbyData();
    }
    
    public static LobbyManager getInstance() {
        if (manager == null) {
            manager = new LobbyManager();
        }
        return manager;
    }
    
    private void loadAllLobbyData() {
        plugin.getLogger().info("Loading lobby data...");
        
        // Load all groups
        List<String> groups = groupDataManager.getAllGroups();
        for (String groupName : groups) {
            LobbyGroup group = new LobbyGroup(groupName);
            lobbyGroupMap.put(groupName, group);
        }
        
        // Load all lobbies and assign to groups
        Map<String, String> lobbiesWithGroups = lobbiesDataManager.getAllLobbiesWithGroups();
        for (Map.Entry<String, String> entry : lobbiesWithGroups.entrySet()) {
            String lobbyName = entry.getKey();
            String groupName = entry.getValue();
            
            Location location = lobbiesDataManager.getLobbyLocation(lobbyName);
            if (location != null) {
                Lobby lobby = new Lobby(location);
                lobbyLocations.put(lobbyName, location);
                
                LobbyGroup group = lobbyGroupMap.get(groupName);
                if (group != null) {
                    group.addLobby(lobby);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + lobbiesWithGroups.size() + " lobbies in " + groups.size() + " groups");
    }
    
    public boolean sendToLobby(Player player) {
        try {
            String selectedGroup = getPlayerSelectedGroup(player);
            LobbyGroup group = lobbyGroupMap.get(selectedGroup);
            
            if (group == null || group.getLobbies().isEmpty()) {
                // Fallback to default group
                group = lobbyGroupMap.get("default");
                if (group == null || group.getLobbies().isEmpty()) {
                    return false; // No lobbies available
                }
            }
            
            Lobby selectedLobby = selectLobbyFromGroup(group);
            if (selectedLobby != null) {
                selectedLobby.send(player);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error sending player " + player.getName() + " to lobby: " + e.getMessage());
            return false;
        }
    }
    
    private Lobby selectLobbyFromGroup(LobbyGroup group) {
        List<Lobby> lobbies = group.getLobbies();
        if (lobbies.isEmpty()) {
            return null;
        }
        
        LoadBalancingStrategy strategy = configManager.getLoadBalancingStrategy();
        
        switch (strategy) {
            case LEAST_PLAYERS:
                return selectLobbyWithLeastPlayers(lobbies);
            case RANDOM:
            default:
                Random random = new Random();
                return lobbies.get(random.nextInt(lobbies.size()));
        }
    }
    
    private Lobby selectLobbyWithLeastPlayers(List<Lobby> lobbies) {
        Lobby selectedLobby = null;
        int minPlayers = Integer.MAX_VALUE;
        
        for (Lobby lobby : lobbies) {
            int playerCount = getPlayersInLobby(lobby);
            if (playerCount < minPlayers) {
                minPlayers = playerCount;
                selectedLobby = lobby;
            }
        }
        
        return selectedLobby != null ? selectedLobby : lobbies.get(0);
    }
    
    private int getPlayersInLobby(Lobby lobby) {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (lobby.getWorld().equals(player.getWorld())) {
                count++;
            }
        }
        return count;
    }
    
    public LobbyGroup getLobbyGroup(String name) {
        return lobbyGroupMap.get(name);
    }
    
    public boolean createLobbyGroup(String groupName) {
        if (groupDataManager.addGroup(groupName)) {
            LobbyGroup group = new LobbyGroup(groupName);
            lobbyGroupMap.put(groupName, group);
            return true;
        }
        return false;
    }
    
    public boolean deleteLobbyGroup(String groupName) {
        if (groupName.equals("default")) {
            return false; // Cannot delete default group
        }
        
        if (groupDataManager.removeGroup(groupName)) {
            // Move all lobbies from this group to default
            lobbiesDataManager.moveLobbiesFromGroupToDefault(groupName);
            
            // Remove from cache
            lobbyGroupMap.remove(groupName);
            
            // Reload lobby data to reflect changes
            loadAllLobbyData();
            return true;
        }
        return false;
    }
    
    public boolean addLobby(String lobbyName, Location location) {
        if (lobbiesDataManager.addLobby(lobbyName, "default", location)) {
            // Add to cache
            lobbyLocations.put(lobbyName, location);
            
            // Add to default group
            Lobby lobby = new Lobby(location);
            LobbyGroup defaultGroup = lobbyGroupMap.get("default");
            if (defaultGroup != null) {
                defaultGroup.addLobby(lobby);
            }
            
            return true;
        }
        return false;
    }
    
    public boolean removeLobby(String lobbyName) {
        if (lobbiesDataManager.removeLobby(lobbyName)) {
            // Remove from cache
            lobbyLocations.remove(lobbyName);
            
            // Remove from all groups
            for (LobbyGroup group : lobbyGroupMap.values()) {
                group.getLobbies().removeIf(lobby -> lobby.getWorld().getName().equals(lobbyName));
            }
            
            return true;
        }
        return false;
    }
    
    public boolean assignLobbyToGroup(String lobbyName, String groupName) {
        if (lobbiesDataManager.assignLobbyToGroup(lobbyName, groupName)) {
            // Reload lobby data to reflect changes
            loadAllLobbyData();
            return true;
        }
        return false;
    }
    
    public List<String> getGroupsInfo() {
        List<String> info = new ArrayList<>();
        
        for (Map.Entry<String, LobbyGroup> entry : lobbyGroupMap.entrySet()) {
            String groupName = entry.getKey();
            LobbyGroup group = entry.getValue();
            
            List<String> lobbiesInGroup = lobbiesDataManager.getLobbiesInGroup(groupName);
            info.add("§e" + groupName + " §7(" + lobbiesInGroup.size() + " lobbies): " + String.join(", ", lobbiesInGroup));
        }
        
        return info;
    }
    
    public boolean reloadConfiguration() {
        try {
            configManager.reloadConfig();
            groupDataManager.reloadConfig();
            lobbiesDataManager.reloadConfig();
            loadAllLobbyData();
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error reloading configuration: " + e.getMessage());
            return false;
        }
    }
    
    public List<String> getAllGroupNames() {
        return new ArrayList<>(lobbyGroupMap.keySet());
    }
    
    public List<String> getAllLobbyNames() {
        return lobbiesDataManager.getAllLobbyNames();
    }
    
    public String getPlayerSelectedGroup(Player player) {
        if (!player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
            return "default";
        }
        
        return playerDataManager.getPlayerSelectedGroup(player);
    }
    
    public boolean setPlayerSelectedGroup(Player player, String groupName) {
        if (!player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
            return false;
        }
        
        if (lobbyGroupMap.containsKey(groupName)) {
            playerDataManager.savePlayerData(player, groupName);
            return true;
        }
        
        return false;
    }
    
    public boolean teleportToSpecificLobby(Player player, String lobbyName) {
        Location location = lobbyLocations.get(lobbyName);
        if (location != null) {
            player.teleport(location);
            return true;
        }
        return false;
    }
    
    public List<String> getAvailableGroupsForPlayer(Player player) {
        List<String> availableGroups = new ArrayList<>();
        
        for (String groupName : lobbyGroupMap.keySet()) {
            List<String> lobbiesInGroup = lobbiesDataManager.getLobbiesInGroup(groupName);
            if (!lobbiesInGroup.isEmpty()) {
                availableGroups.add("§e" + groupName + " §7(" + lobbiesInGroup.size() + " lobbies)");
            }
        }
        
        return availableGroups;
    }
    
    public void handlePlayerJoin(Player player) {
        // This method is called when a player joins the server
        // We can implement logic here to automatically send them to lobby if needed
        // For now, we'll just log it
        plugin.getLogger().info("Player " + player.getName() + " joined - selected group: " + getPlayerSelectedGroup(player));
    }
    
    public boolean isPlayerInLobby(Player player) {
        // Check if player is in any lobby world
        String worldName = player.getWorld().getName();
        
        for (String lobbyName : lobbyLocations.keySet()) {
            Location lobbyLocation = lobbyLocations.get(lobbyName);
            if (lobbyLocation != null && lobbyLocation.getWorld().getName().equals(worldName)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void openLobbySelectionGUI(Player player) {
        if (!player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
            player.sendMessage("§cYou don't have permission to select lobby groups!");
            return;
        }
        
        String title = configManager.getGUITitle();
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // Add default group item
        ItemStack defaultItem = new ItemStack(Material.GRASS);
        ItemMeta defaultMeta = defaultItem.getItemMeta();
        defaultMeta.setDisplayName("§a" + configManager.getDefaultGroupDisplayName());
        
        List<String> defaultLore = new ArrayList<>();
        for (String line : configManager.getDefaultGroupDescription()) {
            defaultLore.add(line.replace("&", "§"));
        }
        defaultMeta.setLore(defaultLore);
        defaultItem.setItemMeta(defaultMeta);
        
        gui.setItem(10, defaultItem);
        
        // Add other groups
        int slot = 12;
        for (String groupName : lobbyGroupMap.keySet()) {
            if (!groupName.equals("default") && slot < 17) {
                List<String> lobbiesInGroup = lobbiesDataManager.getLobbiesInGroup(groupName);
                if (!lobbiesInGroup.isEmpty()) {
                    ItemStack groupItem = new ItemStack(Material.DIAMOND);
                    ItemMeta groupMeta = groupItem.getItemMeta();
                    groupMeta.setDisplayName("§e" + groupName.substring(0, 1).toUpperCase() + groupName.substring(1) + " Lobbies");
                    
                    List<String> groupLore = new ArrayList<>();
                    groupLore.add("§7Click to select " + groupName + " lobbies");
                    groupLore.add("§7Lobbies: " + lobbiesInGroup.size());
                    groupMeta.setLore(groupLore);
                    groupItem.setItemMeta(groupMeta);
                    
                    gui.setItem(slot, groupItem);
                    slot++;
                }
            }
        }
        
        player.openInventory(gui);
    }
    
    public void saveAllData() {
        // Save any pending data when plugin is disabled
        plugin.getLogger().info("Saving all lobby manager data...");
        
        // Force save configurations
        groupDataManager.saveConfig();
        lobbiesDataManager.saveConfig();
        
        plugin.getLogger().info("All lobby manager data saved!");
    }
}