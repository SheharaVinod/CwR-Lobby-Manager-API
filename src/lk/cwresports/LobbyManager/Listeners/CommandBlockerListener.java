package lk.cwresports.LobbyManager.Listeners;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlockerListener implements Listener {
    
    private final CwRLobbyAPI plugin;
    private final LobbyManager lobbyManager;
    
    public CommandBlockerListener(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.lobbyManager = LobbyManager.getInstance();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        
        // Check if player is in a lobby
        if (!lobbyManager.isPlayerInLobby(player)) {
            return; // Player is not in a lobby, allow command
        }
        
        // Check if player has admin permission to bypass command blocking
        if (player.hasPermission("cwr-core.lobby-manager.admin")) {
            return; // Admin can use any command
        }
        
        // Check if command is blocked
        if (plugin.getConfigManager().isCommandBlocked(command)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("command-blocked"));
        }
    }
}