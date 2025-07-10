package lk.cwresports.LobbyManager.Tabs;

import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class SelectSpawnTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return LobbyManager.getInstance().getGroups();
        }
        return List.of();
    }
}
