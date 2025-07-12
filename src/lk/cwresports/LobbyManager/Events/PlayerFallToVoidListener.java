package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;


public class PlayerFallToVoidListener implements Listener {

    private static Plugin plugin;

    @EventHandler
    public void onPlayerVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!LobbyManager.getInstance().isInALobby(player)) {
            return;
        }

        double current_y = event.getTo().getY();

        double possible_y = plugin.getConfig().getDouble("auto-teleport-back-to-spawn-when-y-level", -300);

        if (possible_y > current_y) {
            LobbyManager.getInstance().sendToLobby(player);
        }
    }

    public static void register(Plugin plugin) {
        PlayerFallToVoidListener.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new PlayerFallToVoidListener(), plugin);
    }

}
