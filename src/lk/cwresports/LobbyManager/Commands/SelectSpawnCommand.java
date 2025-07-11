package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.ConfigAndData.PlayerDataManager;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectSpawnCommand implements CommandExecutor {
    public static final String NAME = "select-spawn";
    private final PlayerDataManager playerDataManager;

    public SelectSpawnCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextStrings.colorize(TextStrings.ONLY_PLAYERS_CAN_EXECUTE_THIS_COMMAND));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(TextStrings.colorize("&cUsage: /select-spawn <group>"));
            return true;
        }

        String groupName = args[0].toLowerCase();

        if (groupName.equalsIgnoreCase("default")) {
            playerDataManager.deleteFileOf(player);
            player.sendMessage(TextStrings.colorize("&aSelected group: " + groupName, false));
            return true;
        }

        // Check if group exists
        if (!LobbyManager.getInstance().getGroups().contains(groupName)) {
            player.sendMessage(TextStrings.colorize("&cGroup not found!"));
            return true;
        }

        // Check permission for the specific group
        String permission = "cwr-core.lobby-manager.spawn." + groupName;
        if (!player.hasPermission(permission)) {
            player.sendMessage(TextStrings.colorize("&cYou don't have permission to select this group!"));
            return true;
        }

        // Save selection to player data
        playerDataManager.setAndSave(player, "selected-group", groupName);
        player.sendMessage(TextStrings.colorize("&aSelected group: " + groupName, false));
        return true;
    }
}