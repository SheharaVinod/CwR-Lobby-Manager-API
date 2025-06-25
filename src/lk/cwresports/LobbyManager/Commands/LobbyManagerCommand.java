package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.Lobby;
import lk.cwresports.LobbyManager.API.LobbyGroup;
import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.API.NextLocationTypes;
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
    public static final String sub_change_lobby_spawn_rotation = "change_lobby_spawn_rotation";
    public static final String sub_change_group_of = "change_group_of";
    public static final String sub_admin = "admin";
    public static final String sub_help = "help";
    public static final String sub_info = "info";
    public static final String sub_save = "save";

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
        if (!(strings.length > 0)) {
            return help(admin, strings);
        } else {
            if (strings[0].equalsIgnoreCase(sub_admin)) {
                return admin(admin, strings);
            }

            if (!isAdmin(admin)) {
                admin.sendMessage(TextStrings.colorize(TextStrings.YOU_ARE_NOT_IN_ADMIN_MOD));
                return true;
            }

            if (strings[0].equalsIgnoreCase(sub_save)) {
                return save(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_help)) {
                return help(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_info)) {
                return info(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_create_lobby)) {
                return create_lobby(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_create_group)) {
                return create_group(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_change_group_of)) {
                return change_group_of(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_change_lobby_spawn_rotation)) {
                return change_lobby_spawn_rotation(admin, strings);
            }
        }

        return true;
    }

    public boolean admin(Player admin, String[] strings) {
        if (isAdmin(admin)) {
            removeAdmin(admin);
            admin.sendMessage(TextStrings.colorize(TextStrings.YOU_ARE_NO_LONGER_IN_ADMIN_MOD));
        } else {
            admins.add(admin);
            admin.sendMessage(TextStrings.colorize(TextStrings.YOU_ARE_NOW_IN_ADMIN_MOD));
        }
        return true;
    }

    public boolean create_group(Player admin, String[] strings) {
        if (strings.length > 1) {
            if (strings[1].equalsIgnoreCase("default")) {
                admin.sendMessage(TextStrings.colorize(TextStrings.YOU_CANT_CREATE_DEFAULT_GROUP));
                return true;
            } else if (strings[1].isBlank()) {
                admin.sendMessage(TextStrings.colorize(TextStrings.YOU_CANT_CREATE_BLANK_GROUP));
                return true;
            }
            LobbyGroup group = new LobbyGroup(strings[1]);
            // TODO: think
            //? NOTE: when create a new group there are no lobby to teleport.
            admin.sendMessage(TextStrings.colorize(TextStrings.GROUP_CREATED_SUCCESSFULLY));
        }
        return true;
    }

    public boolean create_lobby(Player admin, String[] strings) {
        Lobby lobby = new Lobby(admin.getLocation());
        admin.sendMessage(TextStrings.colorize(TextStrings.LOBBY_CREATED_SUCCESSFULLY));
        // TODO: check
        //? NOTE: by default lobby add to default group.
        return true;
    }

    public boolean change_group_of(Player admin, String[] strings) {
        // /lobby-manager change_group_of <lobby_name> <group_name>
        if (strings.length < 3) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }

        String lobby_name = strings[2];
        String group_name = strings[3];

        LobbyManager.getInstance().change_group_of(lobby_name, group_name, admin);
        return true;
    }

    public boolean help(Player admin, String[] strings) {
        for (String massages : TextStrings.HELP) {
            admin.sendMessage(TextStrings.colorize(massages));
        }
        return true;
    }

    public boolean save(Player admin, String[] strings) {
        // TODO: save data.

        return true;
    }

    public boolean info(Player admin, String[] strings) {
        // TODO: print all information about , lobby group and lobbies. in chat. this is maybe hugh. but it's fine.
        //? lobby groups. and there lobbies. and also next location types. and also there names.

        return true;
    }

    public boolean change_lobby_spawn_rotation(Player admin, String[] strings) {
        // /lobby-manager change_lobby_spawn_rotation default
        if (strings.length < 2) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        String type = strings[2];
        try {
            NextLocationTypes nextLocationTypes = NextLocationTypes.valueOf(type);
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(admin.getWorld().getName());
            lobby.setLocationTypes(nextLocationTypes);
            admin.sendMessage(TextStrings.colorize(TextStrings.SUCCESSFUL));
        } catch (IllegalArgumentException e) {
            // ignore.
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
        }
        return true;
    }
}
