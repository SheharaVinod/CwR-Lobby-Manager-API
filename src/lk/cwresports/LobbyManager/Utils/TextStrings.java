package lk.cwresports.LobbyManager.Utils;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.ChatColor;

public class TextStrings {
    public static String YOU_ARE_NOT_AN_ADMIN = "&4You are not an admin.!";
    public static String YOU_ARE_NOW_IN_ADMIN_MOD = "&6You are now in admin mod.!";

    public static String ONLY_PLAYERS_CAN_EXECUTE_THIS_COMMAND = "&6Only players can execute this command.";

    public static String YOU_CANT_CREATE_DEFAULT_GROUP = "&4You are not able to create a new default group.!";
    public static String YOU_CANT_CREATE_BLANK_GROUP = "&4You are not able to create a anonymous group.!";
    public static String GROUP_CREATED_SUCCESSFULLY = "&6Group created successfully.";
    public static String LOBBY_CREATED_SUCCESSFULLY = "&6Lobby created successfully.";
    public static String ALREADY_A_LOBBY = "&6This is already a lobby.";
    public static String EVENT_LOBBY_CREATED_SUCCESSFULLY = "&6Event lobby created successfully.";
    public static String SOMETHING_WENT_WRONG = "&6Something went wrong. check again.";
    public static String CHANGE_GROUP_SUCCESSFULLY = "&6Something went wrong. check again.";
    public static String SUCCESSFUL = "&6Success.!";


    public static String YOU_ARE_NO_LONGER_IN_ADMIN_MOD = "&6You are no longer in admin mod.!";
    public static String WARN_ADMIN_MOD = "&4WARN: &6this command is not a toy, toggle this if you really know what you doing.!";

    public static String YOU_ARE_NOT_IN_ADMIN_MOD = "&4You are not in admin mod to use this command.!";

    public static String YOU_DONT_HAVE_PERMISSION = "&4You don't have permission.!";
				
    public static String[] HELP = 	{
            "&bYou can create lobby group using",
            "",
            "&6/lobby-manager create_group <name>",
            "&6/lobby-manager delete_group name",
            "&6/lobby-manager create_lobby",
            "&6/lobby-manager delete_lobby",
            "&6/lobby-manager save",
            "&6/lobby-manager create_event_lobby",
            "&6/lobby-manager set_period <MM-DD-HH-mm-ss> <days> <timezone>",
            "&6/lobby-manager change_lobby_spawn_rotation default",
            "&6/lobby-manager change_group_of <lobby_name> <group_name>",
            "&6/lobby-manager change_group_of <lobby_name> <group_name>",
            ""
    };
				
    public static String colorize(String massage) {
        return colorize(massage, true);
    }

    public static String colorize(String massage, boolean prefix) {
        if (prefix) {
            return ChatColor.translateAlternateColorCodes('&', CwRLobbyAPI.PREFIX) + ChatColor.translateAlternateColorCodes('&', massage);
        }
        return ChatColor.translateAlternateColorCodes('&', massage);
    }
}
