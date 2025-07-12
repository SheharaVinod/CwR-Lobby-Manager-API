package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.*;
import lk.cwresports.LobbyManager.ConfigAndData.LobbyDataManager;
import lk.cwresports.LobbyManager.Utils.PermissionNodes;
import lk.cwresports.LobbyManager.Utils.RotationCalculator;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.GameMode;
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

    public static final String sub_disabled_hunger = "disabled_hunger";
    public static final String sub_disabled_damage = "disabled_damage";
    public static final String sub_set_game_mod = "set_game_mod";
    public static final String sub_cansel_player_interaction = "cansel_player_interaction";

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
    public static final String sub_save = "save";
    public static final String sub_info = "info";

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
            sub_save,
            sub_change_lobby_rotation,
            sub_set_group_lobby_rotation_time,
            sub_rotate_every_lobby_group,
            sub_info_of_all_event_lobbies,
            sub_info_of_all_groups,
            sub_disabled_hunger,
            sub_disabled_damage,
            sub_set_game_mod,
            sub_cansel_player_interaction,
            sub_info
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
            } else if (strings[0].equalsIgnoreCase(sub_disabled_hunger)) {
                return disabled_hunger(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_disabled_damage)) {
                return disabled_damage(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_set_game_mod)) {
                return set_game_mod(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_cansel_player_interaction)) {
                return cansel_player_interaction(admin, strings);
            } else if (strings[0].equalsIgnoreCase(sub_info)) {
                return info(admin, strings);
            }
        }

        return true;
    }

    public boolean info(Player admin, String[] strings) {
        // Check if player is in a lobby
        if (!LobbyManager.getInstance().isInALobby(admin)) {
            admin.sendMessage(TextStrings.colorize("§cThis is not a lobby."));
            return true;
        }

        String worldName = admin.getWorld().getName();
        Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);

        if (lobby == null) {
            admin.sendMessage(TextStrings.colorize("§cThis is not a lobby."));
            return true;
        }

        // Display lobby information
        admin.sendMessage(TextStrings.colorize("§6§l=== Lobby Information ==="));
        admin.sendMessage(TextStrings.colorize("§eLobby Name: §f" + worldName));

        // Lobby type
        String lobbyType = lobby.isEventLobby() ? "Event Lobby" : "Group Lobby";
        admin.sendMessage(TextStrings.colorize("§eLobby Type: §f" + lobbyType));

        // Game mode
        admin.sendMessage(TextStrings.colorize("§eGame Mode: §f" + lobby.getGameMode().name()));

        // Disabled features
        admin.sendMessage(TextStrings.colorize("§eDisabled Hunger: §f" + lobby.isDisabledHunger()));
        admin.sendMessage(TextStrings.colorize("§eDisabled Damage: §f" + lobby.isDisabledDamage()));
        admin.sendMessage(TextStrings.colorize("§eCancel Player Interactions: §f" + lobby.isCanselInteraction()));

        // Location settings
        admin.sendMessage(TextStrings.colorize("§eSpawn Rotation Type: §f" + lobby.getLocationTypes().name()));
        admin.sendMessage(TextStrings.colorize("§eTotal Spawn Locations: §f" + lobby.getSpawnLocations().size()));

        // Default spawn location
        Location defaultSpawn = lobby.getDefaultSpawnLocation();
        if (defaultSpawn != null) {
            admin.sendMessage(TextStrings.colorize("§eDefault Spawn: §f" +
                    String.format("X: %.1f, Y: %.1f, Z: %.1f",
                            defaultSpawn.getX(), defaultSpawn.getY(), defaultSpawn.getZ())));
        }

        // Event lobby specific information
        if (lobby.isEventLobby() && lobby instanceof EventLobbies) {
            EventLobbies eventLobby = (EventLobbies) lobby;
            admin.sendMessage(TextStrings.colorize("§6§l--- Event Lobby Details ---"));

            String eventDate = eventLobby.getEventDate();
            String expirePeriod = eventLobby.getExpireDays();

            if (eventDate != null && expirePeriod != null) {
                admin.sendMessage(TextStrings.colorize("§eEvent Date: §f" + eventDate));
                admin.sendMessage(TextStrings.colorize("§eExpire Period: §f" + expirePeriod));
                admin.sendMessage(TextStrings.colorize("§eEvent Active: §f" + eventLobby.isEvent()));
            } else {
                admin.sendMessage(TextStrings.colorize("§cEvent period not configured"));
            }
        } else {
            // Group lobby specific information
            LobbyGroup currentGroup = LobbyManager.getInstance().getCurrentGroupOf(lobby);
            if (currentGroup != null) {
                admin.sendMessage(TextStrings.colorize("§6§l--- Group Lobby Details ---"));
                admin.sendMessage(TextStrings.colorize("§eGroup Name: §f" + currentGroup.getName()));
                admin.sendMessage(TextStrings.colorize("§eGroup Rotation Type: §f" + currentGroup.getLobbyRotationType()));
                admin.sendMessage(TextStrings.colorize("§eGroup Rotation Schedule: §f" + currentGroup.getLobbyRotationTimeUnit().name()));
                admin.sendMessage(TextStrings.colorize("§eTotal Lobbies in Group: §f" + currentGroup.getLobbies().size()));

                boolean isCurrentLobby = (currentGroup.getCurrentLobby() == lobby);
                admin.sendMessage(TextStrings.colorize("§eIs Current Active Lobby: §f" + isCurrentLobby));
            }
        }

        admin.sendMessage(TextStrings.colorize("§6§l========================"));
        return true;
    }


    public boolean disabled_hunger(Player admin, String[] strings) {
        // /lobby-manager disabled_hunger true
        if (strings.length < 2) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        try {
            boolean value = Boolean.parseBoolean(strings[0]);
            if (!LobbyManager.getInstance().isInALobby(admin)) {
                admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
                return true;
            }
            String worldName = admin.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);

            lobby.setDisabledHunger(value);

        } catch (Exception e) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }

        return true;
    }

    public boolean cansel_player_interaction(Player admin, String[] strings) {
        // /lobby-manager cansel_player_interaction true
        if (strings.length < 2) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        try {
            boolean value = Boolean.parseBoolean(strings[0]);
            if (!LobbyManager.getInstance().isInALobby(admin)) {
                admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
                return true;
            }

            String worldName = admin.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);

            lobby.setCanselInteraction(value);
        } catch (Exception e) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        return true;
    }

    public boolean set_game_mod(Player admin, String[] strings) {
        // /lobby-manager set_game_mod ADVENTURE
        if (strings.length < 2) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        try {
            GameMode gameMode = GameMode.valueOf(strings[1]);
            if (!LobbyManager.getInstance().isInALobby(admin)) {
                admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
                return true;
            }

            String worldName = admin.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);

            lobby.setGameMode(gameMode);
        } catch (Exception e) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        return true;
    }

    public boolean disabled_damage(Player admin, String[] strings) {
        // /lobby-manager disabled_damage true
        if (strings.length < 2) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
        }
        try {
            boolean value = Boolean.parseBoolean(strings[0]);
            if (!LobbyManager.getInstance().isInALobby(admin)) {
                admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
                return true;
            }
            String worldName = admin.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);

            lobby.setDisabledDamage(value);
        } catch (Exception e) {
            admin.sendMessage(TextStrings.colorize(TextStrings.SOMETHING_WENT_WRONG));
            return true;
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
            String expirePeriod = eventLobby.getExpireDays();

            if (eventDate == null || expirePeriod == null) {
                admin.sendMessage("§e" + worldName + "§7: §cEvent period not set");
                continue;
            }

            try {
                // Parse and format the date
                String[] dateParts = eventDate.split("-");
                String formattedDate;

                if (dateParts.length == 5) { // MM-DD-HH-mm-ss
                    formattedDate = String.format("%s/%s %s:%s",
                            dateParts[0], dateParts[1], dateParts[2], dateParts[3]);
                } else { // DD-HH-mm-ss
                    formattedDate = String.format("%s %s:%s",
                            dateParts[0], dateParts[1], dateParts[2]);
                }

                // Parse and format the expiration
                String[] expireParts = expirePeriod.split("-");
                String formattedExpire;

                if (expireParts.length == 1) { // Days only
                    formattedExpire = expireParts[0] + " days";
                } else { // DD-HH-mm-ss
                    formattedExpire = String.format("%sd %sh %sm",
                            expireParts[0], expireParts[1], expireParts[2]);
                    if (expireParts.length > 3) {
                        formattedExpire += " " + expireParts[3] + "s";
                    }
                }

                admin.sendMessage("§e" + worldName + "§7 - Starts: §a" + formattedDate +
                        "§7 - Duration: §a" + formattedExpire);

            } catch (Exception e) {
                admin.sendMessage("§e" + worldName + "§7: §cInvalid date format");
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
            admin.sendMessage("§eAvailable time units: MINUTE, HOUR, DAY, WEEK, MONTH, MANUAL");
            return true;
        }

        String groupName = strings[1];
        String timeUnitStr = strings[2].toUpperCase();

        try {
            TimeUnits unit = TimeUnits.valueOf(timeUnitStr);
            LobbyGroup group = LobbyManager.getInstance().getLobbyGroup(groupName);

            if (group == null) {
                admin.sendMessage("§cGroup not found!");
                return true;
            }

            group.setLobbyRotationTimeUnit(unit);
            if (unit != TimeUnits.MANUAL) {
                long nextRotation = RotationCalculator.calculateNextRotation(unit);
                if (nextRotation != -1) {
                    group.setNextRotationTime(nextRotation);
                } else {
                    admin.sendMessage("§cFailed to calculate next rotation time!");
                    return true;
                }
            }
            admin.sendMessage("§aRotation time unit set to " + timeUnitStr);
            return true;
        } catch (IllegalArgumentException e) {
            admin.sendMessage("§cInvalid time unit! Available: MINUTE, HOUR, DAY, WEEK, MONTH, MANUAL");
            return true;
        } catch (Exception e) {
            admin.sendMessage("§cAn error occurred: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
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
            admin.sendMessage("§cUsage: /lobby-manager set_period <MM-DD-HH-mm-ss|DD-HH-mm-ss> <days|DD-HH-mm-ss>");
            admin.sendMessage("§eExamples:");
            admin.sendMessage("§7- /lobby-manager set_period 12-25-15-00-00 7 (Dec 25th 3PM, 7 days)");
            admin.sendMessage("§7- /lobby-manager set_period 25-15-00-00 2-12-30 (25th 3PM, 2 days 12h 30m)");
            admin.sendMessage("§7- /lobby-manager set_period 07-12-09-49-00 00-12-00 (July 12th 9:49AM, 12 hours)");
            return true;
        }

        try {
            String date = strings[1];
            StringBuilder expirePeriod = new StringBuilder(strings[2]);

            // Combine additional expiration parts if provided
            for (int i = 3; i < strings.length; i++) {
                expirePeriod.append("-").append(strings[i]);
            }

            // Validate lobby type
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(admin.getWorld().getName());
            if (!(lobby instanceof EventLobbies)) {
                admin.sendMessage("§cYou must be in an event lobby!");
                return true;
            }

            ((EventLobbies) lobby).setPeriod(date, expirePeriod.toString());
            admin.sendMessage("§aEvent period set successfully!");
            admin.sendMessage("§7Start: §e" + date);
            admin.sendMessage("§7Duration: §e" + expirePeriod);
        } catch (IllegalArgumentException e) {
            admin.sendMessage("§cError: " + e.getMessage());
            admin.sendMessage("§cValid formats:");
            admin.sendMessage("§7Date: §eMM-DD-HH-mm-ss §7or §eDD-HH-mm-ss");
            admin.sendMessage("§7Duration: §edays §7or §eDD-HH-mm-ss");
        } catch (Exception e) {
            admin.sendMessage("§cAn unexpected error occurred");
            e.printStackTrace();
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
