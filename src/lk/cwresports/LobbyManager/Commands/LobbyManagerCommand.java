package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class LobbyManagerCommand implements CommandExecutor {
    public static final String NAME = "lobby-manager";

    public static final String sub_create_group = "create_group";
    public static final String sub_create_lobby = "create_lobby";
    public static final String sub_admin = "admin";
    public static final String sub_help = "help";

    private static final Set<Player> admins = new HashSet<>();

    public static void removeAdmin(Player player) {
        admins.remove(player);
    }

    public static boolean isAdmin(Player player) {
        return admins.contains(player);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player admin)) {
            return true;
        }

        if (!admin.hasPermission(PermissionNodes.ADMIN)) {
            admin.sendMessage(TextStrings.colorize(TextStrings.YOU_DONT_HAVE_PERMISSION));
            return true;
        }
        // TODO: toggle admin here.


        if (!isAdmin(admin)) {
            admin.sendMessage(TextStrings.colorize(TextStrings.YOU_ARE_NOT_IN_ADMIN_MOD));
            return true;
        }



        return true;
    }

    public boolean admin(Player admin, String[] strings) {

        return true;
    }

    public boolean create_group(Player admin, String[] strings) {

        return true;
    }

    public boolean create_lobby(Player admin, String[] strings) {

        return true;
    }

    public boolean help(Player admin, String[] strings) {
        return true;
    }
}
