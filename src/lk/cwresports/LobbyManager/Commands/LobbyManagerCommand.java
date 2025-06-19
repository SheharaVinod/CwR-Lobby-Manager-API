package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyManagerCommand implements CommandExecutor, TabCompleter {
    
    private final CwRLobbyAPI plugin;
    private final LobbyManager lobbyManager;
    
    public LobbyManagerCommand(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.lobbyManager = LobbyManager.getInstance();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "creategroup":
                return handleCreateGroup(sender, args);
            case "deletegroup":
                return handleDeleteGroup(sender, args);
            case "addlobby":
                return handleAddLobby(sender, args);
            case "removelobby":
                return handleRemoveLobby(sender, args);
            case "assigngroup":
                return handleAssignGroup(sender, args);
            case "groups":
                return handleListGroups(sender);
            case "reload":
                return handleReload(sender);
            case "help":
            default:
                sendHelpMessage(sender);
                return true;
        }
    }
    
    private boolean handleCreateGroup(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.creategroup")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to create groups!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /lm creategroup <groupName>");
            return true;
        }
        
        String groupName = args[1].toLowerCase();
        
        if (groupName.equals("default")) {
            sender.sendMessage(ChatColor.RED + "Cannot create a group named 'default' - it already exists!");
            return true;
        }
        
        if (lobbyManager.getLobbyGroup(groupName) != null) {
            sender.sendMessage(ChatColor.RED + "Group '" + groupName + "' already exists!");
            return true;
        }
        
        boolean success = lobbyManager.createLobbyGroup(groupName);
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully created lobby group '" + groupName + "'!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to create lobby group '" + groupName + "'!");
        }
        
        return true;
    }
    
    private boolean handleDeleteGroup(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.deletegroup")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to delete groups!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /lm deletegroup <groupName>");
            return true;
        }
        
        String groupName = args[1].toLowerCase();
        
        if (groupName.equals("default")) {
            sender.sendMessage(ChatColor.RED + "Cannot delete the 'default' group!");
            return true;
        }
        
        if (lobbyManager.getLobbyGroup(groupName) == null) {
            sender.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return true;
        }
        
        boolean success = lobbyManager.deleteLobbyGroup(groupName);
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully deleted lobby group '" + groupName + "'!");
            sender.sendMessage(ChatColor.YELLOW + "All lobbies from this group have been moved to the default group.");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to delete lobby group '" + groupName + "'!");
        }
        
        return true;
    }
    
    private boolean handleAddLobby(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.addlobby")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to add lobbies!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /lm addlobby <lobbyName>");
            return true;
        }
        
        Player player = (Player) sender;
        String lobbyName = args[1];
        
        boolean success = lobbyManager.addLobby(lobbyName, player.getLocation());
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully added lobby '" + lobbyName + "' to the default group!");
            sender.sendMessage(ChatColor.YELLOW + "Location: " + formatLocation(player.getLocation()));
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to add lobby '" + lobbyName + "' (lobby may already exist)!");
        }
        
        return true;
    }
    
    private boolean handleRemoveLobby(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.removelobby")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to remove lobbies!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /lm removelobby <lobbyName>");
            return true;
        }
        
        String lobbyName = args[1];
        
        boolean success = lobbyManager.removeLobby(lobbyName);
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully removed lobby '" + lobbyName + "'!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to remove lobby '" + lobbyName + "' (lobby may not exist)!");
        }
        
        return true;
    }
    
    private boolean handleAssignGroup(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.assigngroup")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to assign groups!");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /lm assigngroup <lobbyName> <groupName>");
            return true;
        }
        
        String lobbyName = args[1];
        String groupName = args[2].toLowerCase();
        
        if (lobbyManager.getLobbyGroup(groupName) == null) {
            sender.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return true;
        }
        
        boolean success = lobbyManager.assignLobbyToGroup(lobbyName, groupName);
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully assigned lobby '" + lobbyName + "' to group '" + groupName + "'!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to assign lobby '" + lobbyName + "' to group '" + groupName + "'!");
        }
        
        return true;
    }
    
    private boolean handleListGroups(CommandSender sender) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.groups")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to list groups!");
            return true;
        }
        
        List<String> groupInfo = lobbyManager.getGroupsInfo();
        
        sender.sendMessage(ChatColor.GOLD + "=== Lobby Groups ===");
        for (String info : groupInfo) {
            sender.sendMessage(ChatColor.YELLOW + info);
        }
        
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("cwr-core.lobby-manager.admin.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin!");
            return true;
        }
        
        boolean success = lobbyManager.reloadConfiguration();
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded Lobby Manager configuration!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to reload configuration!");
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Lobby Manager Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/lm creategroup <name>" + ChatColor.WHITE + " - Create a new lobby group");
        sender.sendMessage(ChatColor.YELLOW + "/lm deletegroup <name>" + ChatColor.WHITE + " - Delete a lobby group");
        sender.sendMessage(ChatColor.YELLOW + "/lm addlobby <name>" + ChatColor.WHITE + " - Add current location as lobby");
        sender.sendMessage(ChatColor.YELLOW + "/lm removelobby <name>" + ChatColor.WHITE + " - Remove a lobby");
        sender.sendMessage(ChatColor.YELLOW + "/lm assigngroup <lobby> <group>" + ChatColor.WHITE + " - Assign lobby to group");
        sender.sendMessage(ChatColor.YELLOW + "/lm groups" + ChatColor.WHITE + " - List all groups and their lobbies");
        sender.sendMessage(ChatColor.YELLOW + "/lm reload" + ChatColor.WHITE + " - Reload plugin configuration");
    }
    
    private String formatLocation(org.bukkit.Location location) {
        return String.format("World: %s, X: %.1f, Y: %.1f, Z: %.1f", 
            location.getWorld().getName(), 
            location.getX(), 
            location.getY(), 
            location.getZ());
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("creategroup", "deletegroup", "addlobby", 
                "removelobby", "assigngroup", "groups", "reload", "help");
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("deletegroup") || subCommand.equals("assigngroup")) {
                // Add group names for completion
                for (String groupName : lobbyManager.getAllGroupNames()) {
                    if (groupName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(groupName);
                    }
                }
            } else if (subCommand.equals("removelobby")) {
                // Add lobby names for completion
                for (String lobbyName : lobbyManager.getAllLobbyNames()) {
                    if (lobbyName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(lobbyName);
                    }
                }
            }
        } else if (args.length == 3 && args[0].toLowerCase().equals("assigngroup")) {
            // Add group names for assigngroup command
            for (String groupName : lobbyManager.getAllGroupNames()) {
                if (groupName.toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(groupName);
                }
            }
        }
        
        return completions;
    }
}
