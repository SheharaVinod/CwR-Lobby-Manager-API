package lk.cwresports.LobbyManager.Tasks;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.scheduler.BukkitRunnable;

public class DataCleanupTask extends BukkitRunnable {
    
    private final CwRLobbyAPI plugin;
    
    public DataCleanupTask(CwRLobbyAPI plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        try {
            plugin.getLogger().info("Starting scheduled player data cleanup...");
            
            long maxOfflineTime = plugin.getConfigManager().getDataCleanupTimeMillis();
            int beforeCount = plugin.getPlayerDataManager().getPlayerDataCount();
            
            plugin.getPlayerDataManager().cleanupOldPlayerData(maxOfflineTime);
            
            int afterCount = plugin.getPlayerDataManager().getPlayerDataCount();
            int cleaned = beforeCount - afterCount;
            
            if (cleaned > 0) {
                plugin.getLogger().info("Data cleanup completed: " + cleaned + " old player data files removed");
            } else {
                plugin.getLogger().info("Data cleanup completed: No old data files found");
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error during data cleanup: " + e.getMessage());
        }
    }
}