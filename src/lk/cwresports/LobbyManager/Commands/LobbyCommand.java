package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyCommand implements CommandExecutor, TabCompleter {
    
    private final CwRLobbyAPI plugin;
    private final LobbyManager lobbyManager;
    
    public LobbyCommand(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.lobbyManager = LobbyManager.getInstance();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Open lobby selection GUI if player has permission
            if (player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
                lobbyManager.openLobbySelectionGUI(player);
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to select lobby groups!");
                player.sendMessage(ChatColor.YELLOW + "Use /spawn to teleport to lobby.");
            }
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "select":
                return handleSelectGroup(player, args);
            case "teleport":
                return handleTeleport(player, args);
            case "list":
                return handleListGroups(player);
            default:
                sendHelpMessage(player);
                return true;
        }
    }
    
    private boolean handleSelectGroup(Player player, String[] args) {
        if (!player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to select lobby groups!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /lobby select <groupName>");
            return true;
        }
        
        String groupName = args[1].toLowerCase();
        
        if (lobbyManager.getLobbyGroup(groupName) == null) {
            player.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return true;
        }
        
        boolean success = lobbyManager.setPlayerSelectedGroup(player, groupName);
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Successfully selected lobby group '" + groupName + "'!");
            player.sendMessage(ChatColor.YELLOW + "Use /spawn to teleport to a lobby in this group.");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to select lobby group '" + groupName + "'!");
        }
        
        return true;
    }
    
    private boolean handleTeleport(Player player, String[] args) {
        if (!player.hasPermission("cwr-core.lobby-manager.command.teleport")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to teleport directly to lobbies!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /lobby teleport <lobbyName>");
            return true;
        }
        
        String lobbyName = args[1];
        
        boolean success = lobbyManager.teleportToSpecificLobby(player, lobbyName);
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Teleporting you to lobby '" + lobbyName + "'...");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to teleport to lobby '" + lobbyName + "'! Lobby may not exist or be offline.");
        }
        
        return true;
    }
    
    private boolean handleListGroups(Player player) {
        if (!player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to view lobby groups!");
            return true;
        }
        
        List<String> availableGroups = lobbyManager.getAvailableGroupsForPlayer(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Available Lobby Groups ===");
        for (String groupInfo : availableGroups) {
            player.sendMessage(ChatColor.YELLOW + groupInfo);
        }
        
        String currentGroup = lobbyManager.getPlayerSelectedGroup(player);
        if (currentGroup != null) {
            player.sendMessage(ChatColor.GREEN + "Current selected group: " + currentGroup);
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Lobby Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/lobby" + ChatColor.WHITE + " - Open lobby group selection GUI");
        
        if (player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
            player.sendMessage(ChatColor.YELLOW + "/lobby select <group>" + ChatColor.WHITE + " - Select a lobby group");
            player.sendMessage(ChatColor.YELLOW + "/lobby list" + ChatColor.WHITE + " - List available lobby groups");
        }
        
        if (player.hasPermission("cwr-core.lobby-manager.command.teleport")) {
            player.sendMessage(ChatColor.YELLOW + "/lobby teleport <name>" + ChatColor.WHITE + " - Teleport to specific lobby");
        }
        
        player.sendMessage(ChatColor.YELLOW + "/spawn" + ChatColor.WHITE + " - Teleport to your selected lobby");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        
        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("list"));
            
            if (player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
                subCommands.add("select");
            }
            
            if (player.hasPermission("cwr-core.lobby-manager.command.teleport")) {
                subCommands.add("teleport");
            }
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("select") && player.hasPermission(PermissionNodes.MORE_THAN_DEFAULT)) {
                for (String groupName : lobbyManager.getAllGroupNames()) {
                    if (groupName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(groupName);
                    }
                }
            } else if (subCommand.equals("teleport") && player.hasPermission("cwr-core.lobby-manager.command.teleport")) {
                for (String lobbyName : lobbyManager.getAllLobbyNames()) {
                    if (lobbyName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(lobbyName);
                    }
                }
            }
        }
        
        return completions;
    }
}