package lk.cwresports.LobbyManager.Tabs;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.API.TimeUnits;
import lk.cwresports.LobbyManager.Commands.LobbyManagerCommand;
import lk.cwresports.LobbyManager.Utils.TabSorter;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyManagerTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player admin)) {
            return List.of();
        }

        if (strings.length == 1) {
            // First argument: list all subcommands
            return TabSorter.sort(Arrays.asList(LobbyManagerCommand.subs), strings[0]);
        }

        if (strings.length > 1) {
            if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_create_group)) {
                String[] name = {admin.getWorld().getName() + "_group"};
                return List.of(name);

            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_change_lobby_spawn_rotation)) {
                if (strings.length == 2) {
                    String[] type = {"DEFAULT", "RANDOM", "CIRCULAR"};
                    return Arrays.asList(type);
                }

            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_change_group_of)) {
                if (strings.length == 2) {
                    return LobbyManager.getInstance().getGroupedLobbies();
                } else if (strings.length == 3) {
                    return TabSorter.sort(LobbyManager.getInstance().getGroups(), strings[1], true);
                }

            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_remove_spawn_location_by_index)) {
                if (strings.length == 2) {
                    String[] index = {"1", "2", "3", "-1"};
                    return Arrays.asList(index);
                }

            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_set_period)) {
                if (strings.length == 2) {
                    return List.of("<MM-DD-HH-mm-ss>");
                } else if (strings.length == 3) {
                    return List.of("<DD-HH-mm-ss>");
                }

            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_disabled_hunger)) {
                if (strings.length == 2) {
                    return List.of("true", "false");
                }
            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_disabled_damage)) {
                if (strings.length == 2) {
                    return List.of("true", "false");
                }
            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_cansel_player_interaction)) {
                if (strings.length == 2) {
                    return List.of("true", "false");
                }
            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_set_game_mod)) {
                if (strings.length == 2) {
                    List<String> gameMods = new ArrayList<>();
                    for (GameMode value : GameMode.values()) {
                        gameMods.add(value.name());
                    }
                    return gameMods;
                }
            }
            // Add tab completion for new commands
            else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_change_lobby_rotation)) {
                if (strings.length == 2) {
                    // Suggest rotation types
                    return Arrays.asList("RANDOM", "CIRCULAR");
                } else if (strings.length == 3) {
                    // Suggest group names
                    return TabSorter.sort(LobbyManager.getInstance().getGroups(), strings[1], true);
                }

            } else if (strings[0].equalsIgnoreCase(LobbyManagerCommand.sub_set_group_lobby_rotation_time)) {
                if (strings.length == 2) {
                    // Suggest group names
                    return TabSorter.sort(LobbyManager.getInstance().getGroups(), strings[1], true);
                } else if (strings.length == 3) {
                    // Suggest time units
                    return TabSorter.sort(Arrays.asList(
                            TimeUnits.MINUTE.name(),
                            TimeUnits.HOUR.name(),
                            TimeUnits.DAY.name(),
                            TimeUnits.WEEK.name(),
                            TimeUnits.MONTH.name(),
                            TimeUnits.MANUAL.name()
                    ), strings[2], true);
                }
            }
        }

        return List.of();
    }
}