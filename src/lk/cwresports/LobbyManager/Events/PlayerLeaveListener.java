package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Commands.LobbyManagerCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LobbyManager.unBlockSpawnCommand(player);

        if (LobbyManagerCommand.isAdmin(player)) {
            LobbyManagerCommand.removeAdmin(player);
        }
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), plugin);
    }
}
