package lk.cwresports.LobbyManager;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Commands.LobbyCommand;
import lk.cwresports.LobbyManager.Commands.LobbyManagerCommand;
import lk.cwresports.LobbyManager.Commands.SpawnCommand;
import lk.cwresports.LobbyManager.Data.ConfigManager;
import lk.cwresports.LobbyManager.Data.GroupDataManager;
import lk.cwresports.LobbyManager.Data.LobbiesDataManager;
import lk.cwresports.LobbyManager.Data.PlayerDataManager;
import lk.cwresports.LobbyManager.Listeners.CommandBlockerListener;
import lk.cwresports.LobbyManager.Listeners.PlayerConnectionListener;
import lk.cwresports.LobbyManager.Tasks.DataCleanupTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CwRLobbyAPI extends JavaPlugin {
    private static CwRLobbyAPI plugin;
    
    // Managers
    private ConfigManager configManager;
    private GroupDataManager groupDataManager;
    private LobbiesDataManager lobbiesDataManager;
    private PlayerDataManager playerDataManager;
    private LobbyManager lobbyManager;
    
    // Tasks
    private DataCleanupTask dataCleanupTask;

    @Override
    public void onLoad() {
        CwRLobbyAPI.plugin = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("=== CwR Lobby Manager Starting ===");
        
        // Initialize managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Start scheduled tasks
        startScheduledTasks();
        
        getLogger().info("=== CwR Lobby Manager Enabled Successfully ===");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("=== CwR Lobby Manager Disabling ===");
        
        // Cancel scheduled tasks
        if (dataCleanupTask != null) {
            dataCleanupTask.cancel();
        }
        
        // Save any pending data
        if (lobbyManager != null) {
            lobbyManager.saveAllData();
        }
        
        getLogger().info("=== CwR Lobby Manager Disabled ===");
    }
    
    private void initializeManagers() {
        getLogger().info("Initializing managers...");
        
        // Initialize data managers
        configManager = new ConfigManager(this);
        groupDataManager = new GroupDataManager(this);
        lobbiesDataManager = new LobbiesDataManager(this);
        playerDataManager = new PlayerDataManager(this);
        
        // Initialize lobby manager
        lobbyManager = LobbyManager.getInstance();
        lobbyManager.initialize(this);
        
        getLogger().info("All managers initialized successfully!");
    }
    
    private void registerCommands() {
        getLogger().info("Registering commands...");
        
        // Register admin commands
        getCommand("lobbymanager").setExecutor(new LobbyManagerCommand(this));
        getCommand("lobbymanager").setTabCompleter(new LobbyManagerCommand(this));
        
        // Register player commands
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("lobby").setExecutor(new LobbyCommand(this));
        getCommand("lobby").setTabCompleter(new LobbyCommand(this));
        
        getLogger().info("Commands registered successfully!");
    }
    
    private void registerListeners() {
        getLogger().info("Registering event listeners...");
        
        // Register player connection listener
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        
        // Register command blocker listener
        Bukkit.getPluginManager().registerEvents(new CommandBlockerListener(this), this);
        
        getLogger().info("Event listeners registered successfully!");
    }
    
    private void startScheduledTasks() {
        getLogger().info("Starting scheduled tasks...");
        
        // Start data cleanup task
        long cleanupInterval = configManager.getCleanupIntervalMillis() / 1000 / 20; // Convert to ticks
        dataCleanupTask = new DataCleanupTask(this);
        dataCleanupTask.runTaskTimer(this, cleanupInterval, cleanupInterval);
        
        getLogger().info("Scheduled tasks started successfully!");
        getLogger().info("Data cleanup will run every " + configManager.getCleanupIntervalHours() + " hours");
    }

    public static CwRLobbyAPI getPlugin() {
        return plugin;
    }
    
    // Getter methods for managers
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public GroupDataManager getGroupDataManager() {
        return groupDataManager;
    }
    
    public LobbiesDataManager getLobbiesDataManager() {
        return lobbiesDataManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
}
