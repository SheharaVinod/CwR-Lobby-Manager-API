package lk.cwresports.LobbyManager.Utils;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.ChatColor;

public class TextStrings {
    public static String YOU_ARE_NOT_AN_ADMIN = "&4You are not an admin.!";
    public static String WARN_ADMIN_MOD = "&4WARN: &6this command is not a toy, toggle this if you really know what you doing.!";

    public static String YOU_ARE_NOT_IN_ADMIN_MOD = "&4You are not in admin mod to use this command.!";

    public static String YOU_DONT_HAVE_PERMISSION = "&4You don't have permission.!";

    public static String colorize(String massage) {
        return colorize(massage, true);
    }

    public static String colorize(String massage, boolean prefix) {
        if (prefix) {
            return colorize(CwRLobbyAPI.PREFIX) + colorize(massage);
        }
        return ChatColor.translateAlternateColorCodes('&', massage);
    }
}
