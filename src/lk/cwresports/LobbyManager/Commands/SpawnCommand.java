package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand implements CommandExecutor {
    public static final String NAME = "spawn";
    private static int cool_down = 0;
    public static final String CONFIG_SPAWN_COOL_DOWN = "spawn-command-cool-down-in-sec";
    private static Plugin plugin;

    public SpawnCommand(Plugin plugin) {
        SpawnCommand.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        if (LobbyManager.isBlockedSpawnCommand(player)) {
            // TODO: send massage , "you cant use this command right now."
            player.sendMessage(TextStrings.colorize("you cant use this command right now."));
            return true;
        }

        if (cool_down == 0 || plugin == null) {
            LobbyManager.getInstance().sendToLobby(player);
        } else {
            new BukkitRunnable() {
                int sec = cool_down;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    player.sendMessage(TextStrings.colorize("You will teleported in " + sec + " sec."));
                    if (sec == 0) {
                        LobbyManager.getInstance().sendToLobby(player);
                    }
                    sec--;
                }
            }.runTaskTimer(plugin, 0, cool_down * 20L);
        }

        return true;
    }

    public static void set_cool_down(int cool_down) {
        if (cool_down < 0) {
            cool_down = 0;
        }
        SpawnCommand.cool_down = cool_down;
        if (plugin == null) return;
        plugin.getConfig().set("spawn-command-cool-down-in-sec", cool_down);
    }

    public static void set_cool_down(int cool_down, boolean save) {
        if (cool_down < 0) {
            cool_down = 0;
        }
        SpawnCommand.cool_down = cool_down;
        if (plugin == null || !save) return;
        plugin.getConfig().set("spawn-command-cool-down-in-sec", cool_down);
    }
}
