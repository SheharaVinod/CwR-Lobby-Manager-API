package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    public static final String NAME = "spawn";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        if (LobbyManager.isBlockedSpawnCommand(player)) {
            // TODO: send massage , "you cant use this command right now."
            return true;
        }

        LobbyManager.getInstance().sendToLobby(player);
        return true;
    }
}
