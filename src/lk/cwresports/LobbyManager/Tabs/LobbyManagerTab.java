package lk.cwresports.LobbyManager.Tabs;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Commands.LobbyManagerCommand;
import lk.cwresports.LobbyManager.Utils.TimeZoneHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class LobbyManagerTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player admin)) {
            return List.of();
        }


        if (strings.length > 1) {
            if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_create_group)) {
                String[] name = {admin.getWorld().getName() + "_group"};
                return List.of(name);


            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_change_lobby_spawn_rotation)) {
                String[] type = {"DEFAULT", "RANDOM", "CIRCULAR"};
                return List.of(type);


            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_change_group_of)) {
                if (strings.length > 2) {
                    return LobbyManager.getInstance().getGroups();
                }
                return LobbyManager.getInstance().getGroupedLobbies();


            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_remove_spawn_location_by_index)) {
                String[] index = {"1", "2", "3", "-1"};
                return List.of(index);


            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_set_period)) {
                // /lobby-manager set_period <MM-DD-HH-mm-ss> <days> <timezone>
                //                 0             1             2       3
                if (strings.length == 2) {
                    return List.of("<MM-DD-HH-mm-ss>");
                }

                if (strings.length == 3) {
                    return List.of("1", "2", "4", "5", "6", "7");
                }

                if (strings.length == 4) {
                    return TimeZoneHelper.getZones();
                }

                return List.of();
            }
        }

        return List.of(LobbyManagerCommand.subs);
    }
}
