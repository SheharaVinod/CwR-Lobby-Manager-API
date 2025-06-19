package lk.cwresports.LobbyManager.Listeners;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    
    private final CwRLobbyAPI plugin;
    private final LobbyManager lobbyManager;
    
    public PlayerConnectionListener(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.lobbyManager = LobbyManager.getInstance();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Handle player join logic - route to appropriate lobby
        lobbyManager.handlePlayerJoin(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Update player's last offline time
        plugin.getPlayerDataManager().updateLastOfflineTime(event.getPlayer());
    }
}