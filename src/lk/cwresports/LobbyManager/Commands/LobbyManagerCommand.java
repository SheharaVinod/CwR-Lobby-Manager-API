package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.*;
import lk.cwresports.LobbyManager.ConfigAndData.LobbyDataManager;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class LobbyManagerCommand implements CommandExecutor {
    public static final String NAME = "lobby-manager";

    public static final String sub_create_group = "create_group";
    public static final String sub_delete_group = "delete_group";
    public static final String sub_create_lobby = "create_lobby";
    public static final String sub_create_event_lobby = "create_event_lobby";
    public static final String sub_delete_lobby = "delete_lobby";

    public static final String sub_add_a_new_spawn = "add_a_new_spawn";
    public static final String sub_set_default_spawn = "set_default_spawn";
    public static final String sub_remove_spawn_location_by_index = "remove_spawn_location_by_index";

    public static final String sub_set_spawn_cool_down = "set_spawn_cool_down";
    public static final String sub_change_lobby_rotation = "change_lobby_rotation_type"; // TODO:

    public static final String sub_change_lobby_spawn_rotation = "change_spawn_rotation_type";
    public static final String sub_change_group_of = "change_group_of";
    public static final String sub_admin = "admin";
    public static final String sub_set_period = "set_period";
    public static final String sub_help = "help";
    public static final String sub_info = "info";
    public static final String sub_save = "save";

    public static final String[] subs = {
            sub_create_group,
            sub_delete_group,
            sub_create_lobby,
            sub_create_event_lobby,
            sub_delete_lobby,
            sub_remove_spawn_location_by_index,
            sub_add_a_new_spawn,
            sub_set_spawn_cool_down,
            sub_set_default_spawn,
            sub_change_lobby_spawn_rotation,
            sub_change_group_of,
            sub_admin,
            sub_set_period,
            sub_help,
            sub_info,
            sub_save
    };

    Plugin plugin;

    public LobbyManagerCommand(Plugin plugin) {
        this.plugin = plugin;
    }

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
            } else if (strings[0].equalsIgnoreCase(sub_create_event_lobby)) {
                return create_event_lobby(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_create_group)) {
                return create_group(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_change_group_of)) {
                return change_group_of(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_change_lobby_spawn_rotation)) {
                return change_lobby_spawn_rotation(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_set_period)) {
                return set_period(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_delete_group)) {
                return delete_group(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_delete_lobby)) {
                return delete_lobby(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_add_a_new_spawn)) {
                return add_a_new_spawn(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_set_default_spawn)) {
                return set_default_spawn(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_remove_spawn_location_by_index)) {
                return remove_spawn_location_by_index(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_set_spawn_cool_down)) {
                return set_spawn_cool_down(admin, strings);
            }
        }

        return true;
    }

    public boolean set_period(Player admin, String[] strings) {
        if (strings.length < 4) {
            admin.sendMessage("§cUsage: /lobby-manager set_period <MM-DD-HH-mm-ss> <days> <timezone>");
            return true;
        }

        try {
            String date = strings[1];
            int expireDays = Integer.parseInt(strings[2].replace("d", ""));
            String timezone = strings[3];

            // Validate lobby type
            //? wrong...
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(admin.getWorld().getName());
            if (!(lobby instanceof EventLobbies)) {
                admin.sendMessage("§cYou must be in an event lobby!");
                return true;
            }

            ((EventLobbies) lobby).setPeriod(date, expireDays, timezone);
            admin.sendMessage("§aEvent period set successfully!");
        } catch (Exception e) {
            admin.sendMessage("§cInvalid arguments! Format: MM-DD-HH-mm-ss days timezone");
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

    public boolean delete_lobby(Player admin, String[] strings) {
        //lobby-manager delete_lobby
        String name = admin.getWorld().getName();
        Lobby lobby = LobbyManager.getInstance().getLobbyByName(name);
        if (lobby == null) {
            admin.sendMessage(TextStrings.colorize("This world is not a lobby to delete"));
            return true;
        }
        LobbyManager.getInstance().deleteLobby(name);
        return true;
    }

    public boolean delete_group(Player admin, String[] strings) {
        //lobby-manager delete_group name
        if (strings.length < 2) return false;
        boolean isDeleted = LobbyManager.getInstance().unregisterLobbyGroup(strings[1]);
        admin.sendMessage(TextStrings.colorize("is group deleted : " + isDeleted));
        return true;
    }

    public boolean add_a_new_spawn(Player admin, String[] strings) {
        //lobby-manager add_a_new_spawn
        String worldName = admin.getWorld().getName();
        Location location = admin.getLocation();
        Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);
        if (lobby == null) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        lobby.addSpawnLocation(location);
        admin.sendMessage(TextStrings.colorize("add a new spawn location"));
        return true;
    }

    public boolean set_default_spawn(Player admin, String[] strings) {
        //lobby-manager set_default_spawn
        String worldName = admin.getWorld().getName();
        Location location = admin.getLocation();
        Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);
        if (lobby == null) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        lobby.setDefaultSpawnLocation(location);
        admin.sendMessage(TextStrings.colorize("set default spawn location"));
        return true;
    }

    public boolean remove_spawn_location_by_index(Player admin, String[] strings) {
        //lobby-manager remove_spawn_location_by_id [1,2,3]
        if (strings.length < 2) {
            return false;
        }

        String worldName = admin.getWorld().getName();
        LobbyManager lobbyManager = LobbyManager.getInstance();
        Lobby lobby = lobbyManager.getLobbyByName(worldName);
        if (lobby == null) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        try {
            int index = Integer.parseInt(strings[1]);
            if (index < 0) {
                lobby.removeSpawnLocation(lobby.getSpawnLocations().size() + index);
            } else {
                lobby.removeSpawnLocation(index);
            }
        } catch (Exception e) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        admin.sendMessage(TextStrings.colorize("removed.!"));
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
        if (alreadyLobby(admin)) {
            return true;
        }

        Lobby lobby = new GroupLobbies(admin.getLocation());
        admin.sendMessage(TextStrings.colorize(TextStrings.LOBBY_CREATED_SUCCESSFULLY));
        // TODO: check
        //? NOTE: by default lobby add to default group.
        return true;
    }

    private boolean alreadyLobby(Player admin) {
        String name = admin.getWorld().getName();
        Lobby existingLobby = LobbyManager.getInstance().getLobbyByName(name);
        if (existingLobby != null) {
            admin.sendMessage(TextStrings.colorize(TextStrings.ALREADY_A_LOBBY));
            return true;
        }
        return false;
    }

    public boolean create_event_lobby(Player admin, String[] strings) {
        if (alreadyLobby(admin)) {
            return true;
        }

        Lobby lobby = new EventLobbies(admin.getLocation());
        admin.sendMessage(TextStrings.colorize(TextStrings.EVENT_LOBBY_CREATED_SUCCESSFULLY));
        // TODO: check
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
        admin.sendMessage(TextStrings.colorize(TextStrings.SUCCESSFUL));
        return true;
    }

    public boolean help(Player admin, String[] strings) {
        for (String massages : TextStrings.HELP) {
            admin.sendMessage(TextStrings.colorize(massages, false));
        }
        return true;
    }

    public boolean save(Player admin, String[] strings) {
        new LobbyDataManager((JavaPlugin) this.plugin).saveData();
        admin.sendMessage(TextStrings.colorize("Saved.!"));
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
        try {
        String type = strings[1];
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


    public boolean set_spawn_cool_down(Player admin, String[] strings) {
        // /lobby-manager set_spawn_cool_down <int>
        if (strings.length < 2) return false;
        String string = strings[1];
        try {
            SpawnCommand.set_cool_down(Integer.parseInt(string));
            admin.sendMessage(TextStrings.colorize(TextStrings.SUCCESSFUL));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
