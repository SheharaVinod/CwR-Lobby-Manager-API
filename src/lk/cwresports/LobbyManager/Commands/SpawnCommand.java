package lk.cwresports.LobbyManager.Commands;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Utils.TextStrings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpawnCommand implements CommandExecutor {
    public static final String NAME = "spawn";
    private static int cool_down = 0;
    public static final String CONFIG_SPAWN_COOL_DOWN = "spawn-command-cool-down-in-sec";
    public static final String CONFIG_SHOULD_AFK = "should-afk-for-teleport";
    private static Plugin plugin;
    private static final Set<Player> que = new HashSet<>();
    private static final Map<Player, BukkitRunnable> que_runnable = new HashMap<>();


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

        if (que.contains(player)) return true;

        if (cool_down == 0 || plugin == null) {
            que.add(player);
            sendToLobby(player);
        } else {
            BukkitRunnable que_runnable_ = new BukkitRunnable() {
                int sec = cool_down;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (sec == 0) {
                        sendToLobby(player);
                    }
                    player.sendMessage(TextStrings.colorize("&7You will teleported in &f" + sec + "&7 sec."));
                    sec--;
                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();
                    que.remove(player);
                }
            };

            que_runnable.put(player, que_runnable_);
            que_runnable_.runTaskTimer(plugin, 0, 20L);
        }

        return true;
    }

    private void sendToLobby(Player player) {
        que.remove(player);
        LobbyManager.getInstance().sendToLobby(player);
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

    public static void canselMovedPlayer(Player player) {
        boolean should_afk = plugin.getConfig().getBoolean(CONFIG_SHOULD_AFK, true);
        if (!should_afk) return;

        BukkitRunnable queRunnable = que_runnable.get(player);
        if (queRunnable != null) {
            queRunnable.cancel();
        }
        que.remove(player);
        player.sendMessage(TextStrings.colorize("&7Teleportation cancelled."));
    }

    public static void removeQue(Player player) {
        BukkitRunnable runnable = que_runnable.get(player);
        if (runnable != null && runnable.getTaskId() != -1) {
            runnable.cancel();
        }
        que.remove(player);
    }
}
