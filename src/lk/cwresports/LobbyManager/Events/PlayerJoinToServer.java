package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinToServer implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyManager.getInstance().sendToLobby(player);
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinToServer(), plugin);
    }
}
