package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    
    private final CwRLobbyAPI plugin;
    private final LobbyManager lobbyManager;
    
    public SpawnCommand(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.lobbyManager = LobbyManager.getInstance();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        if (!sender.hasPermission("cwr-core.lobby-manager.command.spawn")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        try {
            boolean success = lobbyManager.sendToLobby(player);
            if (success) {
                player.sendMessage(ChatColor.GREEN + "Teleporting you to lobby...");
            } else {
                player.sendMessage(ChatColor.RED + "Failed to teleport to lobby! No available lobbies found.");
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "An error occurred while teleporting to lobby!");
            plugin.getLogger().warning("Error teleporting player " + player.getName() + " to lobby: " + e.getMessage());
        }
        
        return true;
    }
}