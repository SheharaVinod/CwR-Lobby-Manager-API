/*
    CwR Lobby Manager API - Minecraft plugin for managing multiple spawn lobbies
    Copyright (C) 2025 SheharaVinod(AKN Mr_Unknown), Team CwR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package lk.cwresports.LobbyManager.Utils;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.ChatColor;

public class TextStrings {
    public static String YOU_ARE_NOT_AN_ADMIN = "&4You are not an admin.!";
    public static String YOU_ARE_NOW_IN_ADMIN_MOD = "&6You are now in admin mod.!";

    public static String ONLY_PLAYERS_CAN_EXECUTE_THIS_COMMAND = "&6Only players can execute this command.";

    public static final String EVENT_LOBBY_INFO_HEADER = "&6&lEvent Lobbies Information:";
    public static final String NO_EVENT_LOBBIES = "&cNo event lobbies found!";
    public static final String EVENT_NOT_SET = "&cEvent period not set";
    public static final String INVALID_DATE_FORMAT = "&cInvalid date format";

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

    public static final String[] HELP = {
            "&6&l=== Lobby Manager Commands ===",
            "&7Group Management:",
            "&6/lobby-manager create_group <name> &7- Create a new lobby group",
            "&6/lobby-manager delete_group <name> &7- Delete a lobby group",
            "&6/lobby-manager change_group_of <lobby> <group> &7- Move lobby to another group",
            "",
            "&7Lobby Creation:",
            "&6/lobby-manager create_lobby &7- Create regular lobby at your location",
            "&6/lobby-manager create_event_lobby &7- Create event lobby at your location",
            "&6/lobby-manager delete_lobby &7- Delete current lobby",
            "",
            "&7Spawn Points:",
            "&6/lobby-manager add_a_new_spawn &7- Add spawn point at your location",
            "&6/lobby-manager set_default_spawn &7- Set current location as default spawn",
            "&6/lobby-manager remove_spawn_location_by_index <index> &7- Remove spawn point",
            "&6/lobby-manager change_lobby_spawn_rotation <DEFAULT|RANDOM|CIRCULAR> &7- Set spawn rotation",
            "&6/lobby-manager set_spawn_cool_down <seconds> &7- Set spawn command cooldown",
            "",
            "&7Group Rotation:",
            "&6/lobby-manager change_lobby_rotation <RANDOM|CIRCULAR> <group> &7- Set rotation type",
            "&6/lobby-manager set_group_lobby_rotation_time <group> <MINUTE|HOUR|DAY|WEEK|MONTH|MANUAL> &7- Set rotation schedule",
            "&6/lobby-manager rotate_every_lobby_group &7- Force rotate all groups now",
            "",
            "&7Event Management:",
            "&6/lobby-manager set_period <MM-DD-HH-mm-ss|DD-HH-mm-ss> <days|DD-HH-mm-ss> &7- Set event period",
            "&6/lobby-manager info_of_all_event_lobbies &7- List all event lobbies",
            "",
            "&7Lobby Settings:",
            "&6/lobby-manager disabled_hunger <true|false> &7- Toggle hunger in lobby",
            "&6/lobby-manager disabled_damage <true|false> &7- Toggle damage in lobby",
            "&6/lobby-manager set_game_mod <GAMEMODE> &7- Set lobby game mode",
            "&6/lobby-manager cansel_player_interaction <true|false> &7- Toggle block interactions",
            "",
            "&7Information:",
            "&6/lobby-manager info &7- Show current lobby info",
            "&6/lobby-manager info_of_all_groups &7- List all groups and lobbies",
            "&6/lobby-manager help &7- Show this help",
            "",
            "&7System:",
            "&6/lobby-manager admin &7- Toggle admin mode",
            "&6/lobby-manager save &7- Save all lobby data",
            "",
            "&6&l================================="
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
