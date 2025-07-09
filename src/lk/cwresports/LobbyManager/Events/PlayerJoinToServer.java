package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Commands.SpawnCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinToServer implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyManager.getInstance().sendToLobby(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        boolean x = from.getX() != to.getX();
        boolean y = from.getY() != to.getY();
        boolean z = from.getZ() != to.getZ();

        if (x || y || z) {
            SpawnCommand.canselMovedPlayer(player);
        }
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinToServer(), plugin);
    }
}
