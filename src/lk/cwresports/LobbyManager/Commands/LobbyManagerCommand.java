package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.*;
import lk.cwresports.LobbyManager.ConfigAndData.LobbyDataManager;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import lk.cwresports.LobbyManager.Utils.RotationCalculator;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
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

    public static final String sub_change_lobby_rotation = "change_lobby_rotation_type";
    public static final String sub_set_group_lobby_rotation_time = "set_group_lobby_rotation_time";
    public static final String sub_rotate_every_lobby_group = "rotate_every_lobby_group";
    public static final String sub_info_of_all_groups = "info_of_all_groups";
    public static final String sub_info_of_all_event_lobbies = "info_of_all_event_lobbies";

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
            sub_save,
            sub_change_lobby_rotation,
            sub_set_group_lobby_rotation_time,
            sub_rotate_every_lobby_group,
            sub_info_of_all_event_lobbies,
            sub_info_of_all_groups
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
            } else if (strings[0].equalsIgnoreCase(sub_change_lobby_rotation)) {
                return change_lobby_rotation(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_set_group_lobby_rotation_time)) {
                return set_group_lobby_rotation_time(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_rotate_every_lobby_group)) {
                return rotate_every_lobby_group(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_info_of_all_groups)) {
                return info_of_all_groups(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_info_of_all_event_lobbies)) {
                return info_of_all_event_lobbies(admin, strings);
            }
        }

        return true;
    }

    public boolean info_of_all_event_lobbies(Player admin, String[] strings) {
        List<EventLobbies> eventLobbies = LobbyManager.getInstance().getEventLobbies();

        if (eventLobbies.isEmpty()) {
            admin.sendMessage("§cNo event lobbies found!");
            return true;
        }

        admin.sendMessage("§6§lEvent Lobbies Information:");
        admin.sendMessage("§7----------------------------");

        for (EventLobbies eventLobby : eventLobbies) {
            String worldName = eventLobby.getWorld().getName();
            String eventDate = eventLobby.getEventDate();
            int expireDays = eventLobby.getExpireDays();

            if (eventDate == null) {
                admin.sendMessage("§e" + worldName + "§7: §cEvent period not set");
            } else {
                String[] parts = eventDate.split("-");
                if (parts.length >= 2) {
                    String month = parts[0];
                    String day = parts[1];
                    String hour = parts.length > 2 ? parts[2] : "00";
                    String minute = parts.length > 3 ? parts[3] : "00";
                    admin.sendMessage("§e" + worldName + "§7 - " + month + "/" + day + " " + hour + ":" + minute + " - " + expireDays + " days");
                } else {
                    admin.sendMessage("§e" + worldName + "§7: §cInvalid date format");
                }
            }
        }

        admin.sendMessage("§7----------------------------");
        return true;
    }

    public boolean info_of_all_groups(Player admin, String[] strings) {
        LobbyManager lobbyManager = LobbyManager.getInstance();

        if (lobbyManager.lobbyGroupMap.isEmpty()) {
            admin.sendMessage("§cNo groups found!");
            return true;
        }

        admin.sendMessage("§6§lLobby Groups Information:");
        admin.sendMessage("§7----------------------------");

        for (LobbyGroup group : lobbyManager.lobbyGroupMap.values()) {
            StringBuilder info = new StringBuilder();
            info.append("§e").append(group.getName()).append("§7: ");

            if (group.getLobbies().isEmpty()) {
                info.append("§cNo lobbies");
            } else {
                for (Lobby lobby : group.getLobbies()) {
                    String currentIndicator = (lobby == group.getCurrentLobby()) ? "§a" : "";
                    info.append(currentIndicator).append(lobby.getWorld().getName()).append("§7, ");
                }
                // Remove last comma
                info.setLength(info.length() - 2);
            }

            // Add rotation info
            info.append("\n§7Rotation: §e").append(group.getLobbyRotationType())
                    .append(" §7| Schedule: §e").append(group.getLobbyRotationTimeUnit());

            admin.sendMessage(info.toString());
            admin.sendMessage("§7----------------------------");
        }

        return true;
    }

    private boolean change_lobby_rotation(Player admin, String[] strings) {
        if (strings.length < 3) {
            admin.sendMessage("§cUsage: /lobby-manager change_lobby_rotation_type <RANDOM|CIRCULAR> <group>");
            return true;
        }

        String type = strings[1].toUpperCase();
        String groupName = strings[2];

        try {
            LobbyRotationTypes rotationType = LobbyRotationTypes.valueOf(type);
            LobbyGroup group = LobbyManager.getInstance().getLobbyGroup(groupName);

            if (group == null) {
                admin.sendMessage("§cGroup not found!");
                return true;
            }

            if (group.setLobbyRotationType(rotationType.name())) {
                admin.sendMessage(TextStrings.colorize(TextStrings.SUCCESSFUL));
                admin.sendMessage("§aRotation type set to " + type);
            } else {
                admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            }
        } catch (IllegalArgumentException e) {
            admin.sendMessage("§cInvalid rotation type! Use RANDOM or CIRCULAR");
        }
        return true;
    }

    private boolean set_group_lobby_rotation_time(Player admin, String[] strings) {
        if (strings.length < 3) {
            admin.sendMessage("§cUsage: /lobby-manager set_group_lobby_rotation_time <group> <TimeUnit>");
            return true;
        }

        String groupName = strings[1];
        String timeUnit = strings[2].toUpperCase();

        try {
            TimeUnits unit = TimeUnits.valueOf(timeUnit);
            LobbyGroup group = LobbyManager.getInstance().getLobbyGroup(groupName);

            if (group == null) {
                admin.sendMessage("§cGroup not found!");
                return true;
            }

            group.setLobbyRotationTimeUnit(unit);
            if (unit != TimeUnits.MANUAL) {
                group.setNextRotationTime(RotationCalculator.calculateNextRotation(unit));
            }
            admin.sendMessage("§aRotation time unit set to " + timeUnit);
        } catch (IllegalArgumentException e) {
            admin.sendMessage("§cInvalid time unit! Use MINUTE, HOUR, DAY, WEEK, MONTH, or MANUAL");
        }
        return true;
    }

    private boolean rotate_every_lobby_group(Player admin, String[] strings) {
        for (LobbyGroup group : LobbyManager.getInstance().lobbyGroupMap.values()) {
            if (group.getLobbies().isEmpty()) {
                continue;
            }

            // Remove manual check to force rotation
            group.changeCurrentLobby();
            if (group.getLobbyRotationTimeUnit() != TimeUnits.MANUAL) {
                group.setNextRotationTime(
                        RotationCalculator.calculateNextRotation(
                                group.getLobbyRotationTimeUnit()
                        )
                );
            }
        }
        admin.sendMessage("§aAll groups rotated!");
        return true;
    }

    public boolean set_period(Player admin, String[] strings) {
        if (strings.length < 3) {
            admin.sendMessage("§cUsage: /lobby-manager set_period <MM-DD-HH-mm-ss> <days>");
            return true;
        }

        try {
            String date = strings[1];
            int expireDays = Integer.parseInt(strings[2].replace("d", ""));

            // Validate lobby type
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(admin.getWorld().getName());
            if (!(lobby instanceof EventLobbies)) {
                admin.sendMessage("§cYou must be in an event lobby!");
                return true;
            }

            ((EventLobbies) lobby).setPeriod(date, expireDays);
            admin.sendMessage("§aEvent period set successfully!");
        } catch (IllegalArgumentException e) {
            admin.sendMessage("§cInvalid arguments! Format: MM-DD-HH-mm-ss days");
            admin.sendMessage("§cError: " + e.getMessage());
        } catch (Exception e) {
            admin.sendMessage("§cAn error occurred while setting the event period");
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

        String lobby_name = strings[1];  // Changed from strings[2]
        String group_name = strings[2];  // Changed from strings[3]

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
